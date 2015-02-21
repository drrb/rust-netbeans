/*
 * Copyright (C) 2015 drrb
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

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;
import static com.github.drrb.rust.netbeans.parsing.RustTokenId.*;
import com.github.drrb.rust.netbeans.test.NetbeansWithRust;
import junit.framework.AssertionFailedError;
import static org.hamcrest.MatcherAssert.assertThat;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.Ignore;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.lib.lexer.test.LexerTestUtilities;

/**
 *
 */
public class NetbeansRustLexerTest {

    private final NetbeansWithRust netbeans = new NetbeansWithRust();

    @Test
    public void shouldParseSource() {
        TokenSequence<RustTokenId> ts = netbeans.tokenize("fn main() { }");
        assertThat(ts, hasNextToken(FN, 0, "fn"));
        assertThat(ts, hasNextToken(WHITESPACE, 2, " "));
        assertThat(ts, hasNextToken(IDENT, 3, "main"));
        assertThat(ts, hasNextToken(OPEN_PAREN, 7, "("));
        assertThat(ts, hasNextToken(CLOSE_PAREN, 8, ")"));
        assertThat(ts, hasNextToken(WHITESPACE, 9, " "));
        assertThat(ts, hasNextToken(OPEN_BRACE, 10, "{"));
        assertThat(ts, hasNextToken(WHITESPACE, 11, " "));
        assertThat(ts, hasNextToken(CLOSE_BRACE, 12, "}"));
        assertThat(ts, hasNoNextToken());
    }

    @Test
    public void shouldCopeWithEmptyString() {
        TokenSequence<RustTokenId> ts =  netbeans.tokenize("");
        assertThat(ts, hasNoNextToken());
    }

    @Test
    public void shouldCopeWithAHalfFinishedToken() {
        TokenSequence<RustTokenId> ts =  netbeans.tokenize(" /*\n");
        assertThat(ts, hasNextToken(GARBAGE, 0, " /*\n"));
        assertThat(ts, hasNoNextToken());
    }

    @Test
    @Ignore("panics")
    public void shouldCopeWithAHalfFinishedTokenAtTheStartOfTheSource() {
        TokenSequence<RustTokenId> ts =  netbeans.tokenize("\"\n");
        assertThat(ts, hasNextToken(GARBAGE, 0, "\"\n"));
        assertThat(ts, hasNoNextToken());
    }

    private Matcher<TokenSequence<RustTokenId>> hasNoNextToken() {
        return new TypeSafeDiagnosingMatcher<TokenSequence<RustTokenId>>() {
            @Override
            protected boolean matchesSafely(TokenSequence<RustTokenId> actual, Description mismatchDescription) {
                if (actual.moveNext()) {
                    mismatchDescription.appendText("TokenSequence with next token ")
                        .appendValue(actual.token().id())
                        .appendText(" at ")
                        .appendValue(actual.offset())
                        .appendText(" with text ")
                        .appendValue(actual.token().text());
                    return false;
                }
                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Rust token sequence with no next token ");
            }
        };
    }

    private Matcher<TokenSequence<RustTokenId>> hasNextToken(
            final RustTokenId expectedId,
            final int expectedOffset,
            final String expectedText
    ) {
        return new TypeSafeDiagnosingMatcher<TokenSequence<RustTokenId>>() {
            @Override
            protected boolean matchesSafely(TokenSequence<RustTokenId> actual, Description mismatchDescription) {
                if (!actual.moveNext()) {
                    mismatchDescription.appendText("TokenSequence with no next token");
                    return false;
                }
                try {
                    LexerTestUtilities.assertTokenEquals("Token index[" + actual.index() + "]", actual, expectedId, expectedText, expectedOffset);
                } catch (AssertionFailedError failure) {
                    mismatchDescription.appendText(failure.getMessage());
                    return false;
                }
                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Rust token sequence with next token ")
                        .appendValue(expectedId)
                        .appendText(" at ")
                        .appendValue(expectedOffset)
                        .appendText(" with text ")
                        .appendValue(expectedText);
            }
        };
    }
}
