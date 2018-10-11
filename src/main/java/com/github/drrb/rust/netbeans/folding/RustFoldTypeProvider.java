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
package com.github.drrb.rust.netbeans.folding;

import com.github.drrb.rust.netbeans.RustLanguage;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.netbeans.api.editor.fold.FoldType;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.spi.editor.fold.FoldTypeProvider;

/**
 *
 */
@MimeRegistration(mimeType = RustLanguage.MIME_TYPE, service = FoldTypeProvider.class)
public class RustFoldTypeProvider implements FoldTypeProvider {

    @Override
    public Collection<FoldType> getValues(Class type) {
        if (type == FoldType.class) {
            return Arrays.asList(FoldType.CODE_BLOCK, FoldType.DOCUMENTATION);
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public boolean inheritable() {
        return false;
    }
}
