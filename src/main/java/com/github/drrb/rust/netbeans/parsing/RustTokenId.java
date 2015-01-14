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

import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.TokenId;

public enum RustTokenId implements TokenId {
    EQ(TokenCategory.OPERATOR),
    LT(TokenCategory.OPERATOR),
    LE(TokenCategory.OPERATOR),
    EQ_EQ(TokenCategory.OPERATOR),
    NE(TokenCategory.OPERATOR),
    GE(TokenCategory.OPERATOR),
    GT(TokenCategory.OPERATOR),
    AND_AND(TokenCategory.OPERATOR),
    OR_OR(TokenCategory.OPERATOR),
    NOT(TokenCategory.OPERATOR),
    TILDE(TokenCategory.OPERATOR),
    AT(TokenCategory.OPERATOR),
    DOT(TokenCategory.OPERATOR), //TODO: what is this?
    DOT_DOT(TokenCategory.OPERATOR), //TODO: what is this?
    DOT_DOT_DOT(TokenCategory.OPERATOR), //TODO: what is this?
    COMMA(TokenCategory.SEPARATOR),
    SEMI(TokenCategory.SEPARATOR),
    COLON(TokenCategory.SEPARATOR),
    MOD_SEP(TokenCategory.SEPARATOR),
    R_ARROW(TokenCategory.SEPARATOR),
    L_ARROW(TokenCategory.SEPARATOR),
    FAT_ARROW(TokenCategory.SEPARATOR),
    POUND(TokenCategory.SEPARATOR),
    DOLLAR(TokenCategory.OPERATOR),
    QUESTION(TokenCategory.OPERATOR),
    IDENT(TokenCategory.IDENTIFIER),
    UNDERSCORE(TokenCategory.IDENTIFIER),
    LIFETIME(TokenCategory.IDENTIFIER),
    INTERPOLATED(TokenCategory.IDENTIFIER), //TODO: what is this?
    DOC_COMMENT(TokenCategory.COMMENT),
    MATCH_NT(TokenCategory.IDENTIFIER), //TODO: what is this?
    SUBST_NT(TokenCategory.IDENTIFIER), //TODO: what is this?
    SPECIAL_VAR_NT(TokenCategory.IDENTIFIER), //TODO: what is this?
    WHITESPACE(TokenCategory.WHITESPACE),
    COMMENT(TokenCategory.COMMENT),
    SHEBANG(TokenCategory.COMMENT),
    EOF(TokenCategory.WHITESPACE),
    PLUS(TokenCategory.OPERATOR),
    MINUS(TokenCategory.OPERATOR),
    STAR(TokenCategory.OPERATOR),
    SLASH(TokenCategory.OPERATOR),
    PERCENT(TokenCategory.OPERATOR),
    CARET(TokenCategory.OPERATOR),
    AND(TokenCategory.OPERATOR),
    OR(TokenCategory.OPERATOR),
    SHL(TokenCategory.OPERATOR),
    SHR(TokenCategory.OPERATOR),
    OPEN_PAREN(TokenCategory.SEPARATOR),
    OPEN_BRACKET(TokenCategory.SEPARATOR),
    OPEN_BRACE(TokenCategory.SEPARATOR),
    CLOSE_PAREN(TokenCategory.SEPARATOR),
    CLOSE_BRACKET(TokenCategory.SEPARATOR),
    CLOSE_BRACE(TokenCategory.SEPARATOR),
    BYTE_LITERAL(TokenCategory.LITERAL),
    CHAR_LITERAL(TokenCategory.CHARACTER),
    INTEGER_LITERAL(TokenCategory.NUMBER),
    FLOAT_LITERAL(TokenCategory.NUMBER),
    STR_LITERAL(TokenCategory.STRING),
    STR_RAW_LITERAL(TokenCategory.STRING),
    BINARY_LITERAL(TokenCategory.NUMBER),
    BINARY_RAW_LITERAL(TokenCategory.NUMBER);

    private static final Language<RustTokenId> LANGUAGE = new RustLanguageHierarchy().language();

    public static Language<RustTokenId> language() {
        return LANGUAGE;
    }
    private final TokenCategory primaryCategory;

    private RustTokenId(TokenCategory primaryCategory) {
        this.primaryCategory = primaryCategory;
    }

    @Override
    public String primaryCategory() {
        return primaryCategory.getName();
    }
}
