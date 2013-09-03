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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.parsing.spi.indexing.support.IndexResult;
import org.netbeans.modules.parsing.spi.indexing.support.QuerySupport;
import org.openide.filesystems.FileObject;

/**
 *
 */
public class RustIndex {

    private static final Logger LOGGER = Logger.getLogger(RustIndex.class.getName());
    private static final RustIndex EMPTY = new RustIndex(null);

    public static RustIndex get(Collection<FileObject> roots) {
        try {
            QuerySupport querySupport = QuerySupport.forRoots(RustIndexer.NAME, RustIndexer.VERSION, roots.toArray(new FileObject[roots.size()]));
            return new RustIndex(querySupport);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Couldn't get QuerySupport", e);
            return EMPTY;
        }
    }
    private final QuerySupport querySupport;

    RustIndex(QuerySupport querySupport) {
        this.querySupport = querySupport;
    }

    private Collection<? extends IndexResult> query(
            final String fieldName, final String fieldValue,
            final QuerySupport.Kind kind, final String... fieldsToLoad) {
        if (querySupport == null) {
            return Collections.emptySet();
        }
        try {
            return querySupport.query(fieldName, fieldValue, kind, fieldsToLoad);
        } catch (IOException ioe) {
            LOGGER.log(Level.WARNING, null, ioe);
        }

        return Collections.emptySet();
    }
}
