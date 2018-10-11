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
package com.github.drrb.rust.netbeans.rustbridge;

import com.github.drrb.rust.netbeans.parsing.RustTokenId;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

import static com.github.drrb.rust.netbeans.parsing.RustTokenId.*;
import static org.junit.Assert.assertThat;

@Ignore("This is for the old native parser")
public class RustLexerTest {
    private RustLexer lexer;

    @After
    public void cleanUpLexer() {
        lexer.release();
    }

    @Test
    public void shouldTokenizeWhitespace() {
        String input = "  \t ";
        lexer = RustLexer.forString(input);

        assertThat(lexer.nextToken(), isToken(WHITESPACE).from(1, 0).to(1, 5));
        assertThat(lexer.nextToken(), isToken(EOF).from(1, 0).to(1, 5));
    }

    @Test
    public void shouldCopeWithBadSource() throws Exception {
        lexer = RustLexer.forString("fn main() å\n");
        exhaustLexer();
    }

    @Test
    public void shouldCopeWithEmptyString() throws Exception {
        lexer = RustLexer.forString("");
        exhaustLexer();
    }

    @Test
    public void shouldTokenizeRustString() throws Exception {
        StringBuilder source = new StringBuilder();
        source.append("fn main() {\n");
        source.append("  println!(\"hi!\");\n");
        source.append("}\n");
        source.append("\n");
        lexer = RustLexer.forString(source.toString());
        assertThat(lexer.nextToken(), isToken(FN).from(1, 0).to(1, 2));
        assertThat(lexer.nextToken(), isToken(WHITESPACE).from(1, 2).to(1, 3));
        assertThat(lexer.nextToken(), isToken(IDENTIFIER).from(1, 3).to(1, 7));
        assertThat(lexer.nextToken(), isToken(LEFT_PAREN).from(1, 7).to(1, 8));
        assertThat(lexer.nextToken(), isToken(RIGHT_PAREN).from(1, 8).to(1, 9));
        assertThat(lexer.nextToken(), isToken(WHITESPACE).from(1, 9).to(1, 10));
        assertThat(lexer.nextToken(), isToken(LEFT_BRACE).from(1, 10).to(1, 11));
        assertThat(lexer.nextToken(), isToken(WHITESPACE).from(1, 11).to(2, 2));
        assertThat(lexer.nextToken(), isToken(IDENTIFIER).from(2, 2).to(2, 9));
        assertThat(lexer.nextToken(), isToken(BANG).from(2, 9).to(2, 10));
        assertThat(lexer.nextToken(), isToken(LEFT_PAREN).from(2, 10).to(2, 11));
        assertThat(lexer.nextToken(), isToken(STRING_LITERAL).from(2, 11).to(2, 16));
        assertThat(lexer.nextToken(), isToken(RIGHT_PAREN).from(2, 16).to(2, 17));
        assertThat(lexer.nextToken(), isToken(SEMICOLON).from(2, 17).to(2, 18));
        assertThat(lexer.nextToken(), isToken(WHITESPACE).from(2, 18).to(3, 0));
        assertThat(lexer.nextToken(), isToken(RIGHT_BRACE).from(3, 0).to(3, 1));
        assertThat(lexer.nextToken(), isToken(WHITESPACE).from(3, 1).to(4, 1));
        assertThat(lexer.nextToken(), isToken(EOF).from(3, 1).to(4, 1));
    }

    private void exhaustLexer() {
        while (!lexer.nextToken().isEof()) { }
    }

    private static RustTokenMatcher.Builder isToken(RustTokenId type) {
        return new RustTokenMatcher.Builder(type);
    }

    private static class RustTokenMatcher extends TypeSafeMatcher<RustToken> {

        static class Builder {

            private final RustTokenId type;
            private int startLine;
            private int startCol;

            public Builder(RustTokenId type) {
                this.type = type;
            }

            public Builder from(int startLine, int startCol) {
                this.startLine = startLine;
                this.startCol = startCol;
                return this;
            }

            public RustTokenMatcher to(int endLine, int endCol) {
                return new RustTokenMatcher(type, startLine, startCol, endLine, endCol);
            }
        }

        private final RustTokenId type;
        private final int startLine;
        private final int startCol;
        private final int endLine;
        private final int endCol;

        public RustTokenMatcher(RustTokenId type, int startLine, int startCol, int endLine, int endCol) {
            this.type = type;
            this.startLine = startLine;
            this.startCol = startCol;
            this.endLine = endLine;
            this.endCol = endCol;
        }

        @Override
        public boolean matchesSafely(RustToken item) {
            return item.getType() == type
                    && item.startLine == startLine
                    && item.startCol == startCol
                    && item.endLine == endLine
                    && item.endCol == endCol;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("RustToken of type ")
                    .appendValue(type)
                    .appendText(String.format(" at %s,%s - %s,%s", startLine, startCol, endLine, endCol));
        }
    }
}
