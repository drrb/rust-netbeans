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
package com.github.drrb.rust.netbeans.completion;

import com.github.drrb.rust.netbeans.RustLanguage;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import org.netbeans.modules.csl.api.ElementHandle;
import org.netbeans.modules.csl.api.ElementKind;
import org.netbeans.modules.csl.api.Modifier;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.spi.ParserResult;
import org.openide.filesystems.FileObject;

/**
 *
 */
public class RustElementHandle implements ElementHandle {
    private final String name;
    private final OffsetRange offsetRange;
    private final ElementKind kind;
    private final Set<Modifier> modifiers;

    public RustElementHandle(String name, OffsetRange offsetRange, ElementKind kind) {
        this.name = name;
        this.offsetRange = offsetRange;
        this.kind = kind;
        this.modifiers = Collections.emptySet();
    }

    public RustElementHandle(String name, OffsetRange offsetRange, ElementKind kind, Set<Modifier> modifiers) {
        this.name = name;
        this.offsetRange = offsetRange;
        this.kind = kind;
        this.modifiers = EnumSet.copyOf(modifiers);
    }

    @Override
    public FileObject getFileObject() {
        return null;
    }

    @Override
    public String getMimeType() {
        return RustLanguage.MIME_TYPE;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getIn() {
        return null;
    }

    @Override
    public ElementKind getKind() {
        return kind;
    }

    @Override
    public Set<Modifier> getModifiers() {
        return Collections.unmodifiableSet(modifiers);
    }

    @Override
    public boolean signatureEquals(ElementHandle handle) {
        return name.equals(handle.getName());
    }

    @Override
    public OffsetRange getOffsetRange(ParserResult result) {
        return offsetRange;
    }
}
