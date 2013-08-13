/**
 * Copyright (C) 2013 drrb
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.drrb.rust.netbeans.project;

import java.beans.PropertyChangeListener;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.spi.project.ProjectState;
import org.netbeans.spi.project.ui.LogicalViewProvider;
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
class RustProject implements Project {

    public static final String TYPE = "com-github-drrb-rust-netbeans-project";
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

    @Override
    public Lookup getLookup() {
        return Lookups.fixed(
                this, // So people can cast it without casting it, you know?
                new Info(),
                new LogicalView(),
                new RustProjectActionProvider());
    }

    private class Info implements ProjectInformation {

        @StaticResource
        private static final String RUST_PROJECT_ICON = "com/github/drrb/rust/netbeans/project/rust_project_icon_small.png";

        @Override
        public String getName() {
            return projectDirectory.getName();
        }

        @Override
        public String getDisplayName() {
            return getName();
        }

        @Override
        public Icon getIcon() {
            return new ImageIcon(ImageUtilities.loadImage(RUST_PROJECT_ICON));
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
