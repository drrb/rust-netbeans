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

import com.github.drrb.rust.netbeans.project.RustProject;
import java.awt.Image;
import javax.swing.Action;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.netbeans.spi.project.ui.support.NodeFactorySupport;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

public class RustProjectNode extends FilterNode {

    @StaticResource
    public static final String PROJECT_ICON = "com/github/drrb/rust/netbeans/rust-icon_16x16.png";
    private static final String NODE_FACTORIES_PATH = String.format("Projects/%s/Nodes", RustProject.TYPE);
    private final RustProject project;

    public RustProjectNode(Node projectDirectoryNode, RustProject project) throws DataObjectNotFoundException {
        super(projectDirectoryNode,
                NodeFactorySupport.createCompositeChildren(project, NODE_FACTORIES_PATH),
                new ProxyLookup(Lookups.singleton(project), projectDirectoryNode.getLookup()));
        this.project = project;
    }

    @Override
    public String getDisplayName() {
        return ProjectUtils.getInformation(project).getDisplayName();
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage(PROJECT_ICON);
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

    @Override
    public Action[] getActions(boolean context) {
        return CommonProjectActions.forType(RustProject.TYPE);
    }
}
