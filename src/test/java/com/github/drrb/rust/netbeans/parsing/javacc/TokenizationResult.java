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

import com.github.drrb.rust.netbeans.parsing.RustTokenId;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.LinkedList;
import java.util.List;

public class TokenizationResult extends JsonSerializable {
    public static class Token {
        public String image;
        public RustTokenId kind;
        private transient Integer beginLine;
        private transient Integer beginColumn;

        public Token() {
            this(null, null);
        }

        public Token(RustToken token) {
            this(token.image, token.kind(), token.beginLine, token.beginColumn);
        }

        public Token(String image, RustTokenId kind) {
            this(image, kind, null, null);
        }

        public Token(String image, RustTokenId kind, Integer beginLine, Integer beginColumn) {
            this.image = image;
            this.kind = kind;
            this.beginLine = beginLine;
            this.beginColumn = beginColumn;
        }

        @Override
        public String toString() {
            if (beginLine == null) {
                return ToStringBuilder.reflectionToString(this);
            } else {
                return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE, true);
            }
        }

        @Override
        public boolean equals(Object other) {
            return EqualsBuilder.reflectionEquals(this, other);
        }

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this);
        }

        public boolean isGarbage() {
            return kind == RustTokenId.GARBAGE;
        }
    }

    public List<Token> tokens = new LinkedList<>();

}
