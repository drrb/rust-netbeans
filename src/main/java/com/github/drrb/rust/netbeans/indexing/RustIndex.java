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

import com.github.drrb.rust.netbeans.util.Lists;
import java.io.IOException;
import java.util.Collection;
import org.netbeans.api.project.Project;
import org.netbeans.modules.parsing.spi.indexing.Context;
import org.netbeans.modules.parsing.spi.indexing.support.IndexingSupport;
import org.netbeans.modules.parsing.spi.indexing.support.QuerySupport;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

/**
 *
 */
public class RustIndex {

    public RustIndexWriter createIndexWriter(Context context) {
        try {
            return new RustIndexWriter(IndexingSupport.getInstance(context));
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            return RustIndexWriter.NO_OP;
        }
    }

    public RustIndexReader createIndexReader(Project project) {
        try {
            Collection<FileObject> roots = QuerySupport.findRoots(project, null, null, null);
            QuerySupport querySupport = QuerySupport.forRoots(RustIndexer.NAME, RustIndexer.VERSION, Lists.toArray(roots, FileObject.class));
            return new RustIndexReader(querySupport);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            return RustIndexReader.EMPTY;
        }
    }
}
