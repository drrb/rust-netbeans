/*
 * Copyright (C) 2015 drrb
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

import java.io.File;
import org.junit.Test;
import org.junit.Before;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 */
public class CargoTest {
    private Cargo cargo;
    private Shell shell;
    private RustProject project;

    @Before
    public void setUp() {
        project = mock(RustProject.class);
        shell = mock(Shell.class);
        cargo = new Cargo(shell, project);
    }

    @Test
    public void shouldRunCargoCommandsInShell() {
        when(project.dir()).thenReturn(new File("/tmp/myproject"));
        cargo.run("clean", "build");
        verify(shell).run("cargo clean --verbose && cargo build --verbose", new File("/tmp/myproject"));
    }

}
