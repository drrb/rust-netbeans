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
import com.github.drrb.rust.netbeans.parsing.antlr.RustVisibility;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.netbeans.modules.csl.api.Error;
import org.netbeans.modules.csl.api.OffsetRange;

/**
 *
 * @author Tim Boudreau
 */
final class RustParseInfo {

    Set<Error> errors = new LinkedHashSet<>();
    Set<OffsetRange> blocks = new LinkedHashSet<>();
    private List<RustStructureItemImpl> structureItems = new ArrayList<>();
    private RustStructureItemImpl currStructureItem;
    private final List<SemanticRegion> semanticRegions = new LinkedList<>();
    private static final Set<RustVisibility> VISIBILITY_NA = EnumSet.noneOf(RustVisibility.class);

    void clear() {
        errors.clear();
        blocks.clear();
        structureItems.clear();
        semanticRegions.clear();
    }

    void addSemanticRegion(RustElementKind kind, OffsetRange range) {
        addSemanticRegion("", kind, range, false, VISIBILITY_NA, false);
    }

    void addSemanticRegion(String name, RustElementKind kind, OffsetRange range, boolean mutable, Set<RustVisibility> visibility, boolean statyc) {
        RustElementKind childOf = currStructureItem == null ? null : currStructureItem.kind;
        semanticRegions.add(new SemanticRegion(kind, name, range, mutable, visibility, statyc || (kind == RustElementKind.FUNCTION && currStructureItem == null), childOf));
    }

    List<? extends RustSourceRegion> semanticRegions() {
        return semanticRegions;
    }

    List<? extends RustStructureItem> structureItems() {
        return structureItems;
    }

    boolean hasSyntaxError;

    void addError(Error error) {
        if (hasSyntaxError && !(error instanceof SyntaxError)) {
            // not interested in more subtle errors from ErrNode if
            // we already know the source has syntax errors
            return;
        }
        hasSyntaxError |= error instanceof SyntaxError;
        errors.add(error);
    }

    void addStructureItem(RustStructureItemImpl item) {
        List<RustStructureItemImpl> items = this.structureItems;
        if (currStructureItem != null) {
            items = currStructureItem.nested();
            item.setIn(currStructureItem.qName());
        }
        items.add(item);
    }

    void pushStructureItem(RustStructureItemImpl item, Runnable run) {
        RustStructureItemImpl prev = currStructureItem;
        addStructureItem(item);
        currStructureItem = item;
        try {
            run.run();
        } finally {
            currStructureItem = prev;
        }
    }

    void addBlock(RustParser.BlockContext ctx) {
        blocks.add(new OffsetRange(ctx.getSourceInterval().a, ctx.getSourceInterval().b));
    }

    public List<Error> errors() {
        return new ArrayList<>(errors);
    }

    public List<OffsetRange> blocks() {
        return new ArrayList<>(blocks);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("errors: [");
        for (Iterator<Error> it = errors.iterator(); it.hasNext();) {
            Error err = it.next();
            sb.append(err.getDescription()).append('@').append(err.getStartPosition())
                    .append(':').append(err.getEndPosition());
            if (it.hasNext()) {
                sb.append(',');
            }
        }
        sb.append("] structure: [");
        for (Iterator<RustStructureItemImpl> it = structureItems.iterator(); it.hasNext();) {
            sb.append(it.next());
            if (it.hasNext()) {
                sb.append(',');
            }
        }
        sb.append("] blocks [");
        for (Iterator<OffsetRange> it = blocks.iterator(); it.hasNext();) {
            OffsetRange range = it.next();
            sb.append(range.getStart()).append(":").append(range.getEnd());
            if (it.hasNext()) {
                sb.append(',');
            }
        }
        sb.append("] semantic [");
        for (Iterator<SemanticRegion> it = semanticRegions.iterator(); it.hasNext();) {
            sb.append(it.next());
            if (it.hasNext()) {
                sb.append(',');
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
