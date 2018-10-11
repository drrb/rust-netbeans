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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.netbeans.modules.parsing.spi.indexing.support.IndexDocument;
import org.netbeans.modules.parsing.spi.indexing.support.IndexResult;
import org.openide.filesystems.FileObject;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static com.github.drrb.rust.netbeans.test.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;

/**
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({IndexDocument.class, IndexResult.class})
public class IndexItemSerializerTest {

    private IndexItemSerializer serializer;

    @Before
    public void setUp() {
        serializer = new IndexItemSerializer();
    }

    @Test
    public void shouldWriteClassFieldsToDocument() {
        TestSerializableRustThing serializable = new TestSerializableRustThing();
        serializable.name = "Point";
        serializable.start = 1;
        serializable.end = 10;

        IndexDocument document = mock(IndexDocument.class);

        serializer.serialize(document, serializable);

        verify(document).addPair("name", "Point", true, true);
        verify(document).addPair("start", "1", true, true);
        verify(document).addPair("end", "10", true, true);
    }

    @Test
    public void shouldReadClassFieldsFromDocument() {
        IndexResult indexResult = mock(IndexResult.class);
        FileObject file = mock(FileObject.class);

        when(indexResult.getFile()).thenReturn(file);
        when(indexResult.getValue("name")).thenReturn("Point");
        when(indexResult.getValue("start")).thenReturn("1");
        when(indexResult.getValue("end")).thenReturn("10");

        TestSerializableRustThing serialized = serializer.deserialize(indexResult, TestSerializableRustThing.class);
        assertThat(serialized.name, is("Point"));
        assertThat(serialized.start, is(1));
        assertThat(serialized.end, is(10));
        assertThat(serialized.file, is(file));
    }

    public static class TestSerializableRustThing {

        @IndexedString("name")
        private String name;
        @IndexedString("start")
        private int start;
        @IndexedString("end")
        private int end;
        @IndexedFile
        private FileObject file;
    }
}
