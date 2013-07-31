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
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openide.filesystems.FileObject;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class RustProjectFactoryTest {

    @Mock
    private FileObject folder;
    private RustProjectFactory factory;

    @Before
    public void setUp() {
        factory = new RustProjectFactory();
    }

    @Test
    public void shouldNotIdentifyEmptyFolderAsProject() {
        boolean projectDetected = factory.isProject(folder);
        assertThat(projectDetected, is(false));
    }

    @Test
    public void shouldIdentifyAProjectWithAMainFile() {
        when(folder.getFileObject("main.rs")).thenReturn(aFile());

        boolean projectDetected = factory.isProject(folder);

        assertThat(projectDetected, is(true));
    }

    @Test
    public void shouldIdentifyAProjectWithALibFile() {
        when(folder.getFileObject("lib.rs")).thenReturn(aFile());

        boolean projectDetected = factory.isProject(folder);

        assertThat(projectDetected, is(true));
    }

    @Test
    public void shouldIdentifyAProjectWithATestFile() {
        when(folder.getFileObject("test.rs")).thenReturn(aFile());

        boolean projectDetected = factory.isProject(folder);

        assertThat(projectDetected, is(true));
    }

    @Test
    public void shouldIdentifyAProjectWithABenchFile() {
        when(folder.getFileObject("bench.rs")).thenReturn(aFile());

        boolean projectDetected = factory.isProject(folder);

        assertThat(projectDetected, is(true));
    }

    @Test
    public void shouldIdentifyAProjectWithAPackageScript() {
        when(folder.getFileObject("pkg.rs")).thenReturn(aFile());

        boolean projectDetected = factory.isProject(folder);

        assertThat(projectDetected, is(true));
    }

    private FileObject aFile() {
        return mock(FileObject.class);
    }
}