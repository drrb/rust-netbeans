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

import com.github.drrb.rust.antlr.RustBaseVisitor;
import com.github.drrb.rust.antlr.RustParser;
import com.github.drrb.rust.netbeans.parsing.antlr.AntlrTokenID;
import com.github.drrb.rust.netbeans.parsing.antlr.CommonRustTokenIDs;
import java.util.BitSet;

import java.util.LinkedList;
import java.util.List;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;

public class ParseResult extends JsonSerializable {

    public static class Error extends JsonSerializable {

        public final String tokenKind;
        public final int beginLine;
        public final int beginColumn;

        public Error(TokenizationResult.Token unexpectedToken, String message) {
            tokenKind = unexpectedToken.kind;
            beginLine = unexpectedToken.beginLine;
            beginColumn = unexpectedToken.beginColumn;
        }
    }

    public List<Error> errors = new LinkedList<>();

    public ParseResult(RustParser parser) {
        parser.addErrorListener(new ANTLRErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                Token t = e == null ? null : e.getOffendingToken();
                if (offendingSymbol instanceof CommonToken) {
                    t = (CommonToken) offendingSymbol;
                }
                TokenizationResult.Token mdl;
                if (t == null) {
                    AntlrTokenID id = CommonRustTokenIDs.forLiteralName(offendingSymbol.toString());
                    mdl = new TokenizationResult.Token(offendingSymbol.toString(), id == null ? "syntax" : id.name());
                } else {
                    mdl = new TokenizationResult.Token(t.getText(),
                            CommonRustTokenIDs.forTokenType(t.getType()), t.getLine(), t.getCharPositionInLine());
                }
                System.out.println("SyntaxError at " + line + ":" + charPositionInLine + " with " + offendingSymbol);
                Error err = new Error(mdl, e == null ? "syntax error" : e.getMessage());
                errors.add(err);
            }

            @Override
            public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, boolean exact, BitSet ambigAlts, ATNConfigSet configs) {
                // do nothing
            }

            @Override
            public void reportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex, int stopIndex, BitSet conflictingAlts, ATNConfigSet configs) {
                // do nothing
            }

            @Override
            public void reportContextSensitivity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, int prediction, ATNConfigSet configs) {
                // do nothing
            }
        });
        ((CommonTokenStream) parser.getTokenStream()).fill();
        parser.crate().accept(new RustBaseVisitor());
    }

}
