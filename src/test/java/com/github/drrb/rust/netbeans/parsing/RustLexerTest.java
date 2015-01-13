/**
 * Copyright (C) 2013 drrb
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

import org.junit.Test;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import static com.github.drrb.rust.netbeans.parsing.RustToken.Type.*;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import org.junit.After;

public class RustLexerTest {
    private RustLexer lexer;
    
    @After
    public void cleanUpLexer() {
        lexer.release();
    }

    @Test
    public void shouldTokenizeWhitespace() {
        String input = "  \t ";
        lexer = new RustLexer(input);

        assertThat(lexer.nextToken(), isToken(WHITESPACE).from(1, 0).to(1, 5));
        assertThat(lexer.nextToken(), isToken(EOF).from(1, 0).to(1, 5));
    }
    
    @Test
    public void shouldCopeWithBadSource() throws Exception {
        lexer = new RustLexer("fn main() å\n");
        while (!lexer.nextToken().isEof()) {   
        }
    }

    @Test
    public void shouldTokenizeRustString() throws Exception {
        StringBuilder source = new StringBuilder();
        source.append("fn main() {\n");
        source.append("  println!(\"hi!\");\n");
        source.append("}\n");
        source.append("\n");
        lexer = new RustLexer(source.toString());
        assertThat(lexer.nextToken(), isToken(IDENT).from(1, 0).to(1, 2));
        assertThat(lexer.nextToken(), isToken(WHITESPACE).from(1, 2).to(1, 3));
        assertThat(lexer.nextToken(), isToken(IDENT).from(1, 3).to(1, 7));
        assertThat(lexer.nextToken(), isToken(OPEN_DELIM).from(1, 7).to(1, 8));
        assertThat(lexer.nextToken(), isToken(CLOSE_DELIM).from(1, 8).to(1, 9));
        assertThat(lexer.nextToken(), isToken(WHITESPACE).from(1, 9).to(1, 10));
        assertThat(lexer.nextToken(), isToken(OPEN_DELIM).from(1, 10).to(1, 11));
        assertThat(lexer.nextToken(), isToken(WHITESPACE).from(1, 11).to(2, 2));
        assertThat(lexer.nextToken(), isToken(IDENT).from(2, 2).to(2, 9));
        assertThat(lexer.nextToken(), isToken(NOT).from(2, 9).to(2, 10));
        assertThat(lexer.nextToken(), isToken(OPEN_DELIM).from(2, 10).to(2, 11));
        assertThat(lexer.nextToken(), isToken(LITERAL).from(2, 11).to(2, 16));
        assertThat(lexer.nextToken(), isToken(CLOSE_DELIM).from(2, 16).to(2, 17));
        assertThat(lexer.nextToken(), isToken(SEMI).from(2, 17).to(2, 18));
        assertThat(lexer.nextToken(), isToken(WHITESPACE).from(2, 18).to(3, 0));
        assertThat(lexer.nextToken(), isToken(CLOSE_DELIM).from(3, 0).to(3, 1));
        assertThat(lexer.nextToken(), isToken(WHITESPACE).from(3, 1).to(4, 1));
        assertThat(lexer.nextToken(), isToken(EOF).from(3, 1).to(4, 1));
    }

    private static RustTokenMatcher.Builder isToken(RustToken.Type type) {
        return new RustTokenMatcher.Builder(type);
    }

    private static class RustTokenMatcher extends TypeSafeMatcher<RustToken> {

        static class Builder {

            private final RustToken.Type type;
            private int startLine;
            private int startCol;

            public Builder(RustToken.Type type) {
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

        private final RustToken.Type type;
        private final int startLine;
        private final int startCol;
        private final int endLine;
        private final int endCol;

        public RustTokenMatcher(RustToken.Type type, int startLine, int startCol, int endLine, int endCol) {
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
