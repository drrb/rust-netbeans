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
package com.github.drrb.rust.netbeans.parsing.index;

import org.netbeans.modules.csl.api.OffsetRange;

/**
 *
 */
public class RustEnum {

    private final String name;
    private final OffsetRange offsetRange;
    private final RustEnumBody body;

    RustEnum(String name, OffsetRange offsetRange, RustEnumBody body) {
        this.name = name;
        this.offsetRange = offsetRange;
        this.body = body;
    }

    public String getName() {
        return name;
    }

    public RustEnumBody getBody() {
        return body;
    }

    public OffsetRange getOffsetRange() {
        return offsetRange;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String name;
        private OffsetRange offsetRange;
        private RustEnumBody body;

        RustEnum build() {
            return new RustEnum(name, offsetRange, body);
        }

        Builder setName(String name) {
            this.name = name;
            return this;
        }

        Builder setOffsetRange(OffsetRange offsetRange) {
            this.offsetRange = offsetRange;
            return this;
        }

        Builder setBody(RustEnumBody body) {
            this.body = body;
            return this;
        }
    }
}
