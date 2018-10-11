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
package com.github.drrb.rust.netbeans.indexing;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.project.Project;
import org.netbeans.modules.csl.api.ElementKind;
import org.netbeans.modules.csl.api.IndexSearcher;
import org.netbeans.modules.parsing.spi.indexing.support.QuerySupport;

/**
 *
 */
public class RustIndexSearcher implements IndexSearcher {

    private static final Logger LOGGER = Logger.getLogger(RustIndexSearcher.class.getName());
    private final RustIndex index;

    public RustIndexSearcher() {
        this(new RustIndex());
    }

    public RustIndexSearcher(RustIndex index) {
        this.index = index;
    }

    @Override
    public Set<? extends IndexSearcher.Descriptor> getTypes(Project project, String textForQuery, QuerySupport.Kind searchType, IndexSearcher.Helper helper) {
        RustIndexReader indexReader = index.createIndexReader(project);
        Collection<IndexedRustStruct> structs = indexReader.findStructsByName(textForQuery, searchType);
        Set<RustIndexSearchResult> results = new HashSet<>(structs.size());
        for (IndexedRustStruct struct : structs) {
            results.add(new RustIndexSearchResult(struct.getName(), struct.getModule(), ElementKind.CLASS, struct.getOffsetRange(), struct.getFile(), project));
        }
        return results;
    }

    @Override
    public Set<? extends IndexSearcher.Descriptor> getSymbols(Project project, String textForQuery, QuerySupport.Kind searchType, IndexSearcher.Helper helper) {
        LOGGER.log(Level.WARNING, "RustIndexSearcher.getSymbols()");
        return Collections.emptySet();
    }
}
