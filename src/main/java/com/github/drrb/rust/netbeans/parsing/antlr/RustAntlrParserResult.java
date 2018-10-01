/*
 * Copyright (C) 2018 Tim Boudreau
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

package com.github.drrb.rust.netbeans.parsing.antlr;

import com.github.drrb.rust.antlr.RustParser;
import com.github.drrb.rust.antlr.RustVisitor;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.netbeans.modules.csl.api.Error;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.api.Snapshot;

/**
 *
 * @author Tim Boudreau
 */
public final class RustAntlrParserResult extends ParserResult {

    private final RustParseInfo info;
    private RustParser parser;

    public RustAntlrParserResult(Snapshot snapshot, RustParser parser, AtomicBoolean cancelled) {
        super(snapshot);
        this.parser = parser;
        info = RustAnalyzer.analyze(parser, snapshot, cancelled);
    }

    public String toString() {
        return "RustAntlrParserResult {" + info + "}";
    }

    @SuppressWarnings("null")
    public boolean accept(RustVisitor<?> visitor) {
        RustParser parserLocal;
        synchronized(this) {
            parserLocal = this.parser;
        }
        boolean result = parserLocal != null && parserLocal.crate() != null;
        if (result) {
            parserLocal.crate().accept(visitor);
        }
        return result;
    }

    public List<OffsetRange> blocks() {
        return info.blocks();
    }

    public List<? extends RustSourceRegion> semanticRegions() {
        return info.semanticRegions();
    }

    public List<? extends RustStructureItem> structureItems() {
        return info.structureItems();
    }

    public RustParseInfo info() {
        return info;
    }

    @Override
    public List<? extends Error> getDiagnostics() {
        return info.errors();
    }

    @Override
    protected synchronized void invalidate() {
        info.clear();
        parser = null;
    }
}
