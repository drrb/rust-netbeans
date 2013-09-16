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

import com.github.drrb.rust.netbeans.RustSourceSnapshot;
import com.github.drrb.rust.netbeans.parsing.index.RustStruct;
import static com.github.drrb.rust.netbeans.test.Matchers.contains;
import static com.github.drrb.rust.netbeans.test.Matchers.containsKey;
import java.io.IOException;
import org.antlr.v4.runtime.misc.MultiMap;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.mockito.Mockito.when;
import org.netbeans.modules.parsing.spi.indexing.Context;
import org.netbeans.modules.parsing.spi.indexing.Indexable;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Indexable.class, Context.class})
public class RustIndexerTest {

    private RustIndexer indexer;
    private RustIndex index;
    private StubIndexWriter indexWriter;

    @Before
    public void setUp() throws Exception {
        indexWriter = new StubIndexWriter();
        index = mock(RustIndex.class);
        when(index.createIndexWriter(any(Context.class))).thenReturn(indexWriter);
        indexer = new RustIndexer(index);
    }

    @Test
    public void shouldWriteStructsToIndex() {
        RustSourceSnapshot source = new RustSourceSnapshot();
        source.appendln("struct Point {");
        source.appendln("   x: float,");
        source.appendln("   y: float");
        source.appendln("}");
        source.appendln("struct Person {");
        source.appendln("   first_name: ~str,");
        source.appendln("   last_name: ~str");
        source.appendln("}");

        Indexable file = mock(Indexable.class);
        Context context = mock(Context.class);

        indexer.index(file, source.parse(), context);

        assertThat(indexWriter.structs, containsKey(file).mappedToValueThat(contains(struct("Point"))));
        assertThat(indexWriter.structs, containsKey(file).mappedToValueThat(contains(struct("Person"))));
    }

    private Matcher<RustStruct> struct(final String name) {
        return new TypeSafeMatcher<RustStruct>() {

            @Override
            public boolean matchesSafely(RustStruct item) {
                return item.getName().equals(name);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("struct with name ").appendValue(name);
            }
        };
    }

    private static class StubIndexWriter extends RustIndexWriter {

        final MultiMap<Indexable, RustStruct> structs = new MultiMap<>();

        StubIndexWriter() {
            super(null);
        }

        @Override
        public void write(Indexable file, RustStruct struct) throws IOException {
            structs.map(file, struct);
        }
    }
}
