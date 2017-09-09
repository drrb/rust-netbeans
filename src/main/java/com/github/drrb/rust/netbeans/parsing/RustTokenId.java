/**
 * Copyright (C) 2015 drrb
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

import com.github.drrb.rust.netbeans.parsing.javacc.RustTokenKind;
import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.TokenId;

public enum RustTokenId implements TokenId {
    EOF(TokenCategory.WHITESPACE),
    WHITESPACE(TokenCategory.WHITESPACE),
    DOC_COMMENT(TokenCategory.COMMENT),
    INNER_DOC_COMMENT(TokenCategory.COMMENT),
    LINE_COMMENT(TokenCategory.COMMENT),
    BLOCK_COMMENT(TokenCategory.COMMENT),
    DOC_BLOCK_COMMENT(TokenCategory.COMMENT),
    INNER_DOC_BLOCK_COMMENT(TokenCategory.COMMENT),
    NON_NULL(TokenCategory.IDENTIFIER),
    NON_SINGLE_QUOTE(TokenCategory.IDENTIFIER),
    NON_DOUBLE_QUOTE(TokenCategory.IDENTIFIER),
    NON_EOL(TokenCategory.IDENTIFIER),
    ASCII(TokenCategory.IDENTIFIER),
    ASCII_NON_EOL(TokenCategory.IDENTIFIER),
    ASCII_NON_SINGLE_QUOTE(TokenCategory.IDENTIFIER),
    ASCII_NON_DOUBLE_QUOTE(TokenCategory.IDENTIFIER),
    STRING_LITERAL(TokenCategory.STRING),
    RAW_STRING_LITERAL(TokenCategory.STRING),
    CHAR_LITERAL(TokenCategory.CHARACTER),
    NUMBER_LITERAL(TokenCategory.NUMBER),
    BYTE_LITERAL(TokenCategory.CHARACTER),
    BYTE_STRING_LITERAL(TokenCategory.STRING),
    RAW_BYTE_STRING_LITERAL(TokenCategory.STRING),
    STRING_BODY(TokenCategory.STRING),
    CHAR_BODY(TokenCategory.CHARACTER),
    BYTE_BODY(TokenCategory.IDENTIFIER),
    COMMON_ESCAPE(TokenCategory.IDENTIFIER),
    UNICODE_ESCAPE(TokenCategory.IDENTIFIER),
    FLOAT_SUFFIX(TokenCategory.NUMBER),
    EXPONENT(TokenCategory.NUMBER),
    DEC_LIT(TokenCategory.NUMBER),
    HEX_DIGIT(TokenCategory.NUMBER),
    OCT_DIGIT(TokenCategory.NUMBER),
    DEC_DIGIT(TokenCategory.NUMBER),
    NONZERO_DEC(TokenCategory.NUMBER),
    RAW_STRING_LITERAL_3(TokenCategory.STRING),
    RAW_STRING_LITERAL_2(TokenCategory.STRING),
    RAW_STRING_LITERAL_1(TokenCategory.STRING),
    RAW_STRING_LITERAL_0(TokenCategory.STRING),
    RAW_BYTE_STRING_LITERAL_3(TokenCategory.STRING),
    RAW_BYTE_STRING_LITERAL_2(TokenCategory.STRING),
    RAW_BYTE_STRING_LITERAL_1(TokenCategory.STRING),
    RAW_BYTE_STRING_LITERAL_0(TokenCategory.STRING),
    DOUBLE_COLON(TokenCategory.SEPARATOR),
    ARROW(TokenCategory.SEPARATOR),
    HASH(TokenCategory.SEPARATOR),
    LEFT_BRACKET(TokenCategory.SEPARATOR),
    RIGHT_BRACKET(TokenCategory.SEPARATOR),
    LEFT_PAREN(TokenCategory.SEPARATOR),
    RIGHT_PAREN(TokenCategory.SEPARATOR),
    LEFT_BRACE(TokenCategory.SEPARATOR),
    RIGHT_BRACE(TokenCategory.SEPARATOR),
    COMMA(TokenCategory.SEPARATOR),
    COLON(TokenCategory.SEPARATOR),
    PLUS(TokenCategory.OPERATOR),
    DASH(TokenCategory.OPERATOR),
    STAR(TokenCategory.OPERATOR),
    FORWARD_SLASH(TokenCategory.OPERATOR),
    PERCENT(TokenCategory.OPERATOR),
    AMPERSAND(TokenCategory.OPERATOR),
    PIPE(TokenCategory.OPERATOR),
    HAT(TokenCategory.OPERATOR),
    DOUBLE_AMPERSAND(TokenCategory.OPERATOR),
    DOUBLE_PIPE(TokenCategory.OPERATOR),
    LEFT_ANGLE_BRACKET(TokenCategory.SEPARATOR),
    RIGHT_ANGLE_BRACKET(TokenCategory.SEPARATOR),
    SHIFT_LEFT(TokenCategory.OPERATOR),
    SHIFT_RIGHT(TokenCategory.OPERATOR),
    LESS_THAN_EQUAL(TokenCategory.OPERATOR),
    GREATER_THAN_EQUAL(TokenCategory.OPERATOR),
    SEMICOLON(TokenCategory.SEPARATOR),
    DOUBLE_EQUALS(TokenCategory.OPERATOR),
    NOT_EQUAL(TokenCategory.OPERATOR),
    BANG(TokenCategory.OPERATOR),
    EQUALS(TokenCategory.OPERATOR),
    DOT(TokenCategory.SEPARATOR),
    DOLLAR(TokenCategory.SEPARATOR),
    HASH_ROCKET(TokenCategory.SEPARATOR),
    ABSTRACT(TokenCategory.IDENTIFIER),
    ALIGNOF(TokenCategory.IDENTIFIER),
    AS(TokenCategory.IDENTIFIER),
    BECOME(TokenCategory.IDENTIFIER),
    BOX(TokenCategory.IDENTIFIER),
    BREAK(TokenCategory.IDENTIFIER),
    CONST(TokenCategory.IDENTIFIER),
    CONTINUE(TokenCategory.IDENTIFIER),
    CRATE(TokenCategory.IDENTIFIER),
    DO(TokenCategory.IDENTIFIER),
    ELSE(TokenCategory.IDENTIFIER),
    ENUM(TokenCategory.IDENTIFIER),
    EXTERN(TokenCategory.IDENTIFIER),
    FALSE(TokenCategory.IDENTIFIER),
    FINAL(TokenCategory.IDENTIFIER),
    FN(TokenCategory.KEYWORD),
    FOR(TokenCategory.IDENTIFIER),
    IF(TokenCategory.IDENTIFIER),
    IMPL(TokenCategory.IDENTIFIER),
    IN(TokenCategory.IDENTIFIER),
    LET(TokenCategory.IDENTIFIER),
    LOOP(TokenCategory.IDENTIFIER),
    MACRO(TokenCategory.IDENTIFIER),
    MACRO_RULES(TokenCategory.IDENTIFIER),
    MATCH(TokenCategory.IDENTIFIER),
    MOD(TokenCategory.IDENTIFIER),
    MOVE(TokenCategory.IDENTIFIER),
    MUT(TokenCategory.IDENTIFIER),
    OFFSETOF(TokenCategory.IDENTIFIER),
    OVERRIDE(TokenCategory.IDENTIFIER),
    PRIV(TokenCategory.IDENTIFIER),
    PROC(TokenCategory.IDENTIFIER),
    PUB(TokenCategory.IDENTIFIER),
    PURE(TokenCategory.IDENTIFIER),
    REF(TokenCategory.IDENTIFIER),
    RETURN(TokenCategory.IDENTIFIER),
    BIG_SELF(TokenCategory.IDENTIFIER),
    SELF(TokenCategory.IDENTIFIER),
    SIZEOF(TokenCategory.IDENTIFIER),
    STATIC(TokenCategory.IDENTIFIER),
    STRUCT(TokenCategory.IDENTIFIER),
    SUPER(TokenCategory.IDENTIFIER),
    TRAIT(TokenCategory.IDENTIFIER),
    TRUE(TokenCategory.IDENTIFIER),
    TYPE(TokenCategory.IDENTIFIER),
    TYPEOF(TokenCategory.IDENTIFIER),
    UNSAFE(TokenCategory.IDENTIFIER),
    UNSIZED(TokenCategory.IDENTIFIER),
    USE(TokenCategory.IDENTIFIER),
    VIRTUAL(TokenCategory.IDENTIFIER),
    WHERE(TokenCategory.IDENTIFIER),
    WHILE(TokenCategory.IDENTIFIER),
    YIELD(TokenCategory.IDENTIFIER),
    IDENTIFIER(TokenCategory.IDENTIFIER),
    GARBAGE(TokenCategory.IDENTIFIER);

    private static final Language<RustTokenId> LANGUAGE = new RustLanguageHierarchy().language();

    public static Language<RustTokenId> language() {
        return LANGUAGE;
    }
    private final TokenCategory primaryCategory;

    RustTokenId(TokenCategory primaryCategory) {
        this.primaryCategory = primaryCategory;
    }

    public static RustTokenId get(RustTokenKind rustTokenKind) {
        return RustTokenId.valueOf(rustTokenKind.name());
    }

    @Override
    public String primaryCategory() {
        return primaryCategory.getName();
    }
}
