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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.junit.Assert;
import org.junit.Test;
import org.antlr.v4.runtime.Token;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import static com.github.drrb.rust.netbeans.parsing.RustLexer.*;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

public class RustLexerTest {

    @Test
    public void shouldTokenizeWhitespace() {
        String input = "  \t ";
        Iterator<Token> tokens = tokenize(input).iterator();

        Assert.assertEquals(RustLexer.WS, tokens.next().getType());
        assertEquals(Token.EOF, tokens.next().getType());
    }

    @Test
    public void shouldTokenizeFunction() {
        StringBuilder function = new StringBuilder();
        function.append("// Say Hello\n");
        function.append("fn greet(name: str)   {\n");
        function.append("    io::println(fmt!(\"Hello, %?\", name));\n");
        function.append("}\n");
        Iterator<Token> tokens = tokenize(function).iterator();

        assertThat(tokens.next(), is(token(OTHER_LINE_COMMENT, "// Say Hello")));
        assertThat(tokens.next(), is(token(WS, "\n")));
        assertThat(tokens.next(), is(token(FN, "fn")));
        assertThat(tokens.next(), is(token(WS, " ")));
        assertThat(tokens.next(), is(token(IDENT, "greet")));
        assertThat(tokens.next(), is(token(LPAREN, "(")));
        assertThat(tokens.next(), is(token(IDENT, "name")));
        assertThat(tokens.next(), is(token(COLON, ":")));
        assertThat(tokens.next(), is(token(WS, " ")));
        assertThat(tokens.next(), is(token(IDENT, "str")));
        assertThat(tokens.next(), is(token(RPAREN, ")")));
        assertThat(tokens.next(), is(token(WS, "   ")));
        assertThat(tokens.next(), is(token(LBRACE, "{")));
        assertThat(tokens.next(), is(token(WS, "\n    ")));
        assertThat(tokens.next(), is(token(IDENT, "io")));
        assertThat(tokens.next(), is(token(MOD_SEP, "::")));
        assertThat(tokens.next(), is(token(IDENT, "println")));
        assertThat(tokens.next(), is(token(LPAREN, "(")));
        assertThat(tokens.next(), is(token(IDENT, "fmt")));
        assertThat(tokens.next(), is(token(NOT, "!")));
        assertThat(tokens.next(), is(token(LPAREN, "(")));
        assertThat(tokens.next(), is(token(LIT_STR, "\"Hello, %?\"")));
        assertThat(tokens.next(), is(token(COMMA, ",")));
        assertThat(tokens.next(), is(token(WS, " ")));
        assertThat(tokens.next(), is(token(IDENT, "name")));
        assertThat(tokens.next(), is(token(RPAREN, ")")));
        assertThat(tokens.next(), is(token(RPAREN, ")")));
        assertThat(tokens.next(), is(token(SEMI, ";")));
        assertThat(tokens.next(), is(token(WS, "\n")));
        assertThat(tokens.next(), is(token(RBRACE, "}")));
        assertThat(tokens.next(), is(token(WS, "\n")));
        assertThat(tokens.next(), is(token(EOF)));
        assertThat(tokens.hasNext(), is(false));
    }

    private List<Token> tokenize(CharSequence input) {
        RustLexer lexer = new RustLexer(new ANTLRInputStream(input.toString()));
        List<Token> tokens = new LinkedList<Token>();
        Token token;
        do {
            token = lexer.nextToken();
            tokens.add(token);
        } while (token.getType() != Token.EOF);
        return tokens;
    }

    private Matcher<Token> token(final int expectedType) {
        return token(expectedType, null);
    }

    private Matcher<Token> token(final int expectedType, final String expectedText) {
        return new TypeSafeMatcher<Token>() {
            @Override
            public boolean matchesSafely(Token item) {
                return item.getType() == expectedType && (expectedText == null || item.getText().equals(expectedText));
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Token of type ").appendValue(RustLexer.tokenNames[expectedType]);
                if (expectedText != null) {
                    description.appendText(", with text ").appendValue(expectedText);
                }
            }
        };
    }
}
