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
package com.github.drrb.rust.netbeans.highlighting;

import javax.swing.text.Document;

import com.github.drrb.rust.netbeans.RustDocument;
import static com.github.drrb.rust.netbeans.TestParsing.*;
import com.github.drrb.rust.netbeans.parsing.RustLexUtils;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.netbeans.spi.editor.bracesmatching.MatcherContext;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(MatcherContext.class)
public class RustBracesMatcherTest {

    @Mock
    private MatcherContext context;
    @Mock
    private RustLexUtils rustLexUtils;
    private RustBracesMatcher bracesMatcher;

    @Before
    public void setUp() {
        bracesMatcher = new RustBracesMatcher(context, rustLexUtils);
    }

    @Test
    public void shouldFindNoOriginInBracelessDocument() throws Exception {
        StringBuilder source = new StringBuilder();
        //Carat here:  /|/
        source.append("//");
        Document document = RustDocument.containing(source);

        when(context.getDocument()).thenReturn(document);
        when(context.getSearchOffset()).thenReturn(1);
        when(rustLexUtils.getRustTokenSequence(document, 1)).thenReturn(rustTokenSequenceFor(source));

        int[] origin = bracesMatcher.findOrigin();

        assertThat(origin, is(nullValue()));
    }

    @Test
    public void shouldFindOriginNextToBrace() throws Exception {
        StringBuilder source = new StringBuilder();
        //Carat here:  fn main() |{ }
        source.append("fn main() { }");
        Document document = RustDocument.containing(source);

        when(context.getDocument()).thenReturn(document);
        when(context.getSearchOffset()).thenReturn(10);
        when(rustLexUtils.getRustTokenSequence(document, 10)).thenReturn(rustTokenSequenceFor(source));

        int[] origin = bracesMatcher.findOrigin();
        int[] expectedOrigin = {10, 11};

        assertThat(origin, is(expectedOrigin));
    }

    @Test
    public void shouldFindOriginNextToParenthesis() throws Exception {
        StringBuilder source = new StringBuilder();
        //Carat here:  fn main|( ) { }
        source.append("fn main( ) { }");
        Document document = RustDocument.containing(source);

        when(context.getDocument()).thenReturn(document);
        when(context.getSearchOffset()).thenReturn(7);
        when(rustLexUtils.getRustTokenSequence(document, 7)).thenReturn(rustTokenSequenceFor(source));

        int[] origin = bracesMatcher.findOrigin();
        int[] expectedOrigin = {7, 8};

        assertThat(origin, is(expectedOrigin));
    }

    @Test
    public void shouldFindOriginNextToAngleBrackets() throws Exception {
        StringBuilder source = new StringBuilder();
        //Carat here:  fn main|<T> () { }
        source.append("fn main<T> () { }");
        Document document = RustDocument.containing(source);

        when(context.getDocument()).thenReturn(document);
        when(context.getSearchOffset()).thenReturn(7);
        when(rustLexUtils.getRustTokenSequence(document, 7)).thenReturn(rustTokenSequenceFor(source));

        int[] origin = bracesMatcher.findOrigin();
        int[] expectedOrigin = {7, 8};

        assertThat(origin, is(expectedOrigin));
    }

    @Test
    public void shouldFindObviousMatchForwards() throws Exception {
        StringBuilder source = new StringBuilder();
        //Carat here:  fn main<T>() |{ }\n
        source.append("fn main<T>() { }\n");
        Document document = RustDocument.containing(source);

        when(context.getDocument()).thenReturn(document);
        when(context.getSearchOffset()).thenReturn(13);
        when(rustLexUtils.getRustTokenSequence(document, 13)).thenReturn(rustTokenSequenceFor(source));

        int[] origin = bracesMatcher.findMatches();
        int[] expectedOrigin = {15, 16};

        assertThat(origin, is(expectedOrigin));
    }

    @Test
    public void shouldFindObviousMatchBackwards() throws Exception {
        StringBuilder source = new StringBuilder();
        //Carat here:  fn main<T>() { |}
        source.append("fn main<T>() { }");
        Document document = RustDocument.containing(source);

        when(context.getDocument()).thenReturn(document);
        when(context.getSearchOffset()).thenReturn(15);
        when(rustLexUtils.getRustTokenSequence(document, 15)).thenReturn(rustTokenSequenceFor(source));

        int[] origin = bracesMatcher.findMatches();
        int[] expectedOrigin = {13, 14};

        assertThat(origin, is(expectedOrigin));
    }
}
