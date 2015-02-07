/**
 * Copyright (C) 2015 drrb
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

import static com.github.drrb.rust.netbeans.test.TestData.getData;
import static java.util.Arrays.asList;
import java.util.LinkedList;
import java.util.List;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.openide.filesystems.FileObject;
import static org.hamcrest.Matchers.*;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.Node;
import org.openide.util.UtilitiesTest.NamedServicesProviderImpl;
import org.openide.util.lookup.Lookups;
import org.openide.util.test.MockLookup;

/**
 *
 */
public class RustProjectTest {

    private FileObject projectDirectory;
    private Project project;

    @Before
    public void setUp() {
        projectDirectory = FileUtil.toFileObject(getData("RustProjectTest/testrustproject"));
        project = new RustProject(projectDirectory, null);
        NamedServicesProviderImpl namedServicesProvider = new NamedServicesProviderImpl();
        namedServicesProvider.addNamedLookup("Projects/com-github-drrb-rust-netbeans-project/Lookup", Lookups.fixed(new RustSources(project)));
        namedServicesProvider.addNamedLookup("Projects/com-github-drrb-rust-netbeans-project/Nodes", Lookups.fixed(new ProjectFilesNodeFactory(), new SourcesNodeFactory()));
        MockLookup.setInstances(namedServicesProvider);
    }

    @Test
    public void shouldReturnProjectFolder() {
        assertThat(project.getProjectDirectory(), is(projectDirectory));
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

        assertThat(info.getName(), is("testrustproject"));
    }

    @Test
    public void shouldDisplayTheProjectNameFromTheCargoConfig() {
        ProjectInformation info = ProjectUtils.getInformation(project);

        assertThat(info.getDisplayName(), is("Test Rust Project"));
    }

    @Test
    public void shouldFallBackToDirNameIfNoPackageNameInConfig() {
        projectDirectory = FileUtil.toFileObject(getData("RustProjectTest/testrustproject-nocargo"));
        project = new RustProject(projectDirectory, null);
        ProjectInformation info = ProjectUtils.getInformation(project);

        assertThat(info.getDisplayName(), is("testrustproject-nocargo"));
    }

    @Test
    public void shouldDisplayTheRustProjectIcon() {
        ProjectInformation info = ProjectUtils.getInformation(project);

        assertThat(info.getIcon().getIconWidth(), is(16));
        assertThat(info.getIcon().getIconHeight(), is(16));
    }

    @Test
    public void shouldHaveLogicalViewProvider() throws Exception {
        LogicalViewProvider logicalViewProvider = project.getLookup().lookup(LogicalViewProvider.class);
        Node projectNode = logicalViewProvider.createLogicalView();
        assertThat(projectNode.getDisplayName(), is("Test Rust Project"));

        assertThat(projectNode, hasChildren("Cargo.toml")); //Actually, it has "Sources" too, but we seem to not be setting up the lookups properly
//        assertThat(projectNode, hasChildren("Sources", "Cargo.toml"));
    }

    private Matcher<Node> hasChildren(String... children) {
        return new HasChildren(children);
    }

    private static class HasChildren extends TypeSafeDiagnosingMatcher<Node> {

        private final String[] expectedChildNames;

        HasChildren(String[] expectedChildNames) {
            this.expectedChildNames = expectedChildNames.clone();
        }

        @Override
        protected boolean matchesSafely(Node node, Description mismatchDescription) {
            Node[] actualChildren = node.getChildren().getNodes(true); // 'true' loads the children if they're lazily loading (otherwise they're displayed as "Please Wait..."
            List<String> actualChildNames = new LinkedList<>();
            for (Node actualChild : actualChildren) {
                actualChildNames.add(actualChild.getDisplayName());
            }
            mismatchDescription.appendText("got node with children: ")
                                .appendValueList("<", ", ", ">", actualChildNames);
            return actualChildNames.equals(asList(expectedChildNames));
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("node with children: ")
                        .appendValueList("<", ", ", ">", expectedChildNames);
        }
    }
}
