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
package com.github.drrb.rust.netbeans.highlighting;

import static com.github.drrb.rust.netbeans.TestParsing.*;
import com.github.drrb.rust.netbeans.parsing.NetbeansRustParser;
import java.util.Map;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import static org.hamcrest.Matchers.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.netbeans.modules.csl.api.ColoringAttributes;
import static org.netbeans.modules.csl.api.ColoringAttributes.*;
import org.netbeans.modules.csl.api.OffsetRange;

/**
 *
 */
public class RustOccurrencesFinderTest {

    private RustOccurrencesFinder occurrencesFinder;

    @Before
    public void setUp() {
        occurrencesFinder = new RustOccurrencesFinder();
    }

    @Test
    public void shouldFindTwoIdentifiersInAFunction() {
        StringBuilder source = new StringBuilder();
        source.append("fn main() {\n");
        source.append("    let name = ~\"john\";\n");
        source.append("    println(name);\n");
        source.append("}\n");
        NetbeansRustParser.NetbeansRustParserResult result = parse(source);

        occurrencesFinder.setCaretPosition(22); // Caret is at: let na|me = ...
        occurrencesFinder.run(result, null);
        Map<OffsetRange, ColoringAttributes> occurrences = occurrencesFinder.getOccurrences();
        assertThat(occurrences, hasOccurrence(20, 24, LOCAL_VARIABLE));
        assertThat(occurrences, hasOccurrence(48, 52, LOCAL_VARIABLE));
    }

    @Test
    public void shouldNotMatchIdentifierWithADifferentName() {
        StringBuilder source = new StringBuilder();
        source.append("fn main() {\n");
        source.append("    let name = ~\"john\";\n");
        source.append("    println(name);\n");
        source.append("    let age = 50;\n");
        source.append("}\n");
        NetbeansRustParser.NetbeansRustParserResult result = parse(source);

        occurrencesFinder.setCaretPosition(22); // Caret is at: let na|me = ...
        occurrencesFinder.run(result, null);
        Map<OffsetRange, ColoringAttributes> occurrences = occurrencesFinder.getOccurrences();
        assertThat(occurrences, not(hasOccurrence(63, 66, LOCAL_VARIABLE)));
    }

    @Test
    public void shouldMatchIdentifierAtRight() {
        StringBuilder source = new StringBuilder();
        source.append("fn main() {\n");
        source.append("    let name = ~\"john\";\n");
        source.append("    println(name);\n");
        source.append("}\n");
        NetbeansRustParser.NetbeansRustParserResult result = parse(source);

        occurrencesFinder.setCaretPosition(24); // Caret is at: let name| = ...
        occurrencesFinder.run(result, null);
        Map<OffsetRange, ColoringAttributes> occurrences = occurrencesFinder.getOccurrences();
        assertThat(occurrences, hasOccurrence(20, 24, LOCAL_VARIABLE));
        assertThat(occurrences, hasOccurrence(48, 52, LOCAL_VARIABLE));
    }

    private Matcher<Map<OffsetRange, ColoringAttributes>> hasOccurrence(final int start, final int end, final ColoringAttributes type) {
        return new TypeSafeMatcher<Map<OffsetRange, ColoringAttributes>>() {
            @Override
            public boolean matchesSafely(Map<OffsetRange, ColoringAttributes> occurrences) {
                OffsetRange offsetRange = new OffsetRange(start, end);
                if (occurrences.containsKey(offsetRange)) {
                    return occurrences.get(offsetRange).equals(type);
                } else {
                    return false;
                }
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("symbol occurence from ")
                        .appendValue(start)
                        .appendText(" to ")
                        .appendValue(end)
                        .appendText(" of type ")
                        .appendValue(type);
            }
        };
    }

}
