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

import java.awt.Image;
import java.beans.BeanInfo;
import javax.swing.Action;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.hamcrest.Matchers.*;
import org.netbeans.api.project.ProjectInformation;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Node.class)
public class RustProjectNodeTest {

    @Mock
    Node projectDirectoryNode;
    @Mock
    RustProject project;
    @Mock
    ProjectInformation projectInfo;
    private RustProjectNode projectNode;

    @Before
    public void setUp() throws Exception {
        when(projectDirectoryNode.getChildren()).thenReturn(mock(Children.class));
        when(projectDirectoryNode.getLookup()).thenReturn(mock(Lookup.class));
        when(project.getLookup()).thenReturn(Lookups.fixed(projectInfo));
        projectNode = new RustProjectNode(projectDirectoryNode, project);
    }

    @Test
    public void shouldDisplayProjectNodeWithDisplayName() {
        when(projectInfo.getDisplayName()).thenReturn("my rust project");

        assertThat(projectNode.getDisplayName(), is("my rust project"));
    }

    @Test
    public void shouldDisplayProjectIcon() {
        assertThat(projectNode.getIcon(BeanInfo.ICON_COLOR_16x16), isA(Image.class));
    }

    @Test
    public void shouldHaveActions() {
        Action[] actions = projectNode.getActions(true);

        assertThat(actions, is(not(emptyArray())));
    }
}
