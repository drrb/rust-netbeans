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
public class RustFunctionParameterName {

    private final String text;
    private final OffsetRange offsetRange;

    public RustFunctionParameterName(String text, OffsetRange offsetRange) {
        this.text = text;
        this.offsetRange = offsetRange;
    }

    public OffsetRange getOffsetRange() {
        return offsetRange;
    }

    public String getText() {
        return text;
    }
}
