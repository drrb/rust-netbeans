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

import com.github.drrb.rust.netbeans.test.junit412.Parameterized;
import com.github.drrb.rust.netbeans.test.junit412.Parameterized.Parameters;
import org.junit.ComparisonFailure;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static com.github.drrb.rust.netbeans.parsing.javacc.TestSrc.Dump.NO_DUMP;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class ParseTest {
    // Temporarily run just one test by using this list:
    private static final List<TestSrc> INCLUDED_SOURCES = Stream.of(
//            "errors/errors_in_items.rs"
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
            fail("Expected parse result file doesn't exist: " + expectedParseResultFile + ". Creating it.");
        }
        ParseResult expectedResult = expectedParseResultFile.result();
        if (expectedResult.equals(actualResult)) {
            return;
        }
        throw new ComparisonFailure(
                "Parsing " + sourceFile + " didn't produce expected result",
                expectedResult.json(),
                actualResult.json()
        );
    }

}
