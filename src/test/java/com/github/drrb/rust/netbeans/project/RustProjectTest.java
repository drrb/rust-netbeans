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

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.netbeans.spi.project.ProjectState;
import org.openide.filesystems.FileObject;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.spi.project.ui.LogicalViewProvider;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class RustProjectTest {

    @Mock
    private FileObject projectDirectory;
    @Mock
    private ProjectState projectState;
    private Project project;

    @Before
    public void setUp() {
        project = new RustProject(projectDirectory, projectState);
    }

    @Test
    public void shouldReturnProjectFolder() {
        assertThat(project.getProjectDirectory(), is(projectDirectory));
    }

    @Test
    public void shouldProvideProjectInfo() {
        ProjectInformation info = ProjectUtils.getInformation(project);

        assertThat(info.getProject(), is(project));
    }

    @Test
    public void shouldNameProjectAfterDirectory() {
        when(projectDirectory.getName()).thenReturn("myrustproject");
        ProjectInformation info = ProjectUtils.getInformation(project);

        assertThat(info.getName(), is("myrustproject"));
    }

    @Test
    public void shouldDisplayTheProjectName() {
        when(projectDirectory.getName()).thenReturn("myrustproject");
        ProjectInformation info = ProjectUtils.getInformation(project);

        assertThat(info.getDisplayName(), is("myrustproject"));
    }

    @Test
    public void shouldDisplayTheRustProjectIcon() {
        when(projectDirectory.getName()).thenReturn("myrustproject");
        ProjectInformation info = ProjectUtils.getInformation(project);

        assertThat(info.getIcon().getIconWidth(), is(16));
        assertThat(info.getIcon().getIconHeight(), is(16));
    }

    @Test
    public void shouldHaveLogicalViewProvider() {
        LogicalViewProvider logicalViewProvider = project.getLookup().lookup(LogicalViewProvider.class);
        assertThat(logicalViewProvider, is(not(nullValue())));
    }
}
