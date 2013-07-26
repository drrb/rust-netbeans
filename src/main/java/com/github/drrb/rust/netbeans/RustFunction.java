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

import org.antlr.v4.runtime.tree.RuleNode;

/**
 *
 */
public class RustFunction {
    private final int startIndex;
    private final int endIndex;
    private final String name;

    public RustFunction(RustParser.Item_fn_declContext ctx) {
        this.startIndex = ctx.getStart().getStartIndex();
        this.endIndex = ctx.getStop().getStopIndex();
        this.name = ctx.accept(new FunctionNameFinder());
    }

    public String getName() {
        return name;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    private static class FunctionNameFinder extends RustBaseVisitor<String> {

        @Override
        public String visitIdent(RustParser.IdentContext ctx) {
            return ctx.getText();
        }

        @Override
        protected String aggregateResult(String aggregate, String nextResult) {
            if (aggregate == null) {
                return nextResult;
            } else {
                return aggregate;
            }
        }

        @Override
        protected boolean shouldVisitNextChild(RuleNode node, String currentResult) {
            return currentResult == null;
        }
    }
}
