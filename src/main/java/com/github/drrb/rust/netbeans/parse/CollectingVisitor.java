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
package com.github.drrb.rust.netbeans.parse;

import com.github.drrb.rust.netbeans.RustBaseVisitor;
import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public class CollectingVisitor<T> extends RustBaseVisitor<List<T>> {

    @Override
    protected List<T> defaultResult() {
        return new LinkedList<T>();
    }

    @Override
    protected List<T> aggregateResult(List<T> aggregate, List<T> nextResult) {
        aggregate.addAll(nextResult);
        return aggregate;
    }
}
