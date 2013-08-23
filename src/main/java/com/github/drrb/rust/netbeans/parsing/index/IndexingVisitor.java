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

import com.github.drrb.rust.netbeans.parsing.RustBaseVisitor;
import static com.github.drrb.rust.netbeans.parsing.RustLexUtils.offsetRangeFor;
import com.github.drrb.rust.netbeans.parsing.RustParser;

/**
 *
 */
public class IndexingVisitor extends RustBaseVisitor<RustSourceIndex> {

    private final RustSourceIndex index = new RustSourceIndex();

    @Override
    protected RustSourceIndex defaultResult() {
        return index;
    }

    @Override
    protected RustSourceIndex aggregateResult(RustSourceIndex aggregate, RustSourceIndex nextResult) {
        return aggregate;
    }

    @Override
    public RustSourceIndex visitItem_fn_decl(final RustParser.Item_fn_declContext functionContext) {
        final RustFunction.Builder functionBuilder = RustFunction.builder().setOffsetRange(offsetRangeFor(functionContext));
        visitChildren(functionContext);
        functionContext.accept(new RustBaseVisitor<Void>() {
            @Override
            public Void visitArg(RustParser.ArgContext ctx) {
                return ctx.pat().accept(new RustBaseVisitor<Void>() {
                    @Override
                    public Void visitNon_global_path(RustParser.Non_global_pathContext ctx) {
                        RustParser.IdentContext argContext = ctx.ident(ctx.ident().size() - 1);
                        functionBuilder.addParameterName(new RustFunctionParameterName(argContext.getText(), offsetRangeFor(argContext)));
                        return null;
                    }
                });
            }

            @Override
            public Void visitFun_body(RustParser.Fun_bodyContext functionBodyContext) {
                final RustFunctionBody.Builder functionBodyBuilder = RustFunctionBody.builder().setText(functionBodyContext.getText()).setOffsetRange(offsetRangeFor(functionBodyContext));
                functionBodyContext.accept(new RustBaseVisitor<Void>() {
                    @Override
                    public Void visitIdent(RustParser.IdentContext ctx) {
                        functionBodyBuilder.addLocalVariableIdentifier(new RustLocalVariableIdentifier(ctx.getText(), offsetRangeFor(ctx)));
                        return null;
                    }
                });
                functionBuilder.setBody(functionBodyBuilder.build());
                return null;
            }
        });
        index.addFunction(functionBuilder.build());
        return index;
    }
}