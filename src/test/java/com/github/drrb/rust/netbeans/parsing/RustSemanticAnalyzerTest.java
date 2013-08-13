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
package com.github.drrb.rust.netbeans.parsing;

import com.github.drrb.rust.netbeans.RustDocument;
import com.github.drrb.rust.netbeans.TestParsing;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import javax.swing.text.Document;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.netbeans.modules.csl.api.ColoringAttributes;
import static org.netbeans.modules.csl.api.ColoringAttributes.*;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.parsing.spi.ParseException;

/**
 *
 */
public class RustSemanticAnalyzerTest {

    private RustSemanticAnalyzer semanticAnalyzer;

    @Before
    public void setUp() {
        semanticAnalyzer = new RustSemanticAnalyzer();
    }

    @Test
    public void shouldFindFunction() {
        StringBuilder source = new StringBuilder();
        source.append("fn main() {\n");
        source.append("  return 1\n");
        source.append("}\n");
        assertThat(analyzed(source), hasHighlight(3, 7, METHOD));
    }

    @Test
    public void shouldFindEnumWithConstants() {
        StringBuilder source = new StringBuilder();
        source.append("enum Color {\n");
        source.append("  Red,\n");
        source.append("  Green,\n");
        source.append("  Blue,\n");
        source.append("  Black\n");
        source.append("}\n");
        assertThat(analyzed(source), hasHighlight(5, 10, CLASS));
        assertThat(analyzed(source), hasHighlight(15, 18, ENUM));
        assertThat(analyzed(source), hasHighlight(22, 27, ENUM));
        assertThat(analyzed(source), hasHighlight(31, 35, ENUM));
        assertThat(analyzed(source), hasHighlight(39, 44, ENUM));
    }

    @Test
    public void shouldFindStructAndFields() {
        StringBuilder source = new StringBuilder();
        source.append("struct Point {\n");
        source.append("    x: float,\n");
        source.append("    y: float\n");
        source.append("}\n");
        assertThat(analyzed(source), hasHighlight(7, 12, CLASS));
        assertThat(analyzed(source), hasHighlight(19, 20, FIELD));
        assertThat(analyzed(source), hasHighlight(33, 34, FIELD));
    }

    @Test
    public void shouldFindTrait() {
        StringBuilder source = new StringBuilder();
        source.append("trait Printable {\n");
        source.append("    fn print(&self);\n");
        source.append("}\n");
        assertThat(analyzed(source), hasHighlight(6, 15, CLASS));
        assertThat(analyzed(source), hasHighlight(25, 30, METHOD));
    }

    @Test
    public void shouldFindTraitImplWithMethods() {
        StringBuilder source = new StringBuilder();
        source.append("impl Printable for int {\n");
        source.append("    fn print(&self) { println(fmt!(\"%d\", *self)) }\n");
        source.append("}\n");
        assertThat(analyzed(source), hasHighlight(5, 14, CLASS));
        assertThat(analyzed(source), hasHighlight(32, 37, METHOD));
    }

    @Test
    public void shouldNotDieWhenIdentifierNotTypedYet() {
        StringBuilder source = new StringBuilder();
        source.append("struct ");
        analyzed(source); // Shouldn't die
    }

    private Matcher<Map<OffsetRange, Set<ColoringAttributes>>> hasHighlight(final int start, final int end, final ColoringAttributes... coloringAttributes) {
        return new TypeSafeMatcher<Map<OffsetRange, Set<ColoringAttributes>>>() {
            @Override
            public boolean matchesSafely(Map<OffsetRange, Set<ColoringAttributes>> colors) {
                OffsetRange offsetRange = new OffsetRange(start, end);
                if (colors.containsKey(offsetRange)) {
                    return colors.get(offsetRange).equals(EnumSet.copyOf(Arrays.asList(coloringAttributes)));
                } else {
                    return false;
                }
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("highlights containing section from ")
                        .appendValue(start)
                        .appendText(" to ")
                        .appendValue(end)
                        .appendText(" with attributes ")
                        .appendValueList("<", ",", ">", coloringAttributes);
            }
        };
    }

    private Map<OffsetRange, Set<ColoringAttributes>> analyzed(CharSequence source) {
        semanticAnalyzer.run(TestParsing.parse(source), null);
        return semanticAnalyzer.getHighlights();
    }
}
