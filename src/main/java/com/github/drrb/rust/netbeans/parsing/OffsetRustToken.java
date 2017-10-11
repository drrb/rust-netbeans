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

import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.csl.api.OffsetRange;

/**
 *
 */
public class OffsetRustToken {

    public static OffsetRustToken atCurrentLocation(TokenSequence<RustTokenId> tokenSequence) {
        return new OffsetRustToken(tokenSequence.offsetToken());
    }
    private final Token<RustTokenId> token;

    private OffsetRustToken(Token<RustTokenId> offsetToken) {
        this.token = offsetToken;
    }

    public RustTokenId id() {
        return token.id();
    }

    public OffsetRange getRangeIn(TokenHierarchy<?> tokenHierarchy) {
        int tokenOffset = token.offset(tokenHierarchy);
        return new OffsetRange(tokenOffset, tokenOffset + token.length());
    }

    public CharSequence text() {
        return token.text();
    }
}
