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

import com.github.drrb.rust.netbeans.parsing.index.RustStruct;
import java.io.IOException;
import org.netbeans.modules.parsing.spi.indexing.Indexable;
import org.netbeans.modules.parsing.spi.indexing.support.IndexDocument;
import org.netbeans.modules.parsing.spi.indexing.support.IndexingSupport;

/**
 *
 */
public class RustIndexWriter {

    public static final RustIndexWriter NO_OP = new RustIndexWriter(null) {
        @Override
        public void write(Indexable file, RustStruct struct) throws IOException {
        }
    };
    private final IndexingSupport indexingSupport;
    private final IndexItemSerializer serializer;

    public RustIndexWriter(IndexingSupport indexingSupport) {
        this.indexingSupport = indexingSupport;
        this.serializer = new IndexItemSerializer();
    }

    public void write(Indexable file, RustStruct struct) throws IOException {
        IndexDocument document = indexingSupport.createDocument(file);
        IndexedRustStruct indexedStruct = new IndexedRustStruct(struct);
        serializer.serialize(document, indexedStruct);
        indexingSupport.addDocument(document);
    }
}
