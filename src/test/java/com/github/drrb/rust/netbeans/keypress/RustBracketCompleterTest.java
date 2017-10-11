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
package com.github.drrb.rust.netbeans.keypress;

import com.github.drrb.rust.netbeans.RustDocument;
import static org.hamcrest.core.Is.is;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.runner.RunWith;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import org.netbeans.spi.editor.typinghooks.TypedTextInterceptor.Context;
import org.netbeans.spi.editor.typinghooks.TypedTextInterceptor.MutableContext;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @todo Try to test this with CslTestBase/golden files instead
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(MutableContext.class)
public class RustBracketCompleterTest {

    private RustBracketCompleter interceptor;

    @Before
    public void setUp() {
        interceptor = new RustBracketCompleter();
    }

    @Test
    public void shouldNotStopAnEdit() throws Exception {
        Context context = mock(Context.class);
        assertThat(interceptor.beforeInsert(context), is(false));
    }

    @Test
    public void shouldAddClosingBracketIfTypingAnOpeningBracket() throws Exception {
        MutableContext context = mock(MutableContext.class);
        when(context.getText()).thenReturn("(");
        interceptor.insert(context);
        verify(context).setText("()", 1);
    }

    @Test
    public void shouldOverwriteClosingBracketIfItAlreadyExists() throws Exception {
        MutableContext context = mock(MutableContext.class);
        when(context.getText()).thenReturn(")");
        when(context.getOffset()).thenReturn(2);
        RustDocument document = RustDocument.containing("())");
        when(context.getDocument()).thenReturn(document);
        interceptor.insert(context);
        assertThat(document.getText(0, document.getLength()), is("()"));
    }

    @Test
    public void shouldIgnoreTypingSomethingElse() throws Exception {
        MutableContext context = mock(MutableContext.class);
        when(context.getText()).thenReturn("x");
        verify(context, never()).setText(anyString(), anyInt());
        interceptor.insert(context);
    }

}
