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

import com.github.drrb.rust.netbeans.parsing.javacc.RustParserConstants;
import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.TokenId;

public enum RustTokenId implements TokenId {

    EOF(RustParserConstants.EOF, TokenCategory.WHITESPACE),
    WHITESPACE(RustParserConstants.WHITESPACE, TokenCategory.WHITESPACE),
    DOC_COMMENT(RustParserConstants.DOC_COMMENT, TokenCategory.COMMENT),
    INNER_DOC_COMMENT(RustParserConstants.INNER_DOC_COMMENT, TokenCategory.COMMENT),
    LINE_COMMENT(RustParserConstants.LINE_COMMENT, TokenCategory.COMMENT),
    BLOCK_COMMENT(RustParserConstants.BLOCK_COMMENT, TokenCategory.COMMENT),
    DOC_BLOCK_COMMENT(RustParserConstants.DOC_BLOCK_COMMENT, TokenCategory.COMMENT),
    INNER_DOC_BLOCK_COMMENT(RustParserConstants.INNER_DOC_BLOCK_COMMENT, TokenCategory.COMMENT),
    NON_NULL(RustParserConstants.NON_NULL, TokenCategory.IDENTIFIER),
    NON_SINGLE_QUOTE(RustParserConstants.NON_SINGLE_QUOTE, TokenCategory.IDENTIFIER),
    NON_DOUBLE_QUOTE(RustParserConstants.NON_DOUBLE_QUOTE, TokenCategory.IDENTIFIER),
    NON_EOL(RustParserConstants.NON_EOL, TokenCategory.IDENTIFIER),
    ASCII(RustParserConstants.ASCII, TokenCategory.IDENTIFIER),
    ASCII_NON_EOL(RustParserConstants.ASCII_NON_EOL, TokenCategory.IDENTIFIER),
    ASCII_NON_SINGLE_QUOTE(RustParserConstants.ASCII_NON_SINGLE_QUOTE, TokenCategory.IDENTIFIER),
    ASCII_NON_DOUBLE_QUOTE(RustParserConstants.ASCII_NON_DOUBLE_QUOTE, TokenCategory.IDENTIFIER),
    STRING_LITERAL(RustParserConstants.STRING_LITERAL, TokenCategory.STRING),
    RAW_STRING_LITERAL(RustParserConstants.RAW_STRING_LITERAL, TokenCategory.STRING),
    CHAR_LITERAL(RustParserConstants.CHAR_LITERAL, TokenCategory.CHARACTER),
    NUMBER_LITERAL(RustParserConstants.NUMBER_LITERAL, TokenCategory.NUMBER),
    BYTE_LITERAL(RustParserConstants.BYTE_LITERAL, TokenCategory.CHARACTER),
    BYTE_STRING_LITERAL(RustParserConstants.BYTE_STRING_LITERAL, TokenCategory.STRING),
    RAW_BYTE_STRING_LITERAL(RustParserConstants.RAW_BYTE_STRING_LITERAL, TokenCategory.STRING),
    STRING_BODY(RustParserConstants.STRING_BODY, TokenCategory.STRING),
    CHAR_BODY(RustParserConstants.CHAR_BODY, TokenCategory.CHARACTER),
    BYTE_BODY(RustParserConstants.BYTE_BODY, TokenCategory.IDENTIFIER),
    COMMON_ESCAPE(RustParserConstants.COMMON_ESCAPE, TokenCategory.IDENTIFIER),
    UNICODE_ESCAPE(RustParserConstants.UNICODE_ESCAPE, TokenCategory.IDENTIFIER),
    FLOAT_SUFFIX(RustParserConstants.FLOAT_SUFFIX, TokenCategory.NUMBER),
    EXPONENT(RustParserConstants.EXPONENT, TokenCategory.NUMBER),
    DEC_LIT(RustParserConstants.DEC_LIT, TokenCategory.NUMBER),
    HEX_DIGIT(RustParserConstants.HEX_DIGIT, TokenCategory.NUMBER),
    OCT_DIGIT(RustParserConstants.OCT_DIGIT, TokenCategory.NUMBER),
    DEC_DIGIT(RustParserConstants.DEC_DIGIT, TokenCategory.NUMBER),
    NONZERO_DEC(RustParserConstants.NONZERO_DEC, TokenCategory.NUMBER),
    RAW_STRING_LITERAL_3(RustParserConstants.RAW_STRING_LITERAL_3, TokenCategory.STRING),
    RAW_STRING_LITERAL_2(RustParserConstants.RAW_STRING_LITERAL_2, TokenCategory.STRING),
    RAW_STRING_LITERAL_1(RustParserConstants.RAW_STRING_LITERAL_1, TokenCategory.STRING),
    RAW_STRING_LITERAL_0(RustParserConstants.RAW_STRING_LITERAL_0, TokenCategory.STRING),
    RAW_BYTE_STRING_LITERAL_3(RustParserConstants.RAW_BYTE_STRING_LITERAL_3, TokenCategory.STRING),
    RAW_BYTE_STRING_LITERAL_2(RustParserConstants.RAW_BYTE_STRING_LITERAL_2, TokenCategory.STRING),
    RAW_BYTE_STRING_LITERAL_1(RustParserConstants.RAW_BYTE_STRING_LITERAL_1, TokenCategory.STRING),
    RAW_BYTE_STRING_LITERAL_0(RustParserConstants.RAW_BYTE_STRING_LITERAL_0, TokenCategory.STRING),
    DOUBLE_COLON(RustParserConstants.DOUBLE_COLON, TokenCategory.SEPARATOR),
    ARROW(RustParserConstants.ARROW, TokenCategory.SEPARATOR),
    DOUBLE_ARROW(RustParserConstants.DOUBLE_ARROW, TokenCategory.OPERATOR),
    HASH(RustParserConstants.HASH, TokenCategory.SEPARATOR),
    LEFT_BRACKET(RustParserConstants.LEFT_BRACKET, TokenCategory.SEPARATOR),
    RIGHT_BRACKET(RustParserConstants.RIGHT_BRACKET, TokenCategory.SEPARATOR),
    LEFT_PAREN(RustParserConstants.LEFT_PAREN, TokenCategory.SEPARATOR),
    RIGHT_PAREN(RustParserConstants.RIGHT_PAREN, TokenCategory.SEPARATOR),
    LEFT_BRACE(RustParserConstants.LEFT_BRACE, TokenCategory.SEPARATOR),
    RIGHT_BRACE(RustParserConstants.RIGHT_BRACE, TokenCategory.SEPARATOR),
    COMMA(RustParserConstants.COMMA, TokenCategory.SEPARATOR),
    COLON(RustParserConstants.COLON, TokenCategory.SEPARATOR),
    PLUS(RustParserConstants.PLUS, TokenCategory.OPERATOR),
    DASH(RustParserConstants.DASH, TokenCategory.OPERATOR),
    STAR(RustParserConstants.STAR, TokenCategory.OPERATOR),
    FORWARD_SLASH(RustParserConstants.FORWARD_SLASH, TokenCategory.OPERATOR),
    PERCENT(RustParserConstants.PERCENT, TokenCategory.OPERATOR),
    AMPERSAND(RustParserConstants.AMPERSAND, TokenCategory.OPERATOR),
    PIPE(RustParserConstants.PIPE, TokenCategory.OPERATOR),
    HAT(RustParserConstants.HAT, TokenCategory.OPERATOR),
    DOUBLE_AMPERSAND(RustParserConstants.DOUBLE_AMPERSAND, TokenCategory.OPERATOR),
    DOUBLE_PIPE(RustParserConstants.DOUBLE_PIPE, TokenCategory.OPERATOR),
    LEFT_ANGLE_BRACKET(RustParserConstants.LEFT_ANGLE_BRACKET, TokenCategory.SEPARATOR),
    RIGHT_ANGLE_BRACKET(RustParserConstants.RIGHT_ANGLE_BRACKET, TokenCategory.SEPARATOR),
    SHIFT_LEFT(RustParserConstants.SHIFT_LEFT, TokenCategory.OPERATOR),
    SHIFT_RIGHT(RustParserConstants.SHIFT_RIGHT, TokenCategory.OPERATOR),
    LESS_THAN_EQUAL(RustParserConstants.LESS_THAN_EQUAL, TokenCategory.OPERATOR),
    GREATER_THAN_EQUAL(RustParserConstants.GREATER_THAN_EQUAL, TokenCategory.OPERATOR),
    SEMICOLON(RustParserConstants.SEMICOLON, TokenCategory.SEPARATOR),
    DOUBLE_EQUALS(RustParserConstants.DOUBLE_EQUALS, TokenCategory.OPERATOR),
    NOT_EQUAL(RustParserConstants.NOT_EQUAL, TokenCategory.OPERATOR),
    PLUS_EQUALS(RustParserConstants.PLUS_EQUALS, TokenCategory.OPERATOR),
    MINUS_EQUALS(RustParserConstants.MINUS_EQUALS, TokenCategory.OPERATOR),
    TIMES_EQUALS(RustParserConstants.TIMES_EQUALS, TokenCategory.OPERATOR),
    DIVIDE_EQUALS(RustParserConstants.DIVIDE_EQUALS, TokenCategory.OPERATOR),
    MOD_EQUALS(RustParserConstants.MOD_EQUALS, TokenCategory.OPERATOR),
    AND_EQUALS(RustParserConstants.AND_EQUALS, TokenCategory.OPERATOR),
    OR_EQUALS(RustParserConstants.OR_EQUALS, TokenCategory.OPERATOR),
    XOR_EQUALS(RustParserConstants.XOR_EQUALS, TokenCategory.OPERATOR),
    SHIFT_LEFT_EQUALS(RustParserConstants.SHIFT_LEFT_EQUALS, TokenCategory.OPERATOR),
    SHIFT_RIGHT_EQUALS(RustParserConstants.SHIFT_RIGHT_EQUALS, TokenCategory.OPERATOR),
    BANG(RustParserConstants.BANG, TokenCategory.OPERATOR),
    EQUALS(RustParserConstants.EQUALS, TokenCategory.OPERATOR),
    DOT(RustParserConstants.DOT, TokenCategory.SEPARATOR),
    DOUBLE_DOT(RustParserConstants.DOUBLE_DOT, TokenCategory.IDENTIFIER),
    DOLLAR(RustParserConstants.DOLLAR, TokenCategory.SEPARATOR),
    HASH_ROCKET(RustParserConstants.HASH_ROCKET, TokenCategory.SEPARATOR),
    ABSTRACT(RustParserConstants.ABSTRACT, TokenCategory.IDENTIFIER),
    ALIGNOF(RustParserConstants.ALIGNOF, TokenCategory.IDENTIFIER),
    AS(RustParserConstants.AS, TokenCategory.KEYWORD),
    BECOME(RustParserConstants.BECOME, TokenCategory.IDENTIFIER),
    BOX(RustParserConstants.BOX, TokenCategory.IDENTIFIER),
    BREAK(RustParserConstants.BREAK, TokenCategory.KEYWORD),
    CONST(RustParserConstants.CONST, TokenCategory.KEYWORD),
    CONTINUE(RustParserConstants.CONTINUE, TokenCategory.KEYWORD),
    CRATE(RustParserConstants.CRATE, TokenCategory.KEYWORD),
    DO(RustParserConstants.DO, TokenCategory.KEYWORD),
    ELSE(RustParserConstants.ELSE, TokenCategory.KEYWORD),
    ENUM(RustParserConstants.ENUM, TokenCategory.KEYWORD),
    EXTERN(RustParserConstants.EXTERN, TokenCategory.KEYWORD),
    FALSE(RustParserConstants.FALSE, TokenCategory.KEYWORD),
    FINAL(RustParserConstants.FINAL, TokenCategory.KEYWORD),
    FN(RustParserConstants.FN, TokenCategory.KEYWORD),
    FOR(RustParserConstants.FOR, TokenCategory.KEYWORD),
    IF(RustParserConstants.IF, TokenCategory.KEYWORD),
    IMPL(RustParserConstants.IMPL, TokenCategory.KEYWORD),
    IN(RustParserConstants.IN, TokenCategory.KEYWORD),
    LET(RustParserConstants.LET, TokenCategory.KEYWORD),
    LOOP(RustParserConstants.LOOP, TokenCategory.KEYWORD),
    MACRO(RustParserConstants.MACRO, TokenCategory.KEYWORD),
    MACRO_RULES(RustParserConstants.MACRO_RULES, TokenCategory.IDENTIFIER),
    MATCH(RustParserConstants.MATCH, TokenCategory.KEYWORD),
    MOD(RustParserConstants.MOD, TokenCategory.IDENTIFIER),
    MOVE(RustParserConstants.MOVE, TokenCategory.IDENTIFIER),
    MUT(RustParserConstants.MUT, TokenCategory.KEYWORD),
    OFFSETOF(RustParserConstants.OFFSETOF, TokenCategory.IDENTIFIER),
    OVERRIDE(RustParserConstants.OVERRIDE, TokenCategory.IDENTIFIER),
    PRIV(RustParserConstants.PRIV, TokenCategory.KEYWORD),
    PROC(RustParserConstants.PROC, TokenCategory.IDENTIFIER),
    PUB(RustParserConstants.PUB, TokenCategory.KEYWORD),
    PURE(RustParserConstants.PURE, TokenCategory.IDENTIFIER),
    REF(RustParserConstants.REF, TokenCategory.IDENTIFIER),
    RETURN(RustParserConstants.RETURN, TokenCategory.KEYWORD),
    BIG_SELF(RustParserConstants.BIG_SELF, TokenCategory.IDENTIFIER),
    SELF(RustParserConstants.SELF, TokenCategory.KEYWORD),
    SIZEOF(RustParserConstants.SIZEOF, TokenCategory.KEYWORD),
    STATIC(RustParserConstants.STATIC, TokenCategory.KEYWORD),
    STRUCT(RustParserConstants.STRUCT, TokenCategory.KEYWORD),
    SUPER(RustParserConstants.SUPER, TokenCategory.IDENTIFIER),
    TRAIT(RustParserConstants.TRAIT, TokenCategory.KEYWORD),
    TRUE(RustParserConstants.TRUE, TokenCategory.KEYWORD),
    TYPE(RustParserConstants.TYPE, TokenCategory.KEYWORD),
    TYPEOF(RustParserConstants.TYPEOF, TokenCategory.KEYWORD),
    UNSAFE(RustParserConstants.UNSAFE, TokenCategory.KEYWORD),
    UNSIZED(RustParserConstants.UNSIZED, TokenCategory.KEYWORD),
    USE(RustParserConstants.USE, TokenCategory.KEYWORD),
    VIRTUAL(RustParserConstants.VIRTUAL, TokenCategory.KEYWORD),
    WHERE(RustParserConstants.WHERE, TokenCategory.KEYWORD),
    WHILE(RustParserConstants.WHILE, TokenCategory.KEYWORD),
    YIELD(RustParserConstants.YIELD, TokenCategory.KEYWORD),
    IDENTIFIER(RustParserConstants.IDENTIFIER, TokenCategory.IDENTIFIER),
    LABEL(RustParserConstants.LABEL, TokenCategory.IDENTIFIER),
    XID_start(RustParserConstants.XID_start, TokenCategory.IDENTIFIER),
    XID_continue(RustParserConstants.XID_continue, TokenCategory.IDENTIFIER),
    GARBAGE(RustParserConstants.GARBAGE, TokenCategory.IDENTIFIER);

    public static final RustLanguageHierarchy LANGUAGE_HIERARCHY = new RustLanguageHierarchy();
    private static final RustTokenId[] LOOKUP;
    static {
        int highestValue = 0;
        for (RustTokenId kind : values()) {
            highestValue = highestValue > kind.javaccKind ? highestValue : kind.javaccKind;
        }
        LOOKUP = new RustTokenId[highestValue + 1];
        for (RustTokenId kind : values()) {
            LOOKUP[kind.javaccKind] = kind;
        }
    }

    public static Language<RustTokenId> language() {
        return LANGUAGE_HIERARCHY.language();
    }

    private final int javaccKind;
    private final TokenCategory category;

    RustTokenId(int javaccKind, TokenCategory category) {
        this.javaccKind = javaccKind;
        this.category = category;
    }

    public static RustTokenId get(int javaccKind) {
        RustTokenId kind = LOOKUP[javaccKind];
        if (kind == null) {
            throw new IllegalArgumentException("No TokenKind for constant: " + javaccKind);
        }
        return kind;
    }

    @Override
    public String primaryCategory() {
        return category.getName();
    }
}
