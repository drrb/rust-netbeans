/**
 * Copyright (C) 2013 drrb
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

import org.antlr.v4.runtime.tree.RuleNode;

/**
 *
 */
public class FetchingVisitor<T> extends RustBaseVisitor<T> {

    @Override
    protected T defaultResult() {
        return null;
    }

    @Override
    protected T aggregateResult(T aggregate, T nextResult) {
        return aggregate == null ? nextResult : aggregate;
    }

    @Override
    protected boolean shouldVisitNextChild(RuleNode node, T currentResult) {
        return currentResult != null;
    }
}
