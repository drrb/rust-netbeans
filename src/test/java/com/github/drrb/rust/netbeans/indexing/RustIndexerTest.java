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
import com.github.drrb.rust.netbeans.test.NetbeansWithRust;
import org.junit.Rule;
import org.junit.Test;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.parsing.spi.indexing.support.QuerySupport;
import org.openide.filesystems.FileObject;

import java.io.IOException;
import java.util.Collection;

import static com.github.drrb.rust.netbeans.test.Matchers.is;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertThat;
import static org.netbeans.modules.parsing.spi.indexing.support.QuerySupport.Kind.PREFIX;

public class RustIndexerTest {

    @Rule
    public final NetbeansWithRust netbeans = new NetbeansWithRust();

    @Test
    public void indexesStructs() throws Exception {
        netbeans.checkIndexer("index/structs.rs");

        FileObject testFile = netbeans.getTestFile("index/structs.rs");

        Collection<IndexedRustStruct> structs = indexWith(testFile).findStructsByName("Poi", PREFIX);
        assertThat(structs, is(asList(
                IndexedRustStruct.builder()
                        .withFile(testFile)
                        .withStruct(
                                RustStruct.builder()
                                        .setName("Point")
                                        .setOffsetRange(new OffsetRange(0, 43))
                                        .build()
                        ).build()
                )
        ));
    }

    private RustIndexReader indexWith(FileObject testFile) throws IOException {
        FileObject sourceRoot = testFile.getParent();
        return new RustIndexReader(QuerySupport.forRoots(RustIndexer.NAME, RustIndexer.VERSION, sourceRoot));
    }
}
