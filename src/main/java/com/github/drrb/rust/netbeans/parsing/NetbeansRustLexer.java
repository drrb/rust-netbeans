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

import com.github.drrb.rust.netbeans.parsing.javacc.JavaccCharStream;
import com.github.drrb.rust.netbeans.parsing.javacc.RustParserTokenManager;
import com.github.drrb.rust.netbeans.parsing.javacc.TokenMgrError;
import com.github.drrb.rust.netbeans.rustbridge.RustLexer;
import org.netbeans.api.lexer.Token;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.LexerRestartInfo;

import java.util.Stack;
import java.util.stream.IntStream;

public class NetbeansRustLexer implements Lexer<RustTokenId> {

    private static final Token<RustTokenId> EOF_TOKEN = null;

    private final LexerRestartInfo<RustTokenId> info;
    private final RustParserTokenManager tokenManager;
    private final Stack<com.github.drrb.rust.netbeans.parsing.javacc.RustToken> unreturnedTokens = new Stack<>();
    private final LexerInput lexerInput;
    private RustLexer lexer = RustLexer.NULL_LEXER;

    public NetbeansRustLexer(LexerRestartInfo<RustTokenId> info) {
        this.info = info;
        this.lexerInput = info.input();
        this.tokenManager = new RustParserTokenManager(new JavaccCharStream(info.input()));
    }

    @Override
    public Token<RustTokenId> nextToken() {
        try {
            com.github.drrb.rust.netbeans.parsing.javacc.RustToken token;
            if (unreturnedTokens.empty()) {
                token = (com.github.drrb.rust.netbeans.parsing.javacc.RustToken) tokenManager.getNextToken();
                log("parsed token: %s%n", token);
                while (token.hasSpecialToken()) {
                    if (token.isEof()) {
                        info.input().backup(1);
                    }
                    log("  token %s has special token %s. pushing %s%n", token, token.specialToken(), token);
                    unreturnedTokens.push(token);
                    log("    backing up %s%n", token.image.length());
                    lexerInput.backup(token.image.length());
                    token = token.specialToken();
                }
            } else {
                token = unreturnedTokens.pop();
                log("using unreturned token %s%n", token);
                IntStream.range(0, token.image.length()).forEach(x -> lexerInput.read());
            }

            log("token = %s%n", token);
            log("  read = %s%n", lexerInput.readLength());
            if (token.isEof()) {
                return EOF_TOKEN;
            } else {
                return info.tokenFactory().createToken(RustTokenId.get(token.kind));
            }
        } catch (TokenMgrError e) {
            return info.tokenFactory().createToken(RustTokenId.GARBAGE);
        }
    }

    private void log(String format, Object... args) {
        //System.out.format(format, args);
    }

    private void ensureLexerCreated() {
        if (lexer == RustLexer.NULL_LEXER) {
            String source = readWholeSource();
            backUp(charsReadThisToken());
            lexer = RustLexer.forString(source);
        }
    }

    private String readWholeSource() {
        reading:
        while (readOneCharacter() != LexerInput.EOF) {
            continue reading;
        }
        return charactersReadSoFar();
    }

    protected void backUp(int length) {
        info.input().backup(length);
    }

    protected int charsReadThisToken() {
        return info.input().readLengthEOF();
    }

    protected String charactersReadSoFar() {
        return info.input().readText().toString();
    }

    protected int readOneCharacter() {
        return info.input().read();
    }

    protected Token<RustTokenId> createToken(RustTokenId tokenType) {
        return info.tokenFactory().createToken(tokenType);
    }

    @Override
    public Object state() {
        return null;
    }

    @Override
    public void release() {
        //lexer.release();
    }

}
