/**
 * Copyright (C) 2017 drrb
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

import com.github.drrb.rust.netbeans.RustLanguage;
import com.github.drrb.rust.netbeans.project.logicalview.RustProjectNode;
import com.github.drrb.rust.netbeans.cargo.CargoConfig;
import com.github.drrb.rust.netbeans.project.action.RustProjectActionProvider;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;

import com.github.drrb.rust.netbeans.sources.RustSourceGroup;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.project.*;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
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

    public Map<String, ClassPath[]> getClassPaths() {
        Sources sources = ProjectUtils.getSources(this);
        SourceGroup[] projectSourceGroups = sources.getSourceGroups(RustSourceGroup.NAME);
        FileObject[] projectSourceGroupRoots = getRoots(projectSourceGroups);
        ClassPath projectClassPath = ClassPathSupport.createClassPath(projectSourceGroupRoots);
        ClassPath[] projectClassPaths = {projectClassPath};
        Map<String, ClassPath[]> classpaths = new HashMap<>();
        //TODO: add Rust library paths too, when the parser is fast enough
        classpaths.put(RustLanguage.SOURCE_CLASSPATH_ID, projectClassPaths);
        return classpaths;
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

    public CargoConfig getCargoConfig() {
        return new CargoConfig(projectDirectory);
    }

    private static FileObject[] getRoots(SourceGroup[] sourceGroups) {
        FileObject[] roots = new FileObject[sourceGroups.length];
        for (int i = 0; i < sourceGroups.length; i++) {
            SourceGroup sourceGroup = sourceGroups[i];
            roots[i] = sourceGroup.getRootFolder();
        }
        return roots;
    }

    private class Info implements ProjectInformation {

        @Override
        public String getName() {
            return projectDirectory.getName();
        }

        @Override
        public String getDisplayName() {
            String packageName = getCargoConfig().getPackageName();
            if (packageName == null) {
                LOGGER.log(Level.WARNING, "Failed to load project name from Cargo config");
                packageName = getName();
            }
            return packageName;
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
