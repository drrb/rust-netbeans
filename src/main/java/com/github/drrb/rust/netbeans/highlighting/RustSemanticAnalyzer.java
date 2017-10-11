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
package com.github.drrb.rust.netbeans.highlighting;

import com.github.drrb.rust.netbeans.parsing.NetbeansRustParser.NetbeansRustParserResult;
import com.github.drrb.rust.netbeans.parsing.javacc.*;
import org.netbeans.modules.csl.api.ColoringAttributes;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.api.SemanticAnalyzer;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.parsing.spi.Scheduler;
import org.netbeans.modules.parsing.spi.SchedulerEvent;
import org.openide.util.Exceptions;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import static com.github.drrb.rust.netbeans.parsing.javacc.ParseUtil.offsetRange;
import static org.netbeans.modules.csl.api.ColoringAttributes.*;

/**
 *
 */
public class RustSemanticAnalyzer extends SemanticAnalyzer<NetbeansRustParserResult> {
    private static final Logger LOG = Logger.getLogger(RustSemanticAnalyzer.class.getName());
    private final Map<OffsetRange, Set<ColoringAttributes>> highlights = new HashMap<>();
    private final AtomicBoolean cancelled = new AtomicBoolean();

    @Override
    public void run(NetbeansRustParserResult result, SchedulerEvent event) {
        highlights.clear();
        cancelled.set(false); //TODO: respect this cancellation in the visitors

        try {
            SimpleNode rootNode = result.rootNode();
            rootNode.jjtAccept(new FunctionNameHighlighter(), null);
            rootNode.jjtAccept(new StructHighlighter(), null);
            rootNode.jjtAccept(new AnnotationHighlighter(), null);
        } catch (ParseException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public Map<OffsetRange, Set<ColoringAttributes>> getHighlights() {
        return new HashMap<>(highlights);
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

    private class FunctionNameHighlighter extends RustParserDefaultVisitor {
        @Override
        public Object visit(ASTfunctionName functionNameNode, Object data) {
            RustToken identifier = (RustToken) functionNameNode.jjtGetFirstToken();
            highlights.put(identifier.offsetRange(), EnumSet.of(STATIC, METHOD));
            return null;
        }
    }

    private class StructHighlighter extends RustParserDefaultVisitor {
        @Override
        public Object visit(ASTstructName structNameNode, Object data) {
            RustToken identifier = (RustToken) structNameNode.jjtGetFirstToken();
            highlights.put(identifier.offsetRange(), CLASS_SET);
            return null;
        }

        @Override
        public Object visit(ASTstructField fieldNode, Object data) {
            RustToken identifier = (RustToken) fieldNode.jjtGetFirstToken();
            highlights.put(identifier.offsetRange(), FIELD_SET);
            return null;
        }
    }

    private class AnnotationHighlighter extends RustParserDefaultVisitor {
        @Override
        public Object visit(ASTAnnotation node, Object data) {
            highlights.put(offsetRange(node), EnumSet.of(ANNOTATION_TYPE));
            return null;
        }
    }
}
