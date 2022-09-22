/**
 * Copyright (C) 2017 drrb This program is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version. This program is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU General Public License for more details. You should have received a
 * copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package com.github.drrb.rust.netbeans.classpath;

import static com.github.drrb.rust.netbeans.RustLanguage.SOURCE_CLASSPATH_ID;
import com.github.drrb.rust.netbeans.project.RustProject;
import static com.github.drrb.rust.netbeans.test.Matchers.matchesRegex;
import static com.github.drrb.rust.netbeans.test.TestData.getData;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import static org.hamcrest.Matchers.hasItemInArray;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mockito;
import static org.mockito.Mockito.verify;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.classpath.GlobalPathRegistry;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 */
// 
public class ClasspathSettingProjectOpenedHookTest {

    private RustProject project;

    private GlobalPathRegistry globalPathRegistry;

    private ClasspathSettingProjectOpenedHook hook;

    @Before
    public void setUp() {
        FileObject projectDirectory = FileUtil
                .toFileObject(getData("ClasspathSettingProjectOpenedHookTest/testrustproject"));
        globalPathRegistry = Mockito.mock(GlobalPathRegistry.class);
        project = new RustProject(projectDirectory, null);
        hook = new ClasspathSettingProjectOpenedHook(project, globalPathRegistry);
    }

    @Test
    public void shouldRegisterSourceDirectoryAsClasspathRoot() {
        hook.projectOpened();

        verify(globalPathRegistry).register(eq(SOURCE_CLASSPATH_ID),
                argThat(hasItemInArray(classpathWithRootThat(matchesRegex(".*testrustproject/src$")))));
    }

    private ClasspathWithRoot classpathWithRootThat(Matcher<String> folder) {
        return new ClasspathWithRoot(folder);
    }

    private class ClasspathWithRoot extends TypeSafeMatcher<ClassPath> {

        private final Matcher<String> folderMatcher;

        private ClasspathWithRoot(Matcher<String> folderMatcher) {
            this.folderMatcher = folderMatcher;
        }

        @Override
        public boolean matchesSafely(ClassPath item) {
            for (FileObject root : item.getRoots()) {
                if (folderMatcher.matches(root.getPath())) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("classpath containing root that ").appendDescriptionOf(folderMatcher);
        }
    }
}
