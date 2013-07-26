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
import com.github.drrb.rust.netbeans.RustFunction;
import com.github.drrb.rust.netbeans.RustParser;
import java.util.ArrayList;
import java.util.Arrays;
import static java.util.Arrays.asList;
import java.util.Collection;
import java.util.LinkedList;

/**
 *
 */
public class RustFunctionCollectingVisitor extends RustBaseVisitor<Collection<RustFunction>> {

    @Override
    protected Collection<RustFunction> defaultResult() {
        return new LinkedList<RustFunction>();
    }

    @Override
    protected Collection<RustFunction> aggregateResult(Collection<RustFunction> aggregate, Collection<RustFunction> nextResult) {
        aggregate.addAll(nextResult);
        return aggregate;
    }

    @Override
    public Collection<RustFunction> visitItem_fn_decl(RustParser.Item_fn_declContext ctx) {
        return new LinkedList<RustFunction>(asList(new RustFunction(ctx)));
    }
}
