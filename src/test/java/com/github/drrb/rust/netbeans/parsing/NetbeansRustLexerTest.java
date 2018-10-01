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
package com.github.drrb.rust.netbeans.parsing;

import com.github.drrb.rust.antlr.RustLexer;
import com.github.drrb.rust.netbeans.test.NetbeansWithRust;
import junit.framework.AssertionFailedError;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.Test;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.lib.lexer.test.LexerTestUtilities;

import com.github.drrb.rust.netbeans.parsing.antlr.AntlrTokenID;
import com.github.drrb.rust.netbeans.parsing.antlr.CommonRustTokenIDs;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 */
public class NetbeansRustLexerTest {

    private final NetbeansWithRust netbeans = new NetbeansWithRust();

    static final AntlrTokenID FN = CommonRustTokenIDs.function();
    static final AntlrTokenID WHITESPACE = CommonRustTokenIDs.whitespaceTokenID();
    static final AntlrTokenID IDENTIFIER = CommonRustTokenIDs.identifierTokenID();
    static final AntlrTokenID STRING_LITERAL = CommonRustTokenIDs.stringLiteral();
    static final AntlrTokenID LEFT_PAREN = CommonRustTokenIDs.leftParen();
    static final AntlrTokenID RIGHT_PAREN = CommonRustTokenIDs.rightParen();
    static final AntlrTokenID LEFT_BRACE = CommonRustTokenIDs.leftBrace();
    static final AntlrTokenID BANG = CommonRustTokenIDs.bang();
    static final AntlrTokenID RIGHT_BRACE = CommonRustTokenIDs.rightBrace();
    static final AntlrTokenID SEMICOLON = CommonRustTokenIDs.semicolon();
    static final AntlrTokenID DOUBLE_QUOTE = CommonRustTokenIDs.forSymbol('"');
    static final AntlrTokenID BLOCK_COMMENT_PREFIX = CommonRustTokenIDs.forTokenType(RustLexer.BlockCommentPrefix);
    static final AntlrTokenID EOF = CommonRustTokenIDs.eof();

    @Test
    public void shouldParseSource() {
        TokenSequence<AntlrTokenID> ts = netbeans.tokenize("fn main() { }");
        assertThat(ts, hasNextToken(FN, 0, "fn"));
        assertThat(ts, hasNextToken(WHITESPACE, 2, " "));
        assertThat(ts, hasNextToken(IDENTIFIER, 3, "main"));
        assertThat(ts, hasNextToken(LEFT_PAREN, 7, "("));
        assertThat(ts, hasNextToken(RIGHT_PAREN, 8, ")"));
        assertThat(ts, hasNextToken(WHITESPACE, 9, " "));
        assertThat(ts, hasNextToken(LEFT_BRACE, 10, "{"));
        assertThat(ts, hasNextToken(WHITESPACE, 11, " "));
        assertThat(ts, hasNextToken(RIGHT_BRACE, 12, "}"));
        assertThat(ts, hasNoNextToken());
    }

    @Test
    public void shouldParseSource2() {
        String source = String.join("",
        "fn main() {\n",
        "  println!(\"hi!\");\n",
        "}\n",
        "\n");
        TokenSequence<AntlrTokenID> ts = netbeans.tokenize(source);
        assertThat(ts, hasNextToken(FN, 0, "fn"));
        assertThat(ts, hasNextToken(WHITESPACE, 2, " "));
        assertThat(ts, hasNextToken(IDENTIFIER, 3, "main"));
        assertThat(ts, hasNextToken(LEFT_PAREN, 7, "("));
        assertThat(ts, hasNextToken(RIGHT_PAREN, 8, ")"));
        assertThat(ts, hasNextToken(WHITESPACE, 9, " "));
        assertThat(ts, hasNextToken(LEFT_BRACE, 10, "{"));
        assertThat(ts, hasNextToken(WHITESPACE, 11, "\n  "));
        assertThat(ts, hasNextToken(IDENTIFIER, 14, "println"));
        assertThat(ts, hasNextToken(BANG, 21, "!"));
        assertThat(ts, hasNextToken(LEFT_PAREN, 22, "("));
        assertThat(ts, hasNextToken(STRING_LITERAL, 23, "\"hi!\""));
        assertThat(ts, hasNextToken(RIGHT_PAREN, 28, ")"));
        assertThat(ts, hasNextToken(SEMICOLON, 29, ";"));
        assertThat(ts, hasNextToken(WHITESPACE, 30, "\n"));
        assertThat(ts, hasNextToken(RIGHT_BRACE, 31, "}"));
        assertThat(ts, hasNextToken(WHITESPACE, 32, "\n\n"));
        assertThat(ts, hasNoNextToken());
    }

    @Test
    public void shouldCopeWithEmptyString() {
        TokenSequence<AntlrTokenID> ts =  netbeans.tokenize("");
        assertThat(ts, hasNoNextToken());
    }

    @Test
    public void shouldCopeWithAHalfFinishedToken() {
        TokenSequence<AntlrTokenID> ts =  netbeans.tokenize(" /*\n");
        assertThat(ts, hasNextToken(WHITESPACE, 0, " "));
        assertThat(ts, hasNextToken(BLOCK_COMMENT_PREFIX, 1, "/*"));
        assertThat(ts, hasNextToken(WHITESPACE, 3, "\n"));
        assertThat(ts, hasNoNextToken());
    }

    @Test
    public void shouldCopeWithAHalfFinishedTokenAtTheStartOfTheSource() {
        TokenSequence<AntlrTokenID> ts =  netbeans.tokenize("\"\n");
        assertThat(ts, hasNextToken(DOUBLE_QUOTE, 0, "\""));
        assertThat(ts, hasNextToken(WHITESPACE, 1, "\n"));
        assertThat(ts, hasNoNextToken());
    }

    private Matcher<TokenSequence<AntlrTokenID>> hasNoNextToken() {
        return new TypeSafeDiagnosingMatcher<TokenSequence<AntlrTokenID>>() {
            @Override
            protected boolean matchesSafely(TokenSequence<AntlrTokenID> actual, Description mismatchDescription) {
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

    private Matcher<TokenSequence<AntlrTokenID>> hasNextToken(
            final AntlrTokenID expectedId,
            final int expectedOffset,
            final String expectedText
    ) {
        return new TypeSafeDiagnosingMatcher<TokenSequence<AntlrTokenID>>() {
            @Override
            protected boolean matchesSafely(TokenSequence<AntlrTokenID> actual, Description mismatchDescription) {
                if (!actual.moveNext()) {
                    mismatchDescription.appendText("TokenSequence with no next token");
                    return false;
                }
                try {
                    LexerTestUtilities.assertTokenEquals("Token index[" + actual.index() + "]", actual, expectedId, expectedText, expectedOffset);
                } catch (AssertionFailedError failure) {
                    failure.printStackTrace();
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
