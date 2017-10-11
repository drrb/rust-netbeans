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

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class TokenizationTest {
    // Temporarily run just one test by using this list:
    private static final List<TestSrc> INCLUDED_SOURCES = Stream.of(
    )
            .map(Objects::toString)
            .map(Paths::get)
            .map(TestFile::get)
            .map(TestSrc::get)
            .collect(toList());

    @Parameters(name = "{0}")
    public static Iterable<TestSrc> sources() throws IOException {
        if (INCLUDED_SOURCES.isEmpty()) {
            return TestSrc.all().collect(toList());
        } else {
            return INCLUDED_SOURCES;
        }
    }

    private final TestSrc sourceFile;

    public TokenizationTest(TestSrc sourceFile) {
        this.sourceFile = sourceFile;
    }

    @Test
    public void testLex() throws Exception {
        TokenizationResult actualResult = sourceFile.tokenize();
        ExpectedTokensFile expectedTokensFile = sourceFile.expectedTokensFile();
        if (!expectedTokensFile.exists()) {
            Predicate<TokenizationResult.Token> isGarbage = TokenizationResult.Token::isGarbage;
            List<TokenizationResult.Token> garbageTokens = actualResult.tokens.stream().filter(isGarbage).collect(toList());
            if (garbageTokens.isEmpty()) {
                expectedTokensFile.createWith(actualResult);
                fail("Expected tokens file doesn't exist: " + expectedTokensFile + ". Creating it.");
            } else {
                fail("Found garbage tokens: " + garbageTokens + "\nin tokenization result:\n" + actualResult);
            }
        }
        TokenizationResult expectedResult = expectedTokensFile.tokens();
        if (expectedResult.equals(actualResult)) {
            return;
        }
        throw new ComparisonFailure(
                "Tokenizing " + sourceFile + " didn't produce expected tokens",
                expectedResult.json(),
                actualResult.json()
        );
    }
}
