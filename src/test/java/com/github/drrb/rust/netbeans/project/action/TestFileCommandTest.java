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
package com.github.drrb.rust.netbeans.project.action;

import com.github.drrb.rust.netbeans.cargo.Cargo;
import com.github.drrb.rust.netbeans.cargo.test.TestRunner;
import com.github.drrb.rust.netbeans.project.RustProject;
import com.github.drrb.rust.netbeans.test.NetbeansWithRust;
import org.junit.Test;
import org.junit.Rule;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

public class TestFileCommandTest {
    @Rule
    public final NetbeansWithRust netbeans = new NetbeansWithRust();
    private TestRunner.Factory testRunnerFactory = mock(TestRunner.Factory.class);
    private TestRunner testRunner = mock(TestRunner.class);
    private Cargo cargo = mock(Cargo.class);

    @Test
    public void shouldRunCargoTestForFile() throws Exception {
        RustProject project = netbeans.getTestProject("crates/dependencies");
        FileObject sourceFile = project.getProjectDirectory().getFileObject("src/main.rs");
        Lookup context = Lookups.fixed(cargo, project, sourceFile);
        when(testRunnerFactory.create(project, cargo)).thenReturn(testRunner);
        new TestFileCommand(testRunnerFactory).run(context);
        verify(testRunner).run(sourceFile);
    }

}