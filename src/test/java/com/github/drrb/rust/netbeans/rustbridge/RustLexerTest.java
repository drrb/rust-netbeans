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

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Test;

import com.github.drrb.rust.netbeans.parsing.antlr.AntlrTokenID;
import com.github.drrb.rust.netbeans.parsing.antlr.CommonRustTokenIDs;
import com.github.drrb.rust.netbeans.parsing.antlr.RustAntlrLexer;
import static org.junit.Assert.assertThat;

//@Ignore("This is for the old native parser")
public class RustLexerTest {

    private com.github.drrb.rust.antlr.RustLexer lexer;

    @After
    public void cleanUpLexer() {
        lexer.reset();
    }

    @Test
    public void shouldTokenizeWhitespace() {
        String input = "  \t ";
        lexer = RustAntlrLexer.fromString(input);
        lexer.setChannel(-1);
        assertThat(RustToken.of(lexer.nextToken()), isToken(com.github.drrb.rust.antlr.RustLexer.Whitespace).from(1, 0).to(1, 4));
//        assertThat(RustToken.of(lexer.nextToken()), isToken(-1).from(1, 0).to(1, 5));
    }

    @Test
    public void shouldCopeWithBadSource() throws Exception {
        lexer = RustAntlrLexer.fromString("fn main() å\n");
        exhaustLexer();
    }

    @Test
    public void shouldCopeWithEmptyString() throws Exception {
        lexer = RustAntlrLexer.fromString("");
        exhaustLexer();
    }

    @Test
    public void shouldTokenizeRustString() throws Exception {
        StringBuilder source = new StringBuilder();
        source.append("fn main() {\n");
        source.append("  println!(\"hi!\");\n");
        source.append("}\n");
        source.append("\n");
        lexer = RustAntlrLexer.fromString(source.toString());
        lexer.setChannel(-1);

//        org.antlr.v4.runtime.Token tk;
//        while ((tk = lexer.nextToken()).getType() != -1) {
//            System.out.println(tk.getText() + "\t"
//                    + CommonRustTokenIDs.forTokenType(tk.getType()).name()
//                    + "\t" + tk.getType());
//        }
//
//        lexer = RustAntlrLexer.fromString(source.toString());
//        lexer.setChannel(-1);

        assertThat(RustToken.of(lexer.nextToken()), isToken(com.github.drrb.rust.antlr.RustLexer.Fn).from(1, 0).to(1, 2));
        assertThat(RustToken.of(lexer.nextToken()), isToken(com.github.drrb.rust.antlr.RustLexer.Whitespace).from(1, 2).to(1, 3));
        assertThat(RustToken.of(lexer.nextToken()), isToken(com.github.drrb.rust.antlr.RustLexer.Ident).from(1, 3).to(1, 7));
        assertThat(RustToken.of(lexer.nextToken()), isToken(com.github.drrb.rust.antlr.RustLexer.LeftParen).from(1, 7).to(1, 8));
        assertThat(RustToken.of(lexer.nextToken()), isToken(com.github.drrb.rust.antlr.RustLexer.RightParen).from(1, 8).to(1, 9));
        assertThat(RustToken.of(lexer.nextToken()), isToken(com.github.drrb.rust.antlr.RustLexer.Whitespace).from(1, 9).to(1, 10));
        assertThat(RustToken.of(lexer.nextToken()), isToken(com.github.drrb.rust.antlr.RustLexer.LeftBrace).from(1, 10).to(1, 11));
        assertThat(RustToken.of(lexer.nextToken()), isToken(com.github.drrb.rust.antlr.RustLexer.Whitespace).from(1, 11).to(1, 14));
//        assertThat(RustToken.of(lexer.nextToken()), isToken(com.github.drrb.rust.antlr.RustLexer.Whitespace).from(2, 0).to(2, 2));

        assertThat(RustToken.of(lexer.nextToken()), isToken(com.github.drrb.rust.antlr.RustLexer.Ident).from(2, 2).to(2, 9));
        assertThat(RustToken.of(lexer.nextToken()), isToken(com.github.drrb.rust.antlr.RustLexer.Bang).from(2, 9).to(2, 10));
        assertThat(RustToken.of(lexer.nextToken()), isToken(com.github.drrb.rust.antlr.RustLexer.LeftParen).from(2, 10).to(2, 11));
        assertThat(RustToken.of(lexer.nextToken()), isToken(com.github.drrb.rust.antlr.RustLexer.StringLiteral).from(2, 11).to(2, 16));
        assertThat(RustToken.of(lexer.nextToken()), isToken(com.github.drrb.rust.antlr.RustLexer.RightParen).from(2, 16).to(2, 17));
        assertThat(RustToken.of(lexer.nextToken()), isToken(com.github.drrb.rust.antlr.RustLexer.Semicolon).from(2, 17).to(2, 18));
        assertThat(RustToken.of(lexer.nextToken()), isToken(com.github.drrb.rust.antlr.RustLexer.Whitespace).from(2, 18).to(2, 19));
        assertThat(RustToken.of(lexer.nextToken()), isToken(com.github.drrb.rust.antlr.RustLexer.RightBrace).from(3, 0).to(3, 1));
        assertThat(RustToken.of(lexer.nextToken()), isToken(com.github.drrb.rust.antlr.RustLexer.Whitespace).from(3, 1).to(3, 3));
        assertThat(RustToken.of(lexer.nextToken()), isToken(-1).from(5, 0).to(5, 0));
    }

    private void exhaustLexer() {
        while (lexer.nextToken().getType() != -1) {
        }
    }

    private static RustTokenMatcher.Builder isToken(RustToken type) {
        return new RustTokenMatcher.Builder(type);
    }

    private static RustTokenMatcher.Builder isToken(int type) {
        return new RustTokenMatcher.Builder(type);
    }

    private static class RustTokenMatcher extends TypeSafeMatcher<RustToken> {

        static class Builder {

            private final int type;
            private int startLine;
            private int startCol;

            public Builder(AntlrTokenID type) {
                this.type = type.ordinal();
            }

            public Builder(int type) {
                this.type = type;
            }

            public Builder(RustToken token) {
                this.type = token.type;
                this.startLine = token.startLine;
                this.startCol = token.startCol;
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

        private final int type;
        private final int startLine;
        private final int startCol;
        private final int endLine;
        private final int endCol;

        public RustTokenMatcher(int type, int startLine, int startCol, int endLine, int endCol) {
            this.type = type;
            this.startLine = startLine;
            this.startCol = startCol;
            this.endLine = endLine;
            this.endCol = endCol;
        }

        @Override
        public boolean matchesSafely(RustToken item) {
            return item.getType() == CommonRustTokenIDs.forTokenType(type)
                    && item.startLine == startLine
                    && item.startCol == startCol
                    && item.endLine == endLine
                    && item.endCol == endCol;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("RustToken of type ")
                    .appendValue(type).appendValue(" ").appendValue(CommonRustTokenIDs.forTokenType(type).name())
                    .appendText(String.format(" at %s,%s - %s,%s", startLine, startCol, endLine, endCol));
        }
    }
}
