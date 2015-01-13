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

import com.github.drrb.rust.netbeans.parsing.RustToken.Type;
import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.TokenId;

public enum RustTokenId implements TokenId {

    IDENT(Type.IDENT, TokenCategory.IDENTIFIER),
    OPEN_DELIM(Type.OPEN_DELIM, TokenCategory.SEPARATOR),
    CLOSE_DELIM(Type.CLOSE_DELIM, TokenCategory.SEPARATOR),
    WHITESPACE(Type.WHITESPACE, TokenCategory.WHITESPACE),
    NOT(Type.NOT, TokenCategory.OPERATOR),
    LITERAL(Type.LITERAL, TokenCategory.LITERAL),
    SEMI(Type.SEMI, TokenCategory.IDENTIFIER),
    EOF(Type.EOF, TokenCategory.WHITESPACE);

    private static final Language<RustTokenId> LANGUAGE = new RustLanguageHierarchy().language();

    public static Language<RustTokenId> language() {
        return LANGUAGE;
    }
    private final RustToken.Type nativeTokenType;
    private final TokenCategory primaryCategory;

    @SuppressWarnings("LeakingThisInConstructor")
    private RustTokenId(RustToken.Type nativeTokenType, TokenCategory primaryCategory) {
        this.nativeTokenType = nativeTokenType;
        this.primaryCategory = primaryCategory;
    }

    public RustToken.Type nativeTokenType() {
        return nativeTokenType;
    }

    @Override
    public String primaryCategory() {
        return primaryCategory.getName();
    }
}
