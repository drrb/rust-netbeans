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
package com.github.drrb.rust.netbeans.cargo;

import com.github.drrb.rust.netbeans.project.RustProject;
import com.github.drrb.rust.netbeans.test.NetbeansWithRust;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.openide.filesystems.FileObject;

/**
 *
 */
public class CargoConfigTest {

    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();
    @Rule
    public final NetbeansWithRust netbeans = new NetbeansWithRust();

    @Test
    public void shouldFindCratesInCargoConfig() throws Exception {
        netbeans.checkCrates("crates/types");
    }

    @Test
    public void shouldFindCratesForEachSource() throws Exception {
        netbeans.checkCrates("crates/dependencies");
    }

    @Test
    public void shouldFindModuleForObviousPath() throws Exception {
        RustProject project = netbeans.getTestProject("crates/dependencies");
        FileObject targetFile = project.getProjectDirectory().getFileObject("src/other/third.rs");
        String moduleName = project.getCargoConfig().getModuleName(targetFile);
        assertThat(moduleName, is("other::third"));
    }

    @Test
    public void shouldFindModuleForModPath() throws Exception {
        RustProject project = netbeans.getTestProject("crates/dependencies");
        FileObject targetFile = project.getProjectDirectory().getFileObject("src/other/mod.rs");
        String moduleName = project.getCargoConfig().getModuleName(targetFile);
        assertThat(moduleName, is("other"));
    }

    @Test
    public void shouldFindModuleForRootSourceFile() throws Exception {
        RustProject project = netbeans.getTestProject("crates/dependencies");
        FileObject targetFile = project.getProjectDirectory().getFileObject("src/main.rs");
        String moduleName = project.getCargoConfig().getModuleName(targetFile);
        assertThat(moduleName, is(""));
    }

    @Test
    public void shouldFindModuleForOtherTopLevelFile() throws Exception {
        RustProject project = netbeans.getTestProject("crates/dependencies");
        FileObject targetFile = project.getProjectDirectory().getFileObject("src/toplevelmod.rs");
        String moduleName = project.getCargoConfig().getModuleName(targetFile);
        assertThat(moduleName, is("toplevelmod"));
    }
}
