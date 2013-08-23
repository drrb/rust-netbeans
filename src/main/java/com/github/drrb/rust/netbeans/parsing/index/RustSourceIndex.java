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
package com.github.drrb.rust.netbeans.parsing.index;

import com.github.drrb.rust.netbeans.util.Option;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public class RustSourceIndex {

    private final RangeMap<RustFunction> functionRanges = new RangeMap<RustFunction>();
    private final List<RustFunction> functions = new LinkedList<RustFunction>();

    public List<RustFunction> getFunctions() {
        return Collections.unmodifiableList(functions);
    }

    public Option<RustFunction> getFunctionAt(int offset) {
        return functionRanges.get(offset);
    }

    public void addFunction(RustFunction function) {
        functions.add(function);
        functionRanges.put(function.getOffsetRange(), function);
    }
}
