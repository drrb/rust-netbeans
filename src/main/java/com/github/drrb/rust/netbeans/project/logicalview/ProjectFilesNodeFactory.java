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
import java.util.Arrays;
import java.util.List;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;

/**
 *
 */
@NodeFactory.Registration(projectType = RustProject.TYPE, position = 100)
public class ProjectFilesNodeFactory implements NodeFactory {

    @Override
    public NodeList<?> createNodes(final Project project) {
        return new AbstractNodeList<FileObject>() {

            @Override
            public List<FileObject> keys() {
                return Arrays.asList(project.getProjectDirectory().getFileObject("Cargo", "toml"));
            }

            @Override
            public Node node(FileObject key) {
                try {
                    return DataObject.find(key).getNodeDelegate();
                } catch (DataObjectNotFoundException ex) {
                    return null;
                }
            }
        };
    }

}
