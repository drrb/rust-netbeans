/**
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
package com.github.drrb.rust.netbeans.completion;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JToolTip;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.editor.completion.Completion;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;
import org.netbeans.spi.editor.completion.support.CompletionUtilities;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;

/**
 *
 */
public class RustCompletionItem implements CompletionItem {

    @StaticResource
    private static final String FIELD_ICON_PATH = "com/github/drrb/rust/netbeans/resources/field.png";
    @StaticResource
    private static final String FUNCTION_ICON_PATH = "com/github/drrb/rust/netbeans/resources/function.png";
    @StaticResource
    private static final String PACKAGE_ICON_PATH = "com/github/drrb/rust/netbeans/resources/package.gif";
    @StaticResource
    private static final String STRUCT_ICON_PATH = "com/github/drrb/rust/netbeans/resources/struct.png";
    @StaticResource
    private static final String TRAIT_ICON_PATH = "com/github/drrb/rust/netbeans/resources/trait.png";
    public enum Type {

        FIELD("0x0000B2", FIELD_ICON_PATH),
        FUNCTION("0x0000B2", FUNCTION_ICON_PATH),
        KEYWORD("0x0000B2", FIELD_ICON_PATH),
        PACKAGE("0x0000B2", PACKAGE_ICON_PATH),
        STRUCT("0x0000B2", STRUCT_ICON_PATH),
        TRAIT("0x0000B2", TRAIT_ICON_PATH);
        private final Color color;
        private final ImageIcon icon;

        private Type(String color, String imagePath) {
            this.color = Color.decode(color);
            this.icon = ImageUtilities.loadImageIcon(imagePath, false);
        }
    }
    private final Type type;
    private final String text;
    private final int dotOffset;
    private final int caretOffset;

    public RustCompletionItem(Type type, CharSequence text, int dotOffset, int caretOffset) {
        this.type = type;
        this.text = text.toString();
        this.dotOffset = dotOffset;
        this.caretOffset = caretOffset;
    }

    @Override
    public void defaultAction(JTextComponent textComponent) {
        try {
            StyledDocument doc = (StyledDocument) textComponent.getDocument();
            //Here we remove the characters starting at the start offset
            //and ending at the point where the caret is currently found:
            doc.remove(dotOffset, caretOffset - dotOffset);
            doc.insertString(dotOffset, text, null);
            Completion.get().hideAll();
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public void processKeyEvent(KeyEvent evt) {
    }

    @Override
    public int getPreferredWidth(Graphics graphics, Font font) {
        return CompletionUtilities.getPreferredWidth(text, null, graphics, font);
    }

    @Override
    public void render(Graphics g, Font defaultFont, Color defaultColor, Color backgroundColor, int width, int height, boolean selected) {
        CompletionUtilities.renderHtml(type.icon, text, null, g, defaultFont, (selected ? Color.white : type.color), width, height, selected);
    }

    @Override
    public CompletionTask createDocumentationTask() {
        return new AsyncCompletionTask(new AsyncCompletionQuery() {
            @Override
            protected void query(CompletionResultSet completionResultSet, Document document, int i) {
                completionResultSet.setDocumentation(new RustCompletionDocumentation(RustCompletionItem.this));
                completionResultSet.finish();
            }
        });
    }

    @Override
    public CompletionTask createToolTipTask() {
        return new AsyncCompletionTask(new AsyncCompletionQuery() {
            @Override
            protected void query(CompletionResultSet completionResultSet, Document document, int i) {
                JToolTip toolTip = new JToolTip();
                toolTip.setTipText(String.format("Press Enter to insert \"%s\"", text));
                completionResultSet.setToolTip(toolTip);
                completionResultSet.finish();
            }
        });
    }

    @Override
    public boolean instantSubstitution(JTextComponent component) {
        return false;
    }

    @Override
    public int getSortPriority() {
        return 0;
    }

    @Override
    public CharSequence getSortText() {
        return text;
    }

    @Override
    public CharSequence getInsertPrefix() {
        return text;
    }

    public String getText() {
        return text;
    }
}
