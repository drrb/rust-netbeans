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

/**
 *
 */
public class RustLexer {
    public static final RustLexer NULL_LEXER = new RustLexer((NativeRustLexer) null) {
        @Override public void release() {}

        @Override
        public RustToken.ByValue nextToken() {
            RustToken.ByValue token = new RustToken.ByValue();
            token.type = RustTokenId.EOF.ordinal();
            return token;
        }

    };

    public static RustLexer forString(String input) {
        if (input.isEmpty()) {
            return NULL_LEXER;
        } else {
            return new RustLexer(input);
        }
    }

    private final NativeRustLexer peer;

    public RustLexer(String input) {
        this(RustNative.INSTANCE.createLexer(input));
    }

    public RustLexer(NativeRustLexer peer) {
        this.peer = peer;
    }

    public RustToken nextToken() {
        RustNative.TokenHolder tokenHolder = new RustNative.TokenHolder();
        RustNative.INSTANCE.getNextToken(peer, tokenHolder);
        return tokenHolder.getToken();
    }

    public void release() {
        RustNative.INSTANCE.destroyLexer(peer);
    }
}
