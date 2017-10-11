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

import com.github.drrb.rust.netbeans.test.NetbeansWithRust;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;
import static org.hamcrest.Matchers.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectUtils;

/**
 *
 */
public class RustProjectTest {

    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();
    @Rule
    public final NetbeansWithRust netbeans = new NetbeansWithRust();
    private Project project;

    @Before
    public void setUp() throws Exception {
        project = netbeans.getTestProject("project/simple");
    }

    @Test
    public void shouldBeInItsOwnLookup() {
        Project projectInLookup = project.getLookup().lookup(Project.class);

        assertThat(projectInLookup, is(project));
    }

    @Test
    public void shouldProvideProjectInfo() {
        ProjectInformation info = ProjectUtils.getInformation(project);

        assertThat(info.getProject(), is(project));
    }

    @Test
    public void shouldNameProjectAfterDirectory() {
        ProjectInformation info = ProjectUtils.getInformation(project);

        assertThat(info.getName(), is("simple"));
    }

    @Test
    public void shouldDisplayTheProjectNameFromTheCargoConfig() {
        ProjectInformation info = ProjectUtils.getInformation(project);

        assertThat(info.getDisplayName(), is("Test Rust Project"));
    }

    @Test
    public void shouldDisplayTheRustProjectIcon() {
        ProjectInformation info = ProjectUtils.getInformation(project);

        assertThat(info.getIcon().getIconWidth(), is(16));
        assertThat(info.getIcon().getIconHeight(), is(16));
    }

    @Test
    public void shouldFallBackToDirNameIfNoPackageNameInConfig() throws Exception {
        netbeans.checkLogicalView("project/noname");
    }

    @Test
    public void shouldDisplaySourcesHierarchyInSourceLogicalView() throws Exception {
        netbeans.checkLogicalView("project/simple");
    }
}
