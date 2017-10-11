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

import com.github.drrb.rust.netbeans.RustLanguage;
import javax.swing.text.BadLocationException;
import org.netbeans.api.editor.mimelookup.MimePath;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.spi.editor.typinghooks.TypedTextInterceptor;

/**
 *
 */
public class RustBracketCompleter implements TypedTextInterceptor {

    @Override
    public boolean beforeInsert(Context context) throws BadLocationException {
        return false;
    }

    @Override
    public void insert(MutableContext context) throws BadLocationException {
        char typedCharacter = context.getText().charAt(0);
        switch (typedCharacter) {
            case '(': {
                context.setText("()", 1);
                break;
            }
            case ')': {
                char nextCharacter = context.getDocument().getText(context.getOffset(), 1).charAt(0);
                if (nextCharacter == ')') {
                    context.getDocument().remove(context.getOffset(), 1);
                }
                break;
            }
        }
    }

    @Override
    public void afterInsert(Context context) throws BadLocationException {
    }

    @Override
    public void cancelled(Context context) {
    }

    @MimeRegistration(mimeType = RustLanguage.MIME_TYPE, service = TypedTextInterceptor.Factory.class)
    public static class Factory implements TypedTextInterceptor.Factory {

        @Override
        public TypedTextInterceptor createTypedTextInterceptor(MimePath mimePath) {
            if (RustLanguage.MIME_TYPE.equals(mimePath.getPath())) {
                return new RustBracketCompleter();
            } else {
                return null;
            }
        }

    }
}
