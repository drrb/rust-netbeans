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

import static com.github.drrb.rust.netbeans.test.Matchers.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.any;
import static org.powermock.api.mockito.PowerMockito.mock;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.parsing.spi.indexing.support.IndexResult;
import org.netbeans.modules.parsing.spi.indexing.support.QuerySupport;
import org.netbeans.modules.parsing.spi.indexing.support.QuerySupport.Kind;
import org.openide.filesystems.FileObject;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({QuerySupport.class, IndexResult.class})
public class RustIndexReaderTest {

    @Mock
    private QuerySupport querySupport;
    private RustIndexReader indexReader;

    @Before
    public void setUp() {
        indexReader = new RustIndexReader(querySupport);
    }

    @Test
    public void shouldFindStructsFromIndex() throws Exception {
        IndexResult result = mock(IndexResult.class);
        FileObject file = mock(FileObject.class);
        List<IndexResult> indexResults = new LinkedList<>();
        indexResults.add(result);
        when(result.getFile()).thenReturn(file);
        when(result.getValue("struct-name")).thenReturn("Point");
        when(result.getValue("struct-module")).thenReturn("math.geom");
        when(result.getValue("struct-offset-start")).thenReturn("10");
        when(result.getValue("struct-offset-end")).thenReturn("100");
        when(querySupport.query(eq("struct-name"), eq("Po"), eq(Kind.PREFIX), Mockito.<String>anyVararg())).thenReturn((List) indexResults);

        Collection<IndexedRustStruct> indexedStructs = indexReader.findStructsByName("Po", Kind.PREFIX);

        assertThat(indexedStructs, hasSize(1));
        IndexedRustStruct indexedStruct = indexedStructs.iterator().next();
        assertThat(indexedStruct.getName(), is("Point"));
        assertThat(indexedStruct.getModule(), is("math.geom"));
        assertThat(indexedStruct.getOffsetRange(), is(new OffsetRange(10, 100)));
        assertThat(indexedStruct.getFile(), is(file));
    }
}
