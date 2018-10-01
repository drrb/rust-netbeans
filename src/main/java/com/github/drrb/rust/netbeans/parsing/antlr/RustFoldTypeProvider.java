/*
 * Copyright (C) 2018 Tim Boudreau
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

package com.github.drrb.rust.netbeans.parsing.antlr;

import com.github.drrb.rust.netbeans.RustLanguage;
import java.util.Arrays;
import java.util.Collection;
import org.netbeans.api.editor.fold.FoldTemplate;
import org.netbeans.api.editor.fold.FoldType;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.api.editor.mimelookup.MimeRegistrations;
import org.netbeans.spi.editor.fold.FoldTypeProvider;

/**
 *
 * @author Tim Boudreau
 */
@MimeRegistrations({
    @MimeRegistration(
            mimeType = RustLanguage.MIME_TYPE,
            service = FoldTypeProvider.class,
            position = 1488)})
public class RustFoldTypeProvider implements FoldTypeProvider {

    public static final String COMMENTS = "comments";
    public static final String BLOCKS = "blocks";
    private static final FoldType BLOCK_FOLDS = FoldType.create(BLOCKS, BLOCKS, FoldTemplate.DEFAULT_BLOCK );
    private static final FoldType COMMENT_FOLDS = FoldType.create(COMMENTS, COMMENTS, FoldTemplate.DEFAULT_BLOCK );

    @Override
    @SuppressWarnings("unchecked")
    public Collection getValues(Class type) {
        return Arrays.asList( BLOCK_FOLDS, COMMENT_FOLDS );
    }

    @Override
    public boolean inheritable() {
        return true;
    }
}
