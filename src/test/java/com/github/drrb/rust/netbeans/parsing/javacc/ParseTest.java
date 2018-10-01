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
package com.github.drrb.rust.netbeans.parsing.javacc;

import static com.github.drrb.rust.netbeans.parsing.javacc.TestSrc.Dump.NO_DUMP;
import com.github.drrb.rust.netbeans.test.junit412.Parameterized;
import com.github.drrb.rust.netbeans.test.junit412.Parameterized.Parameters;
import java.io.IOException;
import org.junit.ComparisonFailure;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class ParseTest {

    // Temporarily run just one test by using this list:
    private static final List<TestSrc> INCLUDED_SOURCES = Stream.of( //            "errors/errors_in_items.rs"
            )
            .map(Objects::toString)
            .map(Paths::get)
            .map(TestFile::get)
            .map(TestSrc::get)
            .collect(toList());

    private static final List<TestSrc> EXCLUDED_SOURCES = Stream.of(
            "blocks/blocks.rs",
            "if_then_else/if_statement.rs",
            "macro_complex.rs"
    )
            .map(Paths::get)
            .map(TestFile::get)
            .map(TestSrc::get)
            .collect(toList());

    @Parameters(name = "{0}")
    public static Iterable<TestSrc> sources() {
        if (INCLUDED_SOURCES.isEmpty()) {
            List<TestSrc> allSources = TestSrc.all().collect(toList());
            allSources.removeAll(EXCLUDED_SOURCES);
            return new ArrayList<>(allSources);
        } else {
            return INCLUDED_SOURCES;
        }
    }

    private final TestSrc sourceFile;

    public ParseTest(TestSrc sourceFile) {
        this.sourceFile = sourceFile;
    }

    @Test
    public void testParse() throws Exception {
        ParseResult actualResult = sourceFile.parse(NO_DUMP);
        ExpectedParseResultFile expectedParseResultFile = sourceFile.expectedParseResultFile();
        if (!expectedParseResultFile.exists()) {
            expectedParseResultFile.createWith(actualResult);
            fail(sourceFile.path + ": Expected parse result file doesn't exist: " + expectedParseResultFile + ". Creating it.");
        }
        ParseResult expectedResult = expectedParseResultFile.result();
        if (expectedResult.equals(actualResult)) {
            return;
        }
        throw new ComparisonFailure(
                "Parsing " + sourceFile.path + " didn't produce expected result - expected\n" + expectedResult.json() + "\n"
                + highlightErrors(actualResult),
                expectedResult.json(),
                actualResult.json()
        );
    }

    private String highlightErrors(ParseResult res) throws IOException {
        StringBuilder sb = new StringBuilder();
        List<String> lines = sourceFile.readLines();
        outer:
        for (int i = 0; i < lines.size(); i++) {
            boolean printThisLine = false;
            for (ParseResult.Error err : res.errors) {
                if (err.beginLine == i + 1) {
                    printThisLine = true;
                }
                if (err.beginLine == i) {
                    sb.append(lines.get(i)).append('\n');
                    char[] offset = new char[err.beginColumn];
                    Arrays.fill(offset, ' ');
                    sb.append(offset).append('^').append('\n');
                    continue outer;
                }
            }
            if (printThisLine) {
                sb.append(lines.get(i)).append('\n');
            }
        }
        return sb.toString();
    }

}
