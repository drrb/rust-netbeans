/**
 * Copyright (C) 2013 drrb
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
package com.github.drrb.rust.netbeans;

import com.github.drrb.rust.netbeans.TestParsing;
import com.github.drrb.rust.netbeans.parsing.NetbeansRustParser;

/**
 *
 */
public class RustSource {

    private final StringBuilder source = new StringBuilder();

    public RustSource appendln(String line) {
        source.append(line).append("\n");
        return this;
    }

    public NetbeansRustParser.NetbeansRustParserResult parse() {
        return TestParsing.parse(source);
    }
}
