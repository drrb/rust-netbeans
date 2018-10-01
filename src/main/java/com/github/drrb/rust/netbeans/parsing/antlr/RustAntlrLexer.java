/**
 * Copyright (C) 2018 Tim Boudreau
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
package com.github.drrb.rust.netbeans.parsing.antlr;

import com.github.drrb.rust.antlr.RustLexer;
import java.util.regex.Pattern;
import org.antlr.v4.runtime.CharStreams;
import org.netbeans.api.lexer.PartType;
import org.netbeans.api.lexer.Token;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;

/**
 *
 * @author Tim Boudreau
 */
public class RustAntlrLexer implements Lexer<AntlrTokenID> {

    public static com.github.drrb.rust.netbeans.rustbridge.RustLexer forString(String input) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private final AntlrTokenIDs tokenIds;
    private LexerRestartInfo<AntlrTokenID> info;
    private RustLexer lexer;
    public RustAntlrLexer() {
        tokenIds = AntlrTokenIDs.forVocabulary(RustLexer.VOCABULARY, RustAntlrLexer::categoryFor);
    }

    RustAntlrLexer(LexerRestartInfo<AntlrTokenID> info) {
        tokenIds = AntlrTokenIDs.forVocabulary(RustLexer.VOCABULARY, RustAntlrLexer::categoryFor);
        this.info = info;
        lexer = new RustLexer( new AntlrStreamAdapter( info.input(), "RustAntlrLexer" ) );
//        lexer = new RustLexer(CharStreams.fromString(info.input().readText().toString()));
    }

    public static RustLexer fromString(String s) {
        return new RustLexer(CharStreams.fromString(s));
    }

    @Override
    public Token<AntlrTokenID> nextToken() {
        org.antlr.v4.runtime.Token antlrToken = lexer.nextToken();
        AntlrTokenID id;
        if ( antlrToken.getType() == RustLexer.EOF && antlrToken.getStopIndex() < antlrToken.getStartIndex() ) {
            return null;
        }
//        if ( info.input().readLength() < 1 ) {
//            return null; // XXX eof?
//        }
        assert antlrToken.getType() <= RustLexer.VOCABULARY.getMaxTokenType();
        id = tokenIds.get( antlrToken.getType() );
        Token<AntlrTokenID> tok = info.tokenFactory().createToken( id, ( antlrToken.getStopIndex()
                - antlrToken.getStartIndex() ) + 1, PartType.COMPLETE );
        return tok;
    }

    @Override
    public Object state() {
        return null;
    }

    @Override
    public void release() {
        lexer = null;
        info = null;
    }

    private static final Pattern WORD = Pattern.compile("^[a-zA-Z]+$");
    static String categoryFor(int tokenType, String displayName, String symbolicName, String literalName) {
        if (tokenType == 0) {
            return "eof";
        }
        if (literalName != null && WORD.matcher(literalName).lookingAt()) {
            return "keyword";
        } else if (literalName != null && literalName.length() == 1 && !Character.isAlphabetic(literalName.charAt(0))) {
            switch (literalName.charAt(0)) {
                case '*':
                case '/':
                case '%':
                case '+':
                case '-':
                case '^':
                case '|':
                case '&':
                    return "operator";
                case '.':
                case '{':
                case '}':
                case '(':
                case ')':
                case '[':
                case ']':
                case ',':
                    return "delimiter";
                case '<':
                case '>':
                    return "comparisonOperator";
                case '=':
                    return "assignmentOperator";
            }
            return "symbol";
        } else if (literalName != null && literalName.length() == 2 && !Character.isAlphabetic(literalName.charAt(0)) && !Character.isAlphabetic(literalName.charAt(1))) {
            switch (literalName) {
                case "::":
                case "=>":
                    return "delimiter";
                case "+=":
                case "-=":
                case "/=":
                case "*=":
                case "%=":
                case "|=":
                case "&=":
                case "^=":
                    return "assignmentOperator";
                case "==":
                    return "comparisonOperator";

            }
            return "symbol";
        } else if (literalName != null && literalName.length() == 3 && !Character.isAlphabetic(literalName.charAt(0)) && !Character.isAlphabetic(literalName.charAt(1)) && !Character.isAlphabetic(literalName.charAt(2))) {
            switch (literalName) {
                case "<<=":
                case ">>=":
                    return "assignmentOperator";
            }
            return "symbol";
        } else if (symbolicName != null) {
            if (symbolicName.endsWith("Comment")) {
                return "comment";
            } else if (symbolicName.endsWith("Lit")) {
                return "literal";
            }
            switch (symbolicName) {
                case "Lifetime":
                    return "keyword";
                case "Whitespace":
                    return "whitespace";
                case "Ident":
                    return "identifier";
            }
        }
        return "other";
    }
}
