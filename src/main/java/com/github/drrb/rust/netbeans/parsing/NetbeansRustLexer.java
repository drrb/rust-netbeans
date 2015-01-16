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

import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.lexer.Token;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.LexerRestartInfo;

public class NetbeansRustLexer implements Lexer<RustTokenId> {
    private static final Logger LOGGER = Logger.getLogger(NetbeansRustLexer.class.getName());
    private final LexerRestartInfo<RustTokenId> info;
    private RustLexer lexer;

    public NetbeansRustLexer(LexerRestartInfo<RustTokenId> info) {
        this.info = info;
    }

    @Override
    public Token<RustTokenId> nextToken() {
        ensureLexerCreated();
        RustToken.ByValue token = lexer.nextToken();
        LOGGER.log(Level.INFO, "Next token = {0}", token);
        if (token.getType() == RustTokenId.EOF) {
            return null;
        } else {
            LOGGER.log(Level.INFO, "Reading {0} tokens", token.length());
            for (int i = 0; i < token.length(); i++) {
                info.input().read();
                LOGGER.info("  - Read so far: <" + info.input().readText() + ">");
            }
            LOGGER.info("Claiming that " + token.getType() + " is <" + info.input().readText() + "> and has length " + info.input().readLength());
            return info.tokenFactory().createToken(token.getType());
        }
    }

    private void ensureLexerCreated() {
        if (lexer != null) return;
        LexerInput input = info.input();
        LOGGER.info("About to start reading");
        reading: while(input.read() != LexerInput.EOF) {
            continue reading;
        }
        String source = input.readText().toString();
        System.out.println("read " + source.length() + " characters");
        System.out.println("backing up " + input.readLengthEOF() + " characters");
        input.backup(input.readLengthEOF());
        LOGGER.info("read source: " + source);
        this.lexer = RustLexer.forString(source);
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
