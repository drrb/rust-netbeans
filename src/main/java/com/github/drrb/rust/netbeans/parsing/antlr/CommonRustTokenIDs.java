/*
 * Copyright (C) 2018 Tim Boudreau
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
package com.github.drrb.rust.netbeans.parsing.antlr;

import com.github.drrb.rust.antlr.RustLexer;
import static com.github.drrb.rust.netbeans.parsing.antlr.AntlrRustLanguageHierarchy.INSTANCE;

/**
 *
 * @author Tim Boudreau
 */
public class CommonRustTokenIDs {

    public static AntlrTokenID stringLiteral() {
        return forTokenType(RustLexer.StringLiteral);
    }

    public static AntlrTokenID function() {
        return forTokenType(RustLexer.Fn);
    }

    public static AntlrTokenID eof() {
        return AntlrTokenID.EOF;
    }

    public static AntlrTokenID forSymbol(char symbol) {
        return INSTANCE.tokenIds.forSymbol(symbol);
    }

    public static AntlrTokenID forSymbolicName(String symbolicName) {
        return INSTANCE.tokenIds.forSymbolicName(symbolicName);
    }

    public static AntlrTokenID forLiteralName(String name) {
        return INSTANCE.tokenIds.get(name);
    }

    public static AntlrTokenID forTokenType(int id) {
        return INSTANCE.tokenIds.get(id);
    }

    public static AntlrTokenID whitespaceTokenID() {
        return forTokenType(RustLexer.Whitespace);
    }

    public static AntlrTokenID identifierTokenID() {
        return forSymbolicName("Ident");
    }

    private static AntlrTokenID bang;
    public static AntlrTokenID bang() {
        return bang == null ? bang = forSymbol('!') : bang;
    }

    private static AntlrTokenID semicolon;
    public static AntlrTokenID semicolon() {
        return semicolon == null ? semicolon = forTokenType(RustLexer.Semicolon) : semicolon;
    }

    private static AntlrTokenID leftBrace;
    public static AntlrTokenID leftBrace() {
        return leftBrace == null ? leftBrace = forTokenType(RustLexer.LeftBrace) : leftBrace;
    }

    private static AntlrTokenID rightBrace;
    public static AntlrTokenID rightBrace() {
        return rightBrace == null ? rightBrace = forTokenType(RustLexer.RightBrace) : rightBrace;
    }

    private static AntlrTokenID leftParen;
    public static AntlrTokenID leftParen() {
        return leftParen == null ? leftParen = forTokenType(RustLexer.LeftParen) : leftParen;
    }

    private static AntlrTokenID rightParen;
    public static AntlrTokenID rightParen() {
        return rightParen == null ? rightParen = forTokenType(RustLexer.RightParen) : rightParen;
    }

    private static AntlrTokenID leftBracket;
    public static AntlrTokenID leftBracket() {
        return leftBracket== null ? leftBracket= forTokenType(RustLexer.LeftBracket) : leftBracket;
    }

    private static AntlrTokenID rightBracket;
    public static AntlrTokenID rightBracket() {
        return rightBracket == null ? rightBracket= forTokenType(RustLexer.RightBracket) : rightBracket;
    }

    private static AntlrTokenID leftAngleBracket;
    public static AntlrTokenID leftAngleBracket() {
        return leftAngleBracket== null ? leftAngleBracket= forTokenType(RustLexer.LeftAngleBracket) : leftAngleBracket;
    }

    private static AntlrTokenID rightAngleBracket;
    public static AntlrTokenID rightAngleBracket() {
        return rightAngleBracket == null ? rightAngleBracket= forTokenType(RustLexer.RightAngleBracket) : rightAngleBracket;
    }

    private static AntlrTokenID comma;
    public static AntlrTokenID comma() {
        return comma == null ? comma= forTokenType(RustLexer.Comma) : comma;
    }

    public static Iterable<AntlrTokenID> all() {
        return INSTANCE.tokenIds;
    }
}
