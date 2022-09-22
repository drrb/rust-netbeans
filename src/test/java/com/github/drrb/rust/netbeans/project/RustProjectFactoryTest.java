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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.openide.filesystems.FileObject;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.spi.project.ProjectState;

/**
 *
 */
public class RustProjectFactoryTest {

    private FileObject projectFolder = mock(FileObject.class);
    private ProjectState projectState=mock(ProjectState.class);
    private RustProjectFactory factory;

    @Before
    public void setUp() {
        factory = new RustProjectFactory();
    }

    @Test
    public void shouldNotIdentifyEmptyFolderAsProject() {
        boolean projectDetected = factory.isProject(projectFolder);
        assertThat(projectDetected, is(false));
    }

    @Test
    public void shouldIdentifyAProjectWithACargoFile() {
        when(projectFolder.getFileObject("Cargo.toml")).thenReturn(aFile());

        boolean projectDetected = factory.isProject(projectFolder);

        assertThat(projectDetected, is(true));
    }

    @Test
    public void shouldNotLoadAProjectIfItIsntARustProjectDirectory() throws Exception {
        Project project = factory.loadProject(projectFolder, projectState);

        assertThat(project, is(nullValue()));
    }

    @Test
    public void shouldLoadAProjectFromAProjectDirectory() throws Exception {
        projectFolder = aProject();
        Project project = factory.loadProject(projectFolder, projectState);

        assertThat(project, is(not(nullValue())));
    }

    @Test
    public void shouldReturnAnIconWhenDirectoryIsProject() {
        when(projectFolder.getFileObject("Cargo.toml")).thenReturn(aFile());

        ProjectManager.Result result = factory.isProject2(projectFolder);

        assertThat(result.getIcon(), is(not(nullValue())));
    }

    @Test
    public void shouldReturnNullWhenAnIconWhenDirectoryIsNotAProject() {
        ProjectManager.Result result = factory.isProject2(projectFolder);

        assertThat(result, is(nullValue()));
    }

    private FileObject aFile() {
        return mock(FileObject.class);
    }

    private FileObject aProject() {
        FileObject folder = mock(FileObject.class);
        when(folder.getFileObject("Cargo.toml")).thenReturn(aFile());
        return folder;
    }
}
