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
package com.github.drrb.rust.netbeans.highlighting;

import com.github.drrb.rust.netbeans.RustSourceSnapshot;
import com.github.drrb.rust.netbeans.parsing.NetbeansRustParser.NetbeansRustParserResult;
import com.github.drrb.rust.netbeans.test.PrintTestMethods;
import java.util.Collection;
import static java.util.Collections.emptyList;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Rule;
import org.netbeans.modules.csl.api.ColoringAttributes;
import static org.netbeans.modules.csl.api.ColoringAttributes.*;
import org.netbeans.modules.csl.api.OffsetRange;

/**
 *
 */
public class RustSemanticAnalyzerTest {

    @Rule
    public final PrintTestMethods methodPrinter = new PrintTestMethods();
    private RustSemanticAnalyzer semanticAnalyzer;
    private final Collection<NetbeansRustParserResult> parseResults = new LinkedList<>();

    @Before
    public void setUp() {
        semanticAnalyzer = new RustSemanticAnalyzer();
    }

    @After
    public void destroyStructs() throws Exception {
        for (NetbeansRustParserResult parseResult : parseResults) {
            parseResult.getResult().destroy();
        }
    }

    @Test
    public void shouldFindFunction() {
        RustSourceSnapshot source = new RustSourceSnapshot();
        source.appendln("/// Entry point");
        source.appendln("fn main() {");
        source.appendln("  return 1");
        source.appendln("}");
        assertThat(source, hasSpan("main").withHighlights(METHOD, STATIC));
    }

    @Test
    public void shouldFindEnumWithConstants() {
        RustSourceSnapshot source = new RustSourceSnapshot();
        source.append("enum Color {");
        source.append("  Red,");
        source.append("  Green,");
        source.append("  Blue,");
        source.append("  Black");
        source.append("}");
        assertThat(source, hasSpan("Color").withHighlights(CLASS));
        assertThat(source, hasSpan("Red").withHighlights(ENUM));
        assertThat(source, hasSpan("Green").withHighlights(ENUM));
        assertThat(source, hasSpan("Blue").withHighlights(ENUM));
        assertThat(source, hasSpan("Black").withHighlights(ENUM));
    }

    @Test
    public void shouldFindStructAndFields() {
        RustSourceSnapshot source = new RustSourceSnapshot();
        source.appendln("struct Point {");
        source.appendln("    x: float,");
        source.appendln("    y: float");
        source.appendln("}");
        assertThat(source, hasSpan("Point").withHighlights(CLASS));
        assertThat(source, hasSpan("x").withHighlights(FIELD));
        assertThat(source, hasSpan("y").withHighlights(FIELD));
    }

    @Test
    public void shouldFindTrait() {
        RustSourceSnapshot source = new RustSourceSnapshot();
        source.appendln("trait Printable {");
        source.appendln("    fn print(&self) { println(fmt!(\"%d\", *self)) }");
        source.appendln("    fn implement_me(&self);");
        source.appendln("}");
        assertThat(source, hasSpan("Printable").withHighlights(INTERFACE));
        assertThat(source, hasSpan("print").withHighlights(METHOD));
        assertThat(source, hasSpan("implement_me").withHighlights(METHOD));
    }

    @Test
    public void shouldFindTraitImplWithMethods() {
        RustSourceSnapshot source = new RustSourceSnapshot();
        source.appendln("impl Printable for int {");
        source.appendln("    fn print(&self) { println(fmt!(\"%d\", *self)) }");
        source.appendln("}");
        assertThat(source, hasSpan("Printable").withHighlights(CLASS));
        assertThat(source, hasSpan("print").withHighlights(METHOD));
    }

    @Test
    public void shouldFindImplWithMethods() {
        RustSourceSnapshot source = new RustSourceSnapshot();
        source.appendln("impl Printable {");
        source.appendln("    fn print(&self) { println(fmt!(\"%d\", *self)) }");
        source.appendln("}");
        assertThat(source, hasSpan("Printable").withHighlights(CLASS));
        assertThat(source, hasSpan("print").withHighlights(METHOD));
    }

    @Test
    public void shouldNotFindTypesInMethod() {
        RustSourceSnapshot source = new RustSourceSnapshot();
        source.appendln("impl Point {");
        source.appendln("    fn transpose(&self, x: float, y: float) -> Point {");
        source.appendln("        let new_x_value = self.x + x;");
        source.appendln("        let new_y_value = self.y + y;");
        source.appendln("        Point{ x: new_x_value, y: new_y_value }");
        source.appendln("    }");
        source.appendln("}");

        assertThat(source, hasSpan("float").withNoHighlights());
    }

