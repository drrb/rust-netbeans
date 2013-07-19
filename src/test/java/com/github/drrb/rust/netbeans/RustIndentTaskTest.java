/*
 * Copyright (C) 2013 drrb
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.drrb.rust.netbeans;

import javax.swing.text.Document;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.netbeans.modules.editor.indent.spi.Context;
import static org.mockito.Mockito.*;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class RustIndentTaskTest {
    
    @Mock
    private Context context;
    private RustIndentTask indentTask;
    
    @Before
    public void setUp() {
        indentTask = new RustIndentTask(context);
    }
    
    @Test
    public void shouldIndentLineTheSameAsThePreviousLine() throws Exception {
        StringBuilder source = new StringBuilder();
        source.append("    let x = 0;\n") //As though we just pressed return at the end of this line
              .append("");
        Document document = RustDocument.containing(source);
        
        when(context.document()).thenReturn(document);
        when(context.startOffset()).thenReturn(15);
        when(context.lineStartOffset(15)).thenReturn(15);
        when(context.lineStartOffset(14)).thenReturn(0);
        when(context.lineIndent(0)).thenReturn(4);

        indentTask.reindent();
        
        verify(context).modifyIndent(15, 4);
    }
    
    @Test
    public void shouldIncreaseIndentIfPreviousLineEndsInOpenBrace() throws Exception {
        StringBuilder source = new StringBuilder();
        source.append("    fn main() {\n") //As though we just pressed return at the end of this line
              .append("");
        Document document = RustDocument.containing(source);
        
        when(context.document()).thenReturn(document);
        when(context.startOffset()).thenReturn(16);
        when(context.lineStartOffset(16)).thenReturn(16);
        when(context.lineStartOffset(15)).thenReturn(0);
        when(context.lineIndent(0)).thenReturn(4);

        indentTask.reindent();
        
        verify(context).modifyIndent(16, 8);
    }
}
