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
import org.openide.filesystems.FileUtil;

/**
 *
 */
public class RustElementHandle implements ElementHandle {

    private final String name;
    private final OffsetRange offsetRange;
    private final RustElementDocumentation documentation;
    private final ElementKind kind;
    private final Set<Modifier> modifiers;
    private final FileObject fileObject;

    public RustElementHandle(String name, OffsetRange offsetRange, RustElementDocumentation documentation, ElementKind kind, Set<Modifier> modifiers, FileObject fileObject) {
        this.name = name;
        this.offsetRange = offsetRange;
        this.documentation = documentation;
        this.kind = kind;
        this.modifiers = modifiers == null || modifiers.isEmpty() ? Collections.<Modifier>emptySet() : EnumSet.copyOf(modifiers);
        this.fileObject = fileObject;
    }

    @Override
    public FileObject getFileObject() {
        return fileObject;
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
        return fileObject.getNameExt();
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

    public String getDocumentationHtml() {
        return documentation.getHtml("No documentation found for " + getName());
    }

    public static Builder with(String name, OffsetRange offsetRange, ElementKind kind) {
        return new Builder(name, offsetRange, kind);
    }

    public static class Builder {

        private final String name;
        private final OffsetRange offsetRange;
        private final ElementKind kind;
        private final Set<Modifier> modifiers = EnumSet.noneOf(Modifier.class);
        private RustElementDocumentation documentation = RustElementDocumentation.NONE;
        private FileObject fileObject;

        private Builder(String name, OffsetRange offsetRange, ElementKind kind) {
            this.name = name;
            this.offsetRange = offsetRange;
            this.kind = kind;
        }

        public Builder withDocumentation(RustElementDocumentation documentation) {
            this.documentation = documentation;
            return this;
        }

        public Builder withModifiers(Set<Modifier> modifiers) {
            this.modifiers.addAll(modifiers);
            return this;
        }

        public Builder withModifier(Modifier modifier) {
            this.modifiers.add(modifier);
            return this;
        }

        public Builder withFileObject(FileObject fileObject) {
            this.fileObject = fileObject;
            return this;
        }

        public RustElementHandle build() {
            return new RustElementHandle(name, offsetRange, documentation, kind, modifiers, fileObject);
        }
    }
}
