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

import org.netbeans.api.lexer.Token;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.LexerRestartInfo;

public class NetbeansRustLexer implements Lexer<RustTokenId> {

    private final LexerRestartInfo<RustTokenId> info;
    private RustLexer lexer;

    public NetbeansRustLexer(LexerRestartInfo<RustTokenId> info) {
        this.info = info;
    }

    @Override
    public Token<RustTokenId> nextToken() {
        ensureLexerCreated();
        RustToken token = lexer.nextToken();
        if (token == null) {
            readWholeSource();
            return createToken(RustTokenId.GARBAGE);
        } else if (token.getType() == RustTokenId.EOF) {
            return null;
        } else {
            for (int i = 0; i < token.length(); i++) {
                readOneCharacter();
            }
            return createToken(token.getType());
        }
    }

    private void ensureLexerCreated() {
        if (lexer != null) return;
        String source = readWholeSource();
        backUp(charsReadThisToken());
        lexer = RustLexer.forString(source);
    }

    private String readWholeSource() {
        reading: while (readOneCharacter() != LexerInput.EOF) {
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
        //Tutorial returned null
        return null;
    }

    @Override
    public void release() {
        lexer.release();
    }

}
