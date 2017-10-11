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

public enum TokenCategory {
    CHARACTER("character"),
    ERRORS("errors"),
    IDENTIFIER("identifier"),
    KEYWORD("keyword"),
    LITERAL("literal"),
    COMMENT("comment"),
    NUMBER("number"),
    OPERATOR("operator"),
    STRING("string"),
    SEPARATOR("separator"),
    WHITESPACE("whitespace"),
    METHOD_DECLARATION("method-declaration");
    private final String name;

    TokenCategory(String categoryName) {
        this.name = categoryName;
    }

    public String getName() {
        return name;
    }
}
