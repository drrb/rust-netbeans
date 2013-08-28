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

    AS(RustLexer.AS, TokenCategory.KEYWORD),
    BREAK(RustLexer.BREAK, TokenCategory.KEYWORD),
    CONST(RustLexer.CONST, TokenCategory.KEYWORD),
    COPYTOK(RustLexer.COPYTOK, TokenCategory.KEYWORD),
    DO(RustLexer.DO, TokenCategory.KEYWORD),
    DROP(RustLexer.DROP, TokenCategory.KEYWORD),
    ELSE(RustLexer.ELSE, TokenCategory.KEYWORD),
    ENUM(RustLexer.ENUM, TokenCategory.KEYWORD),
    EXTERN(RustLexer.EXTERN, TokenCategory.KEYWORD),
    FALSE(RustLexer.FALSE, TokenCategory.KEYWORD),
    FN(RustLexer.FN, TokenCategory.KEYWORD),
    FOR(RustLexer.FOR, TokenCategory.KEYWORD),
    IF(RustLexer.IF, TokenCategory.KEYWORD),
    IMPL(RustLexer.IMPL, TokenCategory.KEYWORD),
    LET(RustLexer.LET, TokenCategory.KEYWORD),
    LOG(RustLexer.LOG, TokenCategory.KEYWORD),
    LOOP(RustLexer.LOOP, TokenCategory.KEYWORD),
    MATCH(RustLexer.MATCH, TokenCategory.KEYWORD),
    MOD(RustLexer.MOD, TokenCategory.KEYWORD),
    MUT(RustLexer.MUT, TokenCategory.KEYWORD),
    ONCE(RustLexer.ONCE, TokenCategory.KEYWORD),
    PRIV(RustLexer.PRIV, TokenCategory.KEYWORD),
    PUB(RustLexer.PUB, TokenCategory.KEYWORD),
    PURE(RustLexer.PURE, TokenCategory.KEYWORD),
    REF(RustLexer.REF, TokenCategory.KEYWORD),
    RETURN(RustLexer.RETURN, TokenCategory.KEYWORD),
    SELF(RustLexer.SELF, TokenCategory.KEYWORD),
    STATIC(RustLexer.STATIC, TokenCategory.KEYWORD),
    STRUCT(RustLexer.STRUCT, TokenCategory.KEYWORD),
    TRUE(RustLexer.TRUE, TokenCategory.KEYWORD),
    TRAIT(RustLexer.TRAIT, TokenCategory.KEYWORD),
    TYPE(RustLexer.TYPE, TokenCategory.KEYWORD),
    UNSAFE(RustLexer.UNSAFE, TokenCategory.KEYWORD),
    USE(RustLexer.USE, TokenCategory.KEYWORD),
    WHILE(RustLexer.WHILE, TokenCategory.KEYWORD),
    PLUS(RustLexer.PLUS, TokenCategory.OPERATOR),
    AND(RustLexer.AND, TokenCategory.OPERATOR),
    MINUS(RustLexer.MINUS, TokenCategory.OPERATOR),
    DIV(RustLexer.DIV, TokenCategory.OPERATOR),
    REM(RustLexer.REM, TokenCategory.OPERATOR),
    CARET(RustLexer.CARET, TokenCategory.OPERATOR),
    OR(RustLexer.OR, TokenCategory.OPERATOR),
    EQ(RustLexer.EQ, TokenCategory.OPERATOR),
    LE(RustLexer.LE, TokenCategory.OPERATOR),
    LT(RustLexer.LT, TokenCategory.OPERATOR),
    EQEQ(RustLexer.EQEQ, TokenCategory.OPERATOR),
    NE(RustLexer.NE, TokenCategory.OPERATOR),
    GE(RustLexer.GE, TokenCategory.OPERATOR),
    GT(RustLexer.GT, TokenCategory.OPERATOR),
    NOT(RustLexer.NOT, TokenCategory.OPERATOR),
    TILDE(RustLexer.TILDE, TokenCategory.SEPARATOR),
    STAR(RustLexer.STAR, TokenCategory.SEPARATOR),
    BINOPEQ(RustLexer.BINOPEQ, TokenCategory.OPERATOR),
    AT(RustLexer.AT, TokenCategory.SEPARATOR),
    DOT(RustLexer.DOT, TokenCategory.SEPARATOR),
    DOTDOT(RustLexer.DOTDOT, TokenCategory.SEPARATOR),
    COMMA(RustLexer.COMMA, TokenCategory.SEPARATOR),
    SEMI(RustLexer.SEMI, TokenCategory.SEPARATOR),
    COLON(RustLexer.COLON, TokenCategory.SEPARATOR),
    MOD_SEP(RustLexer.MOD_SEP, TokenCategory.SEPARATOR),
    RARROW(RustLexer.RARROW, TokenCategory.SEPARATOR),
    LARROW(RustLexer.LARROW, TokenCategory.SEPARATOR),
    DARROW(RustLexer.DARROW, TokenCategory.SEPARATOR),
    FAT_ARROW(RustLexer.FAT_ARROW, TokenCategory.SEPARATOR),
    LPAREN(RustLexer.LPAREN, TokenCategory.SEPARATOR),
    RPAREN(RustLexer.RPAREN, TokenCategory.SEPARATOR),
    LBRACKET(RustLexer.LBRACKET, TokenCategory.SEPARATOR),
    RBRACKET(RustLexer.RBRACKET, TokenCategory.SEPARATOR),
    LBRACE(RustLexer.LBRACE, TokenCategory.SEPARATOR),
    RBRACE(RustLexer.RBRACE, TokenCategory.SEPARATOR),
    POUND(RustLexer.POUND, TokenCategory.COMMENT),
    DOLLAR(RustLexer.DOLLAR, TokenCategory.SEPARATOR),
    LIT_INT(RustLexer.LIT_INT, TokenCategory.NUMBER),
    LIT_FLOAT(RustLexer.LIT_FLOAT, TokenCategory.NUMBER),
    LIT_STR(RustLexer.LIT_STR, TokenCategory.STRING),
    IDENT(RustLexer.IDENT, TokenCategory.IDENTIFIER),
    UNDERSCORE(RustLexer.UNDERSCORE, TokenCategory.IDENTIFIER),
    STATIC_LIFETIME(RustLexer.STATIC_LIFETIME, TokenCategory.SEPARATOR),
    LIFETIME(RustLexer.LIFETIME, TokenCategory.SEPARATOR),
    OUTER_DOC_COMMENT(RustLexer.OUTER_DOC_COMMENT, TokenCategory.COMMENT),
    INNER_DOC_COMMENT(RustLexer.INNER_DOC_COMMENT, TokenCategory.COMMENT),
    WS(RustLexer.WS, TokenCategory.WHITESPACE),
    OTHER_LINE_COMMENT(RustLexer.OTHER_LINE_COMMENT, TokenCategory.COMMENT),
    OTHER_BLOCK_COMMENT(RustLexer.OTHER_BLOCK_COMMENT, TokenCategory.COMMENT),
    SHEBANG_LINE(RustLexer.SHEBANG_LINE, TokenCategory.COMMENT),
    BINDIGIT(RustLexer.BINDIGIT, TokenCategory.NUMBER),
    DECDIGIT(RustLexer.DECDIGIT, TokenCategory.NUMBER),
    DECDIGIT_CONT(RustLexer.DECDIGIT_CONT, TokenCategory.NUMBER),
    HEXDIGIT(RustLexer.HEXDIGIT, TokenCategory.NUMBER),
    INTLIT_TY(RustLexer.INTLIT_TY, TokenCategory.NUMBER),
    LITFLOAT_EXP(RustLexer.LITFLOAT_EXP, TokenCategory.NUMBER),
    LITFLOAT_TY(RustLexer.LITFLOAT_TY, TokenCategory.NUMBER),
    ESCAPEDCHAR(RustLexer.ESCAPEDCHAR, TokenCategory.CHARACTER),
    LIT_CHAR(RustLexer.LIT_CHAR, TokenCategory.CHARACTER),
    STRCHAR(RustLexer.STRCHAR, TokenCategory.STRING),
    STRESCAPE(RustLexer.STRESCAPE, TokenCategory.STRING),
    IDSTART(RustLexer.IDSTART, TokenCategory.IDENTIFIER),
    IDCONT(RustLexer.IDCONT, TokenCategory.IDENTIFIER),
    NON_SLASH_OR_WS(RustLexer.NON_SLASH_OR_WS, TokenCategory.IDENTIFIER),
    XIDSTART(RustLexer.XIDSTART, TokenCategory.IDENTIFIER),
    XIDCONT(RustLexer.XIDCONT, TokenCategory.IDENTIFIER);

    private static final Language<RustTokenId> LANGUAGE = new RustLanguageHierarchy().language();

    public static Language<RustTokenId> getLanguage() {
        return LANGUAGE;
    }

    private final int antlrTokenType;
    private final TokenCategory primaryCategory;

    @SuppressWarnings("LeakingThisInConstructor")
    private RustTokenId(int antlrTokenType, TokenCategory primaryCategory) {
        this.antlrTokenType = antlrTokenType;
        this.primaryCategory = primaryCategory;
    }

    public int antlrTokenType() {
        return antlrTokenType;
    }

    @Override
    public String primaryCategory() {
        return primaryCategory.getName();
    }
}
