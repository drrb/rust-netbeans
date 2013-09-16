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
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.parsing.spi.indexing.Context;
import org.netbeans.modules.parsing.spi.indexing.EmbeddingIndexer;
import org.netbeans.modules.parsing.spi.indexing.EmbeddingIndexerFactory;
import org.netbeans.modules.parsing.spi.indexing.Indexable;
import org.openide.util.Exceptions;

/**
 *
 */
public class RustIndexer extends EmbeddingIndexer {

    public static final String NAME = "rust";
    public static final int VERSION = 0;
    private final RustIndex index;

    public RustIndexer(RustIndex index) {
        this.index = index;
    }
    private static final Logger LOGGER = Logger.getLogger(RustIndexer.class.getName());

    @Override
    protected void index(Indexable indexable, Parser.Result parserResult, Context context) {
        LOGGER.log(Level.INFO, "RustIndexer.index({0})", indexable.getRelativePath());
        try {
            RustIndexWriter indexWriter = index.createIndexWriter(context);
            NetbeansRustParserResult parseResult = (NetbeansRustParserResult) parserResult;
            RustSourceIndex index = parseResult.getIndex();
            List<RustStruct> structs = index.getStructs();
            for (RustStruct struct : structs) {
                indexWriter.write(indexable, struct);
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public static class Factory extends EmbeddingIndexerFactory {

        private static final Logger LOGGER = Logger.getLogger(Factory.class.getName());

        @Override
        public EmbeddingIndexer createIndexer(Indexable indexable, Snapshot snapshot) {
            return new RustIndexer(new RustIndex());
        }

        @Override
        public void filesDeleted(Iterable<? extends Indexable> deletedIndexables, Context context) {
            LOGGER.log(Level.WARNING, "filesDeleted({0})", deletedIndexables);
        }

        @Override
        public void filesDirty(Iterable<? extends Indexable> dirty, Context context) {
            LOGGER.log(Level.WARNING, "filesDirty({0})", dirty);
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
