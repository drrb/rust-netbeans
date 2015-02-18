/**
 * Copyright (C) 2015 drrb
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.drrb.rust.netbeans.project;

import com.github.drrb.rust.netbeans.rustbridge.RustCrateType;
import com.moandjiezana.toml.Toml;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.spi.project.ProjectState;
import org.netbeans.spi.project.support.LookupProviderSupport;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.netbeans.spi.project.ui.support.UILookupMergerSupport;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 */
public class RustProject implements Project {

    private static final Logger LOGGER = Logger.getLogger(RustProject.class.getName());
    public static final String TYPE = "com-github-drrb-rust-netbeans-project";
    // Has things in it that are registered as @ProjectServiceProviders (e.g. Sources implementations)
    private static final String PROJECT_LOOKUP_NAME = String.format("Projects/%s/Lookup", TYPE);
    @StaticResource
    public static final String RUST_PROJECT_ICON = "com/github/drrb/rust/netbeans/rust-icon_16x16.png";
    private final FileObject projectDirectory;
    private final ProjectState state;

    public RustProject(FileObject projectDirectory, ProjectState state) {
        this.projectDirectory = projectDirectory;
        this.state = state;
    }

    @Override
    public FileObject getProjectDirectory() {
        return projectDirectory;
    }

    public File dir() {
        return new File(projectDirectory.getPath());
    }

    @Override
    public Lookup getLookup() {
        Lookup baseLookup = Lookups.fixed(
                this, // So people can cast it without casting it, you know?
                new Info(),
                new LogicalView(),
                new RustProjectActionProvider(this),
                LookupProviderSupport.createSourcesMerger(), // Gets implementations of Sources from named lookup below
                //TODO: saw this in Maven project. What does it do?
                //ProjectClassPathModifier.extenderForModifier(this), // Gets implementations of ProjectClassPathModifierImplementation from the named lookup below
                UILookupMergerSupport.createProjectOpenHookMerger(null) // Gets implementations of ProjectOpenedHook from named lookup below
                );
        // Provides Mergers in the base lookup with implementations from the Projects/TYPE/Lookup lookup (e.g. Sources implementations)
        return LookupProviderSupport.createCompositeLookup(baseLookup, PROJECT_LOOKUP_NAME);
    }

    private Toml getCargoConfig() {
        FileObject cargoFile = projectDirectory.getFileObject("Cargo.toml");
        if (cargoFile == null) {
            return new Toml();
        }
        try {
            return new Toml().parse(new InputStreamReader(cargoFile.getInputStream(), UTF_8));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RustProject.class.getName()).log(Level.WARNING, "Couldn't read crates from Cargo.toml", ex);
            return new Toml();
        }
    }

    public List<Crate> getCrates() {
        Toml cargoConfig = getCargoConfig();

        List<Crate> crates = new LinkedList<>();
        Toml libCrate = cargoConfig.getTable("lib");
        String libCratePath = libCrate.getString("path");
        if (libCratePath != null) {
            FileObject crateFile = projectDirectory.getFileObject(libCratePath);
            List<String> libCrateTypes = libCrate.getList("crate-type", String.class);
            if (libCrateTypes.isEmpty()) {
                libCrateTypes.add("rlib");
            }
            for (String libCrateType : libCrateTypes) {
                if (libCrateType.equals("dylib")) {
                    crates.add(new Crate(RustCrateType.DYLIB, crateFile));
                } else if (libCrateType.equals("rlib")) {
                    crates.add(new Crate(RustCrateType.RLIB, crateFile));
                } else if (libCrateType.equals("staticlib")) {
                    crates.add(new Crate(RustCrateType.STATICLIB, crateFile));
                }
            }
        }

        List<Toml> binCrates = cargoConfig.getTables("bin");
        for (Toml binCrate : binCrates) {
            String cratePath = binCrate.getString("path");
            FileObject crateFile = projectDirectory.getFileObject(cratePath);
            crates.add(new Crate(RustCrateType.EXECUTABLE, crateFile));
        }
        return crates;
    }

    private class Info implements ProjectInformation {

        @Override
        public String getName() {
            return projectDirectory.getName();
        }

        @Override
        public String getDisplayName() {
            FileObject cargoFile = projectDirectory.getFileObject("Cargo.toml");
            if (cargoFile == null) return getName();
            try {
                Toml cargoConf = new Toml().parse(cargoFile.asText("UTF-8"));
                return cargoConf.getTable("package").getString("name");
            } catch (IOException | IllegalStateException ex) {
                LOGGER.log(Level.WARNING, "Failed to load project name from Cargo config: {}", ex);
                return getName();
            }
        }

        @Override
        public Icon getIcon() {
            return ImageUtilities.loadImageIcon(RUST_PROJECT_ICON, false);
        }

        @Override
        public Project getProject() {
            return RustProject.this;
        }

        @Override
        public void addPropertyChangeListener(PropertyChangeListener listener) {
        }

        @Override
        public void removePropertyChangeListener(PropertyChangeListener listener) {
        }
    }

    private class LogicalView implements LogicalViewProvider {

        @Override
        public Node createLogicalView() {
            //TODO: test this!
            //TODO: remove these comments if necessary
            try {
                //Obtain the project directory's node:
                FileObject projectDirectory = getProjectDirectory();
                DataFolder projectFolder = DataFolder.findFolder(projectDirectory);
                Node nodeOfProjectFolder = projectFolder.getNodeDelegate();
                //Decorate the project directory's node:
                return new RustProjectNode(nodeOfProjectFolder, RustProject.this);
            } catch (DataObjectNotFoundException e) {
                Exceptions.printStackTrace(e);
                //Fallback-the directory couldn't be created -
                //read-only filesystem or something evil happened
                return new AbstractNode(Children.LEAF);
            }
        }

        @Override
        public Node findPath(Node root, Object target) {
            return null;
        }
    }
}
