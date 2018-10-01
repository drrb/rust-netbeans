/**
 * Copyright (C) 2018 Tim Boudreau
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
package com.github.drrb.rust.netbeans.parsing.antlr;

import java.util.Objects;
import org.netbeans.api.lexer.TokenId;

/**
 * Generic TokenId implementation for tokens from an Antlr 4 Vocabulary.
 *
 * @author Tim Boudreau
 */
public class AntlrTokenID implements TokenId {

    private final int tokenType;
    private final String literalName;
    private final String displayName;
    private final String symbolicName;
    private final String category;

    public static AntlrTokenID EOF = new AntlrTokenID(-1, "EOF", "EOF", "EOF", "other");

    AntlrTokenID(int tokenType, String literalName, String displayName,
            String symbolicName, String category) {
        this.tokenType = tokenType;
        this.literalName = literalName;
        this.displayName = displayName;
        this.symbolicName = symbolicName;
        this.category = category;
    }

    public String literalName() {
        return literalName;
    }

    public String symbolicName() {
        return symbolicName();
    }

    public String displayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return tokenType + "/" + (symbolicName == null ? "-" : "'" + symbolicName + "'")
                + "/" + literalName + "/" + displayName;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o == null) {
            return false;
        } else if (o instanceof AntlrTokenID) {
            AntlrTokenID other = (AntlrTokenID) o;
            return other.tokenType == tokenType &&
                    Objects.equals(other.literalName, literalName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return ((tokenType + 1) * 51) * Objects.hashCode(literalName);
    }

    @Override
    public String name() {
        if (symbolicName != null) {
            return symbolicName;
        } else if (literalName != null) {
            return literalName;
        } else if (displayName != null) {
            return displayName;
        } else {
            return "????";
        }
    }

    @Override
    public int ordinal() {
        return tokenType;
    }

    @Override
    public String primaryCategory() {
        return category;
    }
}
