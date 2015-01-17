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

import java.io.IOException;
import java.io.StringReader;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import org.junit.After;
import org.junit.Test;
import org.netbeans.api.lexer.Token;
import org.netbeans.lib.lexer.WrapTokenId;
import org.netbeans.lib.lexer.token.TextToken;
import org.netbeans.spi.lexer.LexerInput;
import static com.github.drrb.rust.netbeans.parsing.RustTokenId.*;
import static org.hamcrest.MatcherAssert.assertThat;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.Ignore;
import org.netbeans.lib.lexer.token.AbstractToken;

/**
 *
 */
public class NetbeansRustLexerTest {

    private NetbeansRustLexer lexer;

    @After
    public void releaseLexer() {
        lexer.release();
    }

    @Test
    public void shouldParseSource() {
        lexer = new TestableNetbeansRustLexer("fn main() { }");
        assertThat(lexer.nextToken(), isToken(IDENT, 0, "fn"));
        assertThat(lexer.nextToken(), isToken(WHITESPACE, 2, " "));
        assertThat(lexer.nextToken(), isToken(IDENT, 3, "main"));
        assertThat(lexer.nextToken(), isToken(OPEN_PAREN, 7, "("));
        assertThat(lexer.nextToken(), isToken(CLOSE_PAREN, 8, ")"));
        assertThat(lexer.nextToken(), isToken(WHITESPACE, 9, " "));
        assertThat(lexer.nextToken(), isToken(OPEN_BRACE, 10, "{"));
        assertThat(lexer.nextToken(), isToken(WHITESPACE, 11, " "));
        assertThat(lexer.nextToken(), isToken(CLOSE_BRACE, 12, "}"));
        assertThat(lexer.nextToken(), isToken(WHITESPACE, 13, ""));
        assertThat(lexer.nextToken(), is(nullValue()));
    }

    @Test
    public void shouldCopeWithEmptyString() {
        lexer = new TestableNetbeansRustLexer("");
        assertThat(lexer.nextToken(), is(nullValue()));
    }

    @Test
    public void shouldCopeWithAHalfFinishedToken() {
        lexer = new TestableNetbeansRustLexer(" /*\n");
        assertThat(lexer.nextToken(), isToken(GARBAGE, 0, " /*\n"));
        assertThat(lexer.nextToken(), isToken(UNDERSCORE, 4, "")); //TODO: why is this?
        assertThat(lexer.nextToken(), is(nullValue()));
    }

    @Test
    @Ignore("panics")
    public void shouldCopeWithAHalfFinishedTokenAtTheStartOfTheSource() {
        lexer = new TestableNetbeansRustLexer("\"\n");
        assertThat(lexer.nextToken(), is(nullValue()));
    }

    private Matcher<Token<RustTokenId>> isToken(
            final RustTokenId expectedId,
            final int expectedOffset,
            final String expectedText
    ) {
        return new TypeSafeDiagnosingMatcher<Token<RustTokenId>>() {
            @Override
            protected boolean matchesSafely(Token<RustTokenId> actual, Description mismatchDescription) {
                return compare("id", expectedId, actual.id(), mismatchDescription)
                        & compare("offset", expectedOffset, ((AbstractToken) actual).rawOffset(), mismatchDescription)
                        & compare("text", expectedText, actual.text(), mismatchDescription);
            }

            private boolean compare(String property, Object expected, Object actual, Description mismatchDescription) {
                if (expected.equals(actual)) {
                    return true;
                } else {
                    mismatchDescription.appendText(property + " is ").appendValue(actual).appendText(" (expected ").appendValue(expected).appendText(")\n");
                    return false;
                }
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Rust token ")
                        .appendValue(expectedId)
                        .appendText(" at ")
                        .appendValue(expectedOffset)
                        .appendText(" with text ")
                        .appendValue(expectedText);
            }
        };
    }

    /**
     * Best guess at how the Lexing framework works.
     *
     * @todo replace this when we find out how lexers are supposed to be tested
     */
    private static class TestableNetbeansRustLexer extends NetbeansRustLexer {

        private final String source;
        private StringReader reader;
        private StringBuilder readChars;
        private int tokenStartOffset;

        public TestableNetbeansRustLexer(CharSequence source) {
            super(null);
            this.source = source.toString();
            this.readChars = new StringBuilder();
            this.reader = new StringReader(this.source);
            this.tokenStartOffset = 0;
        }

        @Override
        protected Token<RustTokenId> createToken(RustTokenId tokenType) {
            TextToken<RustTokenId> token = new TextToken<>(new WrapTokenId<>(tokenType), charactersReadSoFar()).createCopy(null, tokenStartOffset);
            tokenStartOffset += readChars.length();
            readChars = new StringBuilder();
            return token;
        }

        @Override
        protected int readOneCharacter() {
            try {
                int character = reader.read();
                if (character == -1) {
                    return LexerInput.EOF;
                }
                readChars.append((char) character);
                return character;
            } catch (IOException ex) {
                throw new RuntimeException("Error reading source in test", ex);
            }
        }

        @Override
        protected String charactersReadSoFar() {
            return readChars.toString();
        }

        @Override
        protected void backUp(int length) {
            readChars.delete(0, length);
            try {
                reader = new StringReader(source);
                reader.skip(source.length() - length);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        @Override
        protected int charsReadThisToken() {
            return readChars.length();
        }
    };
}
