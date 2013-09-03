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

import com.github.drrb.rust.netbeans.parsing.NetbeansRustParser.NetbeansRustParserResult;
import com.github.drrb.rust.netbeans.parsing.index.RustSourceIndex;
import com.github.drrb.rust.netbeans.parsing.index.RustStruct;
import com.github.drrb.rust.netbeans.parsing.index.RustStructField;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.parsing.spi.indexing.Context;
import org.netbeans.modules.parsing.spi.indexing.EmbeddingIndexer;
import org.netbeans.modules.parsing.spi.indexing.EmbeddingIndexerFactory;
import org.netbeans.modules.parsing.spi.indexing.Indexable;
import org.netbeans.modules.parsing.spi.indexing.support.IndexDocument;
import org.netbeans.modules.parsing.spi.indexing.support.IndexingSupport;
import org.openide.util.Exceptions;

/**
 *
 */
public class RustIndexer extends EmbeddingIndexer {

    public static final String NAME = "rust";
    public static final int VERSION = 0;
    private static final Logger LOGGER = Logger.getLogger(RustIndexer.class.getName());

    @Override
    protected void index(Indexable indexable, Parser.Result parserResult, Context context) {
        LOGGER.log(Level.WARNING, "RustIndexer.index({0})", indexable.getRelativePath());
        try {
            NetbeansRustParserResult parseResult = (NetbeansRustParserResult) parserResult;
            IndexingSupport indexingSupport = IndexingSupport.getInstance(context);
            RustSourceIndex index = parseResult.getIndex();
            List<IndexDocument> indexDocuments = new LinkedList<>();
            List<RustStruct> structs = index.getStructs();
            for (RustStruct struct : structs) {
                IndexDocument structDocument = indexingSupport.createDocument(indexable);
                structDocument.addPair("struct", struct.getName(), true, true);
                structDocument.addPair("struct_simpleName", struct.getName(), true, true);
                structDocument.addPair("struct_moduleName", "mymodule", true, true);
                structDocument.addPair("struct_offsetStart", String.valueOf(struct.getOffsetRange().getStart()), true, true);
                structDocument.addPair("struct_offsetEnd", String.valueOf(struct.getOffsetRange().getEnd()), true, true);
                for (RustStructField field : struct.getBody().getFields()) {
                    structDocument.addPair("struct_field", field.getName(), true, true);
                }
                indexDocuments.add(structDocument);
            }
            for (IndexDocument indexDocument : indexDocuments) {
                indexingSupport.addDocument(indexDocument);
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public static class Factory extends EmbeddingIndexerFactory {

        @Override
        public EmbeddingIndexer createIndexer(Indexable indexable, Snapshot snapshot) {
            return new RustIndexer();
        }

        @Override
        public void filesDeleted(Iterable<? extends Indexable> deleted, Context context) {
        }

        @Override
        public void filesDirty(Iterable<? extends Indexable> dirty, Context context) {
        }

        @Override
        public String getIndexerName() {
            return NAME;
        }

        @Override
        public int getIndexVersion() {
            return VERSION;
        }
    }
}
