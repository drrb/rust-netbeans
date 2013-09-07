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
package com.github.drrb.rust.netbeans.indexing;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.project.Project;
import org.netbeans.modules.csl.api.ElementKind;
import org.netbeans.modules.csl.api.IndexSearcher;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.parsing.spi.indexing.support.IndexResult;
import org.netbeans.modules.parsing.spi.indexing.support.QuerySupport;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

/**
 *
 */
public class RustIndexSearcher implements IndexSearcher {

    @Override
    public Set<? extends IndexSearcher.Descriptor> getTypes(Project project, String textForQuery, QuerySupport.Kind searchType, IndexSearcher.Helper helper) {
        Logger.getLogger(RustIndexSearcher.class.getName()).log(Level.WARNING, "RustIndexSearcher.getTypes");
        Set<RustIndexSearchResult> results = new HashSet<>();
        Collection<FileObject> roots = QuerySupport.findRoots(project, null, null, null);
        try {
            QuerySupport querySupport = QuerySupport.forRoots("rust", 0, (FileObject[]) roots.toArray());
            Collection<? extends IndexResult> queryResults = querySupport.query("struct", textForQuery, searchType);
            for (IndexResult indexResult : queryResults) {
                String simpleName = indexResult.getValue("struct_simpleName");
                String moduleName = indexResult.getValue("struct_moduleName");
                OffsetRange offsetRange = new OffsetRange(Integer.valueOf(indexResult.getValue("struct_startOffset")), Integer.valueOf(indexResult.getValue("struct_endOffset")));
                FileObject fileObject = indexResult.getFile();
                RustIndexSearchResult result = new RustIndexSearchResult(simpleName, moduleName, ElementKind.CLASS, offsetRange, fileObject, project);
                results.add(result);
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return results;
    }

    @Override
    public Set<? extends IndexSearcher.Descriptor> getSymbols(Project project, String textForQuery, QuerySupport.Kind searchType, IndexSearcher.Helper helper) {
        Logger.getLogger(RustIndexSearcher.class.getName()).log(Level.WARNING, "RustIndexSearcher.getSymbols()");
        return Collections.emptySet();
    }
}
