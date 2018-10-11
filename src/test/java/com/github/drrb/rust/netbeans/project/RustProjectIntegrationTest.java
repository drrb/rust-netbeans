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

import java.util.Arrays;
import junit.framework.Test;
import org.netbeans.jellytools.JellyTestCase;
import org.netbeans.jellytools.ProjectsTabOperator;
import org.netbeans.jellytools.nodes.ProjectRootNode;
import org.netbeans.junit.NbModuleSuite;

/**
 *
 */
public class RustProjectIntegrationTest extends JellyTestCase {

    public static Test suite() {
        return NbModuleSuite.allModules(RustProjectIntegrationTest.class);
    }

    public RustProjectIntegrationTest(String testName) {
        super(testName);
    }

    public void testOpenProject() throws Exception {
        openDataProjects("projects/simplerustproject");
        ProjectsTabOperator projectsTabOperator = new ProjectsTabOperator();
        ProjectRootNode projectRootNode = projectsTabOperator.getProjectRootNode("simplerustproject");

        assertEquals(projectRootNode.getText(), "simple-rust-project");
        assertArrayEquals(projectRootNode.getChildren(), new String[]{"main.rs"});
    }

    protected <T> void assertArrayEquals(T[] expected, T[] actual) {
        String error = String.format("Expected %s, but got %s", Arrays.toString(expected), Arrays.toString(actual));
        assertTrue(error, Arrays.equals(expected, actual));
    }
}
