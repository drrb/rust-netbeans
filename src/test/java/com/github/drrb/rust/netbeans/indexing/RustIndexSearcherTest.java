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

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import static com.github.drrb.rust.netbeans.test.Matchers.*;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.netbeans.api.project.Project;
import org.netbeans.modules.csl.api.IndexSearcher;
import org.netbeans.modules.parsing.spi.indexing.support.QuerySupport;
import org.openide.filesystems.FileObject;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 *
 */
@RunWith(PowerMockRunner.class)
public class RustIndexSearcherTest {

    @Mock
    private RustIndex index;
    private RustIndexSearcher indexSearcher;
    private RustIndexReader indexReader;

    @Before
    public void setUp() {
        indexReader = mock(RustIndexReader.class);
        indexSearcher = new RustIndexSearcher(index);
    }

    @Test
    public void shouldReadIndex() throws Exception {
        Project project = mock(Project.class);
        FileObject file = mock(FileObject.class);
        List<IndexedRustStruct> indexedStructs = new LinkedList<>();
        IndexedRustStruct indexedRustStruct = new IndexedRustStruct();
        indexedRustStruct.file = file;
        indexedRustStruct.name = "Point";
        indexedRustStruct.module = "math";
        indexedRustStruct.offsetStart = 10;
        indexedRustStruct.offsetEnd = 20;
        indexedStructs.add(indexedRustStruct);

        when(index.createIndexReader(project)).thenReturn(indexReader);
        when(indexReader.findStructsByName("Point", QuerySupport.Kind.PREFIX)).thenReturn(indexedStructs);

        Set<? extends IndexSearcher.Descriptor> results = indexSearcher.getTypes(project, "Point", QuerySupport.Kind.PREFIX, null);
        assertThat(results, hasSize(1));
    }
}
