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
import com.github.drrb.rust.netbeans.parse.CollectingVisitor;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
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
    private final Collection<Highlight> highlights = new LinkedList<Highlight>();

    @Override
    public void run(NetbeansRustParser.NetbeansRustParserResult result, SchedulerEvent event) {
        //TODO: Are these needed? Is this class disposable?
        highlights.clear();
        cancelled.set(false);

        RustParser.ProgContext prog = result.getAst();
        List<Highlight> collectedHighlights = prog.accept(new HighlightCollectingVisitor());
        highlights.addAll(collectedHighlights);
    }

    @Override
    public Map<OffsetRange, Set<ColoringAttributes>> getHighlights() {
        return mapHighlights(highlights);
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
    
    private Map<OffsetRange, Set<ColoringAttributes>> mapHighlights(Collection<Highlight> highlights) {
        Map<OffsetRange, Set<ColoringAttributes>> highlightsMap = new HashMap<OffsetRange, Set<ColoringAttributes>>(highlights.size());
        for (Highlight highlight : highlights) {
            highlightsMap.put(highlight.offsetRange, highlight.coloringAttributes);
        }
        return highlightsMap;
    }

    private static class Highlight {

        final ParserRuleContext context;
        final Set<ColoringAttributes> coloringAttributes;
        final OffsetRange offsetRange;

        Highlight(ParserRuleContext context, Set<ColoringAttributes> coloringAttributes) {
            this.coloringAttributes = coloringAttributes;
            this.context = context;
            int start = context.getStart().getStartIndex();
            int stop = context.getStop().getStopIndex();
            this.offsetRange = new OffsetRange(start, stop + 1);
        }
    }

    private class HighlightCollectingVisitor extends CollectingVisitor<Highlight> {

        private List<Highlight> highlight(ParserRuleContext identifier, ColoringAttributes firstColoringAttribute, ColoringAttributes... otherColoringAttributes) {
            LinkedList<Highlight> highlightList = new LinkedList<Highlight>();
            if (identifier != null) {
                return new LinkedList<Highlight>(Collections.singletonList(new Highlight(identifier, EnumSet.of(firstColoringAttribute, otherColoringAttributes))));
            }
            return highlightList;
        }

        @Override
        public List<Highlight> visitItem_fn_decl(RustParser.Item_fn_declContext ctx) {
            return highlight(ctx.ident(), METHOD);
        }

        @Override
        public List<Highlight> visitEnum_decl(RustParser.Enum_declContext ctx) {
            return aggregateResult(highlight(ctx.ident(), CLASS), visitChildren(ctx));
        }

        @Override
        public List<Highlight> visitEnum_variant_decl(RustParser.Enum_variant_declContext ctx) {
            return highlight(ctx.ident(), ENUM);
        }

        @Override
        public List<Highlight> visitStruct_decl(RustParser.Struct_declContext ctx) {
            return aggregateResult(highlight(ctx.ident(), CLASS), visitChildren(ctx));
        }

        @Override
        public List<Highlight> visitStruct_field(RustParser.Struct_fieldContext ctx) {
            return highlight(ctx.ident(), FIELD);
        }

        @Override
        public List<Highlight> visitTrait_decl(RustParser.Trait_declContext ctx) {
            return aggregateResult(highlight(ctx.ident(), CLASS), visitChildren(ctx));
        }

        @Override
        public List<Highlight> visitTrait_method(RustParser.Trait_methodContext ctx) {
            return highlight(ctx.ident(), METHOD);
        }

        @Override
        public List<Highlight> visitImpl(RustParser.ImplContext ctx) {
            List<Highlight> implNameHighlight = ctx.accept(new CollectingVisitor<Highlight>() {
                @Override
                public List<Highlight> visitNon_global_path(RustParser.Non_global_pathContext ctx) {
                    List<Highlight> highlights = new LinkedList<Highlight>();
                    for (RustParser.IdentContext identifier : ctx.ident()) {
                        highlights.add(new Highlight(identifier, CLASS_SET));
                    }
                    return highlights;
                }
            });
            if (implNameHighlight == null) {
                System.out.println("highlight is null");
            }
            return aggregateResult(implNameHighlight, visitChildren(ctx));
        }

        @Override
        public List<Highlight> visitImpl_trait_for_type(RustParser.Impl_trait_for_typeContext ctx) {
            List<Highlight> implNameHighlight = ctx.accept(new RustBaseVisitor<List<Highlight>>() {
                @Override
                public List<Highlight> visitNon_global_path(RustParser.Non_global_pathContext ctx) {
                    List<Highlight> highlights = new LinkedList<Highlight>();
                    for (RustParser.IdentContext identifier : ctx.ident()) {
                        highlights.add(new Highlight(identifier, CLASS_SET));
                    }
                    return highlights;
                }
            });
            return aggregateResult(implNameHighlight, visitChildren(ctx));
        }

        @Override
        public List<Highlight> visitImpl_method(RustParser.Impl_methodContext ctx) {
            return aggregateResult(highlight(ctx.ident(), METHOD), visitChildren(ctx));
        }
    }
}
