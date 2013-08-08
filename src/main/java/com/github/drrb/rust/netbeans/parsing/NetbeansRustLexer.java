/**
 * Copyright (C) 2013 drrb
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.drrb.rust.netbeans.parsing;

import org.netbeans.api.lexer.Token;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;

public class NetbeansRustLexer implements Lexer<RustTokenId> {
    private final RustLexer lexer;
    private final LexerRestartInfo<RustTokenId> info;

    public NetbeansRustLexer(LexerRestartInfo<RustTokenId> info) {
        AntlrCharStream charStream = new AntlrCharStream(info.input(), "RustEditor");
        this.lexer = new RustLexer(charStream);
        this.info = info;
    }

    @Override
    public Token<RustTokenId> nextToken() {
        org.antlr.v4.runtime.Token token = lexer.nextToken();
        if (token.getType() != RustLexer.EOF) {
            RustTokenId tokenId = RustLanguageHierarchy.tokenForAntlrTokenType(token.getType());
            return info.tokenFactory().createToken(tokenId);
        }
        return null;
    }

    @Override
    public Object state() {
        //Tutorial returned null
        return null;
    }

    @Override
    public void release() {
        //Tutorial left this blank
    }

}
