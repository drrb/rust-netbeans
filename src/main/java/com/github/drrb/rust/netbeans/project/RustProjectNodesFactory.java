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

import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

@NodeFactory.Registration(projectType = RustProject.TYPE, position = 10)
public class RustProjectNodesFactory implements NodeFactory {

    @Override
    public NodeList<?> createNodes(Project project) {
        RustProject p = project.getLookup().lookup(RustProject.class);
        assert p != null; //TODO: maybe we'd be better off just casting it, or is there something else going on in the line above?
        return new RustProjectNodeList(p);
    }

    private class RustProjectNodeList implements NodeList<Node> {

        final RustProject project;

        public RustProjectNodeList(RustProject project) {
            this.project = project;
        }

        @Override
        public List<Node> keys() {
            List<Node> keys = new ArrayList<Node>();
            FileObject projectDirectory = project.getProjectDirectory();
            FileObject[] files = projectDirectory.getChildren();
            for (FileObject file : files) {
                try {
                    keys.add(DataObject.find(file).getNodeDelegate());
                } catch (DataObjectNotFoundException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }

            return keys;
        }

        @Override
        public Node node(Node node) {
            return new FilterNode(node);
        }

        @Override
        public void addNotify() {
        }

        @Override
        public void removeNotify() {
        }

        @Override
        public void addChangeListener(ChangeListener cl) {
        }

        @Override
        public void removeChangeListener(ChangeListener cl) {
        }
    }
}