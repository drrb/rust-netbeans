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
package com.github.drrb.rust.netbeans.project.logicalview;

import com.github.drrb.rust.netbeans.sources.RustSourceGroup;
import com.github.drrb.rust.netbeans.project.RustProject;
import java.awt.Image;
import java.util.Arrays;
import java.util.List;
import javax.swing.Icon;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;

@NodeFactory.Registration(projectType = RustProject.TYPE, position = 10)
public class SourcesNodeFactory implements NodeFactory {

    @Override
    public NodeList<?> createNodes(Project project) {
        return new SourcesNodeList((RustProject) project);
    }

    private static class SourcesNodeList extends AbstractNodeList<SourceGroup> {

        final RustProject project;

        SourcesNodeList(RustProject project) {
            this.project = project;
        }

        @Override
        public List<SourceGroup> keys() {
            Sources sources = ProjectUtils.getSources(project);
            SourceGroup[] rustGroup = sources.getSourceGroups(RustSourceGroup.NAME);
            return Arrays.asList(rustGroup);
        }

        @Override
        public Node node(final SourceGroup group) {
            try {
                FilterNode node = new SourcesNode(group);
                return node;
            } catch (DataObjectNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }

        private class SourcesNode extends FilterNode {

            @StaticResource
            private static final String PACKAGE_BADGE = "com/github/drrb/rust/netbeans/resources/packageBadge.gif";
            private final SourceGroup group;

            SourcesNode(SourceGroup group) throws DataObjectNotFoundException {
                this(DataObject.find(group.getRootFolder()).getNodeDelegate(), group);
            }

            SourcesNode(Node original, SourceGroup group) {
                super(original);
                this.group = group;
            }

            @Override
            public String getDisplayName() {
                return group.getDisplayName();
            }

            @Override
            public Image getIcon(int type) {
                return getIcon(false, type);
            }

            @Override
            public Image getOpenedIcon(int type) {
                return getIcon(true, type);
            }

            private Image getIcon(boolean opened, int type) {
                Icon icon = group.getIcon(opened);
                if (icon == null) {
                    Image image = opened ? getDataFolderNodeDelegate().getOpenedIcon(type) : getDataFolderNodeDelegate().getIcon(type);
                    return ImageUtilities.mergeImages(image, ImageUtilities.loadImage(PACKAGE_BADGE), 7, 7);
                } else {
                    return ImageUtilities.icon2Image(icon);
                }
            }

            private Node getDataFolderNodeDelegate() {
                DataFolder folder = getLookup().lookup(DataFolder.class);
                try {
                    if (folder.isValid()) {
                        return folder.getNodeDelegate();
                    }
                } catch (IllegalStateException exception) {
                    //The data systems API is not thread safe,
                    //the DataObject may become invalid after isValid call and before
                    //getNodeDelegate call, we have to catch the ISE. When the DataObject
                    //is valid - other cause rethrow it otherwise return leaf node.
                    if (folder.isValid()) {
                        throw exception;
                    }
                }
                return new AbstractNode(Children.LEAF);
            }
        }
    }
}
