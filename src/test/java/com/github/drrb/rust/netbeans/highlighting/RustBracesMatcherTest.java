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
package com.github.drrb.rust.netbeans.highlighting;

import com.github.drrb.rust.netbeans.RustDocument;
import com.github.drrb.rust.netbeans.RustDocument;
import com.github.drrb.rust.netbeans.RustSourceSnapshot;
import com.github.drrb.rust.netbeans.RustSourceSnapshot;
import static com.github.drrb.rust.netbeans.TestParsing.*;
import com.github.drrb.rust.netbeans.parsing.RustLexUtils;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import org.junit.Test;
import static org.mockito.Mockito.*;
import org.netbeans.modules.editor.bracesmatching.SpiAccessor;
import org.netbeans.spi.editor.bracesmatching.MatcherContext;

/**
 *
 */
public class RustBracesMatcherTest {

    private RustSourceSnapshot source = new RustSourceSnapshot();
    private RustBracesMatcher bracesMatcher;

    @Test
    public void shouldFindNoOriginInBracelessDocument() throws Exception {
        //Carat here:  /|/
        source.append("//");
        bracesMatcher = matcherAtOffset(1);

        int[] origin = bracesMatcher.findOrigin();

        assertThat(origin, is(nullValue()));
    }

    @Test
    public void shouldFindOriginNextToBrace() throws Exception {
        //Carat here:  fn main() |{ }
        source.append("fn main() { }");
        bracesMatcher = matcherAtOffset(10);

        int[] origin = bracesMatcher.findOrigin();
        int[] expectedOrigin = {10, 11};

        assertThat(origin, is(expectedOrigin));
    }

    @Test
    public void shouldFindOriginNextToParenthesis() throws Exception {
        //Carat here:  fn main|( ) { }
        source.append("fn main( ) { }");
        bracesMatcher = matcherAtOffset(7);

        int[] origin = bracesMatcher.findOrigin();
        int[] expectedOrigin = {7, 8};

        assertThat(origin, is(expectedOrigin));
    }

    @Test
    public void shouldFindOriginNextToAngleBrackets() throws Exception {
        //Carat here:  fn main|<T> () { }
        source.append("fn main<T> () { }");
        bracesMatcher = matcherAtOffset(7);

        int[] origin = bracesMatcher.findOrigin();
        int[] expectedOrigin = {7, 8};

        assertThat(origin, is(expectedOrigin));
    }

    @Test
    public void shouldFindObviousMatchForwards() throws Exception {
        //Carat here:  fn main<T>() |{ }\n
        source.append("fn main<T>() { }\n");
        bracesMatcher = matcherAtOffset(13);

        int[] origin = bracesMatcher.findMatches();
        int[] expectedOrigin = {15, 16};

        assertThat(origin, is(expectedOrigin));
    }

    @Test
    public void shouldFindObviousMatchBackwards() throws Exception {
        //Carat here:  fn main<T>() { |}
        source.append("fn main<T>() { }");
        bracesMatcher = matcherAtOffset(15);

        int[] origin = bracesMatcher.findMatches();
        int[] expectedOrigin = {13, 14};

        assertThat(origin, is(expectedOrigin));
    }

    private RustBracesMatcher matcherAtOffset(int caretOffset) {
        SpiAccessor spiAccessor = SpiAccessor.get();
        RustDocument document = source.getDocument();
        MatcherContext context = spiAccessor.createCaretContext(document, caretOffset, true, 0);
        RustLexUtils rustLexUtils = mock(RustLexUtils.class);
        when(rustLexUtils.getRustTokenSequence(eq(document), anyInt())).thenReturn(rustTokenSequenceFor(source));
        return new RustBracesMatcher(context, rustLexUtils);
    }
}
