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

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.parsing.spi.indexing.support.IndexResult;
import org.netbeans.modules.parsing.spi.indexing.support.QuerySupport;
import org.openide.util.Exceptions;

/**
 *
 */
public class RustIndexReader {

    public static final RustIndexReader EMPTY = new RustIndexReader(null) {
        @Override
        public Collection<? extends IndexResult> performQuery(IndexKey indexKey, String textForQuery, QuerySupport.Kind searchType, String... fieldsToLoad) {
            return Collections.emptyList();
        }
    };
    private static final Logger LOGGER = Logger.getLogger(RustIndexReader.class.getName());
    private final QuerySupport querySupport;
    private final IndexItemSerializer serializer;

    public RustIndexReader(QuerySupport querySupport) {
        this.querySupport = querySupport;
        this.serializer = new IndexItemSerializer();
    }

    public Collection<IndexedRustStruct> findStructsByName(String query, QuerySupport.Kind searchType) {
        IndexKey indexKey = searchType == QuerySupport.Kind.CASE_INSENSITIVE_PREFIX ? IndexKey.STRUCT_NAME_LOWERCASE : IndexKey.STRUCT_NAME;
        List<IndexedRustStruct> structs = new LinkedList<>();
        Collection<? extends IndexResult> queryResults = performQuery(indexKey, query, searchType, serializer.getKeys(IndexedRustStruct.class));
        for (IndexResult result : queryResults) {
            structs.add(serializer.deserialize(result, IndexedRustStruct.class));
        }
        return structs;
    }

    public Collection<? extends IndexResult> performQuery(IndexKey indexKey, String textForQuery, QuerySupport.Kind searchType, String... fieldsToLoad) {
        LOGGER.log(Level.WARNING, "RustIndexReader.performQuery({0}, {1}, {2})", new Object[]{indexKey.key(), textForQuery, searchType});
        Collection<? extends IndexResult> queryResults;
        try {
            queryResults = querySupport.query(indexKey.key(), textForQuery, searchType, fieldsToLoad);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            queryResults = Collections.emptySet();
        }
        LOGGER.log(Level.WARNING, "   - returning {0} results", queryResults.size());
        return queryResults;
    }
}
