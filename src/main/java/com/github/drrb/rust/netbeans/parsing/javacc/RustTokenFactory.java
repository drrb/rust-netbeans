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
package com.github.drrb.rust.netbeans.parsing.javacc;

public class RustTokenFactory {

    public static Token newToken(int ofKind, String tokenImage) {
        return new RustToken(maybeTranslateSubkind(ofKind), tokenImage);
    }

    private static int maybeTranslateSubkind(int kind) {
        switch(kind) {
            case RustParserConstants.RAW_STRING_LITERAL_0:
            case RustParserConstants.RAW_STRING_LITERAL_1:
            case RustParserConstants.RAW_STRING_LITERAL_2:
            case RustParserConstants.RAW_STRING_LITERAL_3:
                return RustParserConstants.RAW_STRING_LITERAL;
            case RustParserConstants.RAW_BYTE_STRING_LITERAL_0:
            case RustParserConstants.RAW_BYTE_STRING_LITERAL_1:
            case RustParserConstants.RAW_BYTE_STRING_LITERAL_2:
            case RustParserConstants.RAW_BYTE_STRING_LITERAL_3:
                return RustParserConstants.RAW_BYTE_STRING_LITERAL;
            default:
                return kind;
        }
    }
}

