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

import com.github.drrb.rust.netbeans.NetbeansRustParser.NetbeansRustParserResult;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.antlr.v4.runtime.ParserRuleContext;
import org.netbeans.modules.csl.api.ColoringAttributes;
import static org.netbeans.modules.csl.api.ColoringAttributes.*;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.api.SemanticAnalyzer;
import org.netbeans.modules.parsing.spi.Scheduler;
import org.netbeans.modules.parsing.spi.SchedulerEvent;

/**
 *
 */
public class RustSemanticAnalyzer extends SemanticAnalyzer<NetbeansRustParserResult> {
    
    private final AtomicBoolean cancelled = new AtomicBoolean();
    private final Map<OffsetRange, Set<ColoringAttributes>> highlights = new HashMap<OffsetRange, Set<ColoringAttributes>>();

    @Override
    public void run(NetbeansRustParser.NetbeansRustParserResult result, SchedulerEvent event) {
        //TODO: Are these needed? Is this class disposable?
        highlights.clear();
        cancelled.set(false);

        RustParser.ProgContext prog = result.getAst();
        prog.accept(new HighlightingVisitor());
    }
    
    private Void addHighlight(ParserRuleContext context, ColoringAttributes firstAttribute, ColoringAttributes... otherAttributes) {
        if (context != null) {
            int startIndex = context.getStart().getStartIndex();
            int stopIndex = context.getStop().getStopIndex();
            highlights.put(new OffsetRange(startIndex, stopIndex + 1), EnumSet.of(firstAttribute, otherAttributes));
        }
        return null;
    }

    @Override
    public Map<OffsetRange, Set<ColoringAttributes>> getHighlights() {
        return new HashMap<OffsetRange, Set<ColoringAttributes>>(highlights);
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public Class<? extends Scheduler> getSchedulerClass() {
        return Scheduler.EDITOR_SENSITIVE_TASK_SCHEDULER;
    }

    @Override
    public void cancel() {
        cancelled.set(true);
    }

    private class HighlightingVisitor extends RustBaseVisitor<Void> {

        @Override
        public Void visitItem_fn_decl(RustParser.Item_fn_declContext ctx) {
            return addHighlight(ctx.ident(), METHOD);
        }

        @Override
        public Void visitEnum_decl(RustParser.Enum_declContext ctx) {
            visitChildren(ctx);
            return addHighlight(ctx.ident(), CLASS);
        }

        @Override
        public Void visitEnum_variant_decl(RustParser.Enum_variant_declContext ctx) {
            return addHighlight(ctx.ident(), ENUM);
        }

        @Override
        public Void visitStruct_decl(RustParser.Struct_declContext ctx) {
            visitChildren(ctx);
            return addHighlight(ctx.ident(), CLASS);
        }

        @Override
        public Void visitStruct_field(RustParser.Struct_fieldContext ctx) {
            return addHighlight(ctx.ident(), FIELD);
        }

        @Override
        public Void visitTrait_decl(RustParser.Trait_declContext ctx) {
            visitChildren(ctx);
            return addHighlight(ctx.ident(), CLASS);
        }

        @Override
        public Void visitTrait_method(RustParser.Trait_methodContext ctx) {
            return addHighlight(ctx.ident(), METHOD);
        }

        @Override
        public Void visitImpl(RustParser.ImplContext ctx) {
            visitChildren(ctx);
            ctx.accept(new RustBaseVisitor<Void>() {

                @Override
                public Void visitNon_global_path(RustParser.Non_global_pathContext ctx) {
                    List<RustParser.IdentContext> identifiers = ctx.ident();
                    for (RustParser.IdentContext identifier : identifiers) {
                        addHighlight(identifier, CLASS);
                    }
                    return null;
                }
                
            });
            return null;
        }

        @Override
        public Void visitImpl_trait_for_type(RustParser.Impl_trait_for_typeContext ctx) {
            visitChildren(ctx);
            ctx.accept(new RustBaseVisitor<Void>() {

                @Override
                public Void visitNon_global_path(RustParser.Non_global_pathContext ctx) {
                    List<RustParser.IdentContext> identifiers = ctx.ident();
                    for (RustParser.IdentContext identifier : identifiers) {
                        addHighlight(identifier, CLASS);
                    }
                    return null;
                }
                
            });
            return null;
        }

        @Override
        public Void visitImpl_method(RustParser.Impl_methodContext ctx) {
            visitChildren(ctx);
            return addHighlight(ctx.ident(), METHOD);
        }
    }
}