    @Test
    public void shouldNotFindIdentifiersInMethodsWhenImplentingStructs() {
        RustSourceSnapshot source = new RustSourceSnapshot();
        source.appendln("impl Printable {");
        source.appendln("    fn print(&self) { println(fmt!(\"%d\", *self)) }");
        source.appendln("}");

        assertThat(source, hasSpan("println").withNoHighlights());
    }

    @Test
    public void shouldNotFindIdentifiersInMethodsWhenImplementingTraits() {
        RustSourceSnapshot source = new RustSourceSnapshot();
        source.appendln("impl Printable for int {");
        source.appendln("    fn print(&self) { println(fmt!(\"%d\", *self)) }");
        source.appendln("}");

        assertThat(source, hasSpan("println").withNoHighlights());
    }

    @Test
    public void shouldNotFindClassesBeingImplementedForTraits() {
        RustSourceSnapshot source = new RustSourceSnapshot();
        source.appendln("impl Renderable for int {");
        source.appendln("    fn print(&self) { println(fmt!(\"%d\", *self)) }");
        source.appendln("}");

        assertThat(source, hasSpan("int").withNoHighlights());
    }

    @Test
    public void shouldNotDieWhenIdentifierNotTypedYet() {
        NetbeansRustParserResult parseResult = new RustSourceSnapshot()
                .append("struct ")
                .parseExpectingFailure();
        parseResults.add(parseResult);
        semanticAnalyzer.run(parseResult, null); // Shouldn't die
    }

    private HighlightMatcher.Builder hasSpan(String snippet) {
        return new HighlightMatcher.Builder(snippet);
    }

    public static class HighlightMatcher extends TypeSafeDiagnosingMatcher<RustSourceSnapshot> {

        public static class Builder {

            private final String expectedSnippet;

            private Builder(String expectedSnippet) {
                this.expectedSnippet = expectedSnippet;
            }

            public HighlightMatcher withAnyHighlights() {
                return withHighlights(anything());
            }

            public HighlightMatcher withNoHighlights() {
                return withHighlights(emptyIterable());
            }

            public HighlightMatcher withHighlights(ColoringAttributes... colors) {
                return withHighlights(containsInAnyOrder((Object[]) colors));
            }

            public HighlightMatcher withHighlights(Matcher<? super Iterable<? extends ColoringAttributes>> expectedHighlights) {
                return new HighlightMatcher(expectedSnippet, expectedHighlights);
            }
        }

        private final String expectedSnippet;
        private final Matcher<? super Iterable<? extends ColoringAttributes>> expectedHighlights;

        private HighlightMatcher(String expectedSnippet, Matcher<? super Iterable<? extends ColoringAttributes>> expectedHighlights) {
            this.expectedSnippet = expectedSnippet;
            this.expectedHighlights = expectedHighlights;
        }

        @Override
        protected boolean matchesSafely(RustSourceSnapshot actualSnapshot, Description mismatchDescription) {
            OffsetRange expectedOffsetRange = actualSnapshot.spanOf(expectedSnippet);
            NetbeansRustParserResult parseResult = actualSnapshot.parse();
            RustSemanticAnalyzer rustSemanticAnalyzer = new RustSemanticAnalyzer();
            rustSemanticAnalyzer.run(parseResult, null);
            Map<OffsetRange, Set<ColoringAttributes>> highlights = rustSemanticAnalyzer.getHighlights();
            mismatchDescription.appendText("got highlights ");
            for (Map.Entry<OffsetRange, Set<ColoringAttributes>> highlight : highlights.entrySet()) {
                OffsetRange span = highlight.getKey();
                Set<ColoringAttributes> colors = highlight.getValue();
                mismatchDescription.appendValue(span);
                mismatchDescription.appendText(" (").appendValue(actualSnapshot.snippetAt(span)).appendText("): ");
                mismatchDescription.appendValue(colors);
                mismatchDescription.appendText("\n");
            }
            if (highlights.containsKey(expectedOffsetRange)) {
                return expectedHighlights.matches(highlights.get(expectedOffsetRange));
            } else {
                return expectedHighlights.matches(emptyList());
            }
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("highlights containing snippet ")
                    .appendValue(expectedSnippet)
                    .appendText(" with attributes ")
                    .appendDescriptionOf(expectedHighlights);
        }
    }
}
