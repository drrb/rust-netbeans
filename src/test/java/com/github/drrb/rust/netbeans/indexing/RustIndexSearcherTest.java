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
package com.github.drrb.rust.netbeans.indexing;

import com.github.drrb.rust.netbeans.project.RustProject;
import com.github.drrb.rust.netbeans.test.NetbeansWithRust;
import com.github.drrb.rust.netbeans.test.NetbeansWithRust.Project;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.netbeans.modules.csl.api.ElementKind;
import org.netbeans.modules.csl.api.IndexSearcher;

import java.util.Set;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.netbeans.modules.csl.api.ElementKind.CLASS;
import static org.netbeans.modules.parsing.spi.indexing.support.QuerySupport.Kind.CASE_INSENSITIVE_PREFIX;
import static org.netbeans.modules.parsing.spi.indexing.support.QuerySupport.Kind.PREFIX;

public class RustIndexSearcherTest {

    @Rule
    public final NetbeansWithRust netbeans = new NetbeansWithRust();

    @Test
    @Project("index/project/struct")
    public void findsStructsByPrefix() throws Exception {
        RustProject project = netbeans.getProject();

        netbeans.index(project);

        Set<? extends IndexSearcher.Descriptor> searchResults = netbeans.searchIndex(project, "Per", PREFIX);
        assertThat(searchResults, contains(
                searchResult(CLASS, "Person")
                        .withProjectName("Test Rust Project")
                        .withModuleName("xxx")
                        .withFileName("main.rs")
        ));
    }

    @Test
    @Project("index/project/struct")
    public void findsStructsByCaseInsensitivePrefix() throws Exception {
        RustProject project = netbeans.getProject();

        netbeans.index(project);

        Set<? extends IndexSearcher.Descriptor> searchResults = netbeans.searchIndex(project, "per", CASE_INSENSITIVE_PREFIX);
        assertThat(searchResults, contains(
                searchResult(CLASS, "Person")
                        .withProjectName("Test Rust Project")
                        .withModuleName("xxx")
                        .withFileName("main.rs")
        ));
    }

    private SearchResultMatcher searchResult(ElementKind expectedElementKind, String expectedTypeName) {
        return new SearchResultMatcher(expectedElementKind, expectedTypeName);
    }

    private static class SearchResultMatcher extends TypeSafeMatcher<IndexSearcher.Descriptor> {
        private final ElementKind expectedElementKind;
        private final String expectedTypeName;
        private String expectedProjectName;
        private String expectedModuleName;
        private String expectedFileName;

        public SearchResultMatcher(ElementKind expectedElementKind, String expectedTypeName) {
            this.expectedElementKind = expectedElementKind;
            this.expectedTypeName = expectedTypeName;
        }

        @Override
        protected boolean matchesSafely(IndexSearcher.Descriptor actual) {
            return actual.getElement().getKind().equals(expectedElementKind)
                    && actual.getTypeName().equals(expectedTypeName)
                    && actual.getProjectName().equals(expectedProjectName)
                    && actual.getContextName().equals(expectedModuleName)
                    && actual.getFileObject().getNameExt().equals(expectedFileName);
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("index search result with:")
                    .appendText("\n  elementKind: ").appendValue(expectedElementKind)
                    .appendText("\n  typeName: ").appendValue(expectedTypeName)
                    .appendText("\n  projectName: ").appendValue(expectedProjectName)
                    .appendText("\n  moduleName: ").appendValue(expectedModuleName)
                    .appendText("\n  fileName: ").appendValue(expectedFileName)
            ;
        }

        public SearchResultMatcher withProjectName(String expectedProjectName) {
            this.expectedProjectName = expectedProjectName;
            return this;
        }

        public SearchResultMatcher withModuleName(String expectedModuleName) {
            this.expectedModuleName = expectedModuleName;
            return this;
        }

        public SearchResultMatcher withFileName(String expectedFileName) {
            this.expectedFileName = expectedFileName;
            return this;
        }
    }
}