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
package com.github.drrb.rust.netbeans.parsing.antlr;

import org.netbeans.modules.csl.api.ColoringAttributes;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.api.SemanticAnalyzer;
import org.netbeans.modules.parsing.spi.Scheduler;
import org.netbeans.modules.parsing.spi.SchedulerEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 *
 */
public class RustAntlrSemanticAnalyzer extends SemanticAnalyzer<RustAntlrParserResult> {
//    private static final Logger LOG = Logger.getLogger(RustAntlrSemanticAnalyzer.class.getName());

    private final Map<OffsetRange, Set<ColoringAttributes>> highlights = new HashMap<>();
    private final AtomicBoolean cancelled = new AtomicBoolean();

    @Override
    public void run(RustAntlrParserResult result, SchedulerEvent event) {
        try {
            highlights.clear();
            cancelled.set(false);
            for (RustSourceRegion region : result.info().semanticRegions()) {
                System.out.println("highlight " + region);
                highlights.put(region.range(), region.attributes());
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
            if (e instanceof RuntimeException) {
                throw ((RuntimeException) e);
            } else {
                throw new RuntimeException(e);
            }
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
}
