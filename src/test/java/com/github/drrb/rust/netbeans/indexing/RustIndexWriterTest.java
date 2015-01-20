/**
 * Copyright (C) 2015 drrb
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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.parsing.spi.indexing.Indexable;
import org.netbeans.modules.parsing.spi.indexing.support.IndexDocument;
import org.netbeans.modules.parsing.spi.indexing.support.IndexingSupport;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({IndexingSupport.class, Indexable.class, IndexDocument.class})
public class RustIndexWriterTest {

    @Mock
    private IndexingSupport indexingSupport;
    @Mock
    private Indexable file;
    @Mock
    private IndexDocument document;
    private RustIndexWriter indexWriter;

    @Before
    public void setUp() {
        indexWriter = new RustIndexWriter(indexingSupport);
    }

    @Test
    public void shouldWriteStructToIndex() throws Exception {
        RustStruct struct = RustStruct.builder()
                .setName("Point")
                .setOffsetRange(new OffsetRange(10, 20))
                .build();
        when(indexingSupport.createDocument(file)).thenReturn(document);
        indexWriter.write(file, struct);

        verify(document).addPair("struct-name", "Point", true, true);
        verify(document).addPair("struct-name-lowercase", "point", true, true);
        verify(document).addPair("struct-module", "xxx", true, true);
        verify(document).addPair("struct-offset-start", "10", true, true);
        verify(document).addPair("struct-offset-end", "20", true, true);
    }
}
