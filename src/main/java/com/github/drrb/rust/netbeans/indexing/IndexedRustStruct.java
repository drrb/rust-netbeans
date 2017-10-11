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
package com.github.drrb.rust.netbeans.indexing;

import com.github.drrb.rust.netbeans.parsing.index.RustStruct;
import org.netbeans.modules.csl.api.OffsetRange;
import org.openide.filesystems.FileObject;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

/**
 *
 */
public class IndexedRustStruct {

    protected static final String INDEX_KEY_STRUCT_NAME = "struct-name";
    protected static final String INDEX_KEY_STRUCT_NAME_LOWERCASE = "struct-name-lowercase";
    @IndexedString(INDEX_KEY_STRUCT_NAME)
    protected String name;
    @IndexedString(INDEX_KEY_STRUCT_NAME_LOWERCASE)
    protected String lowercaseName;
    @IndexedString("struct-module")
    protected String module;
    @IndexedString("struct-offset-start")
    protected int offsetStart;
    @IndexedString("struct-offset-end")
    protected int offsetEnd;
    @IndexedFile
    protected FileObject file;

    public IndexedRustStruct() {
    }

    IndexedRustStruct(RustStruct struct) {
        name = struct.getName();
        lowercaseName = name.toLowerCase();
        module = "xxx";
        offsetStart = struct.getOffsetRange().getStart();
        offsetEnd = struct.getOffsetRange().getEnd();
    }

    public String getName() {
        return name;
    }

    public String getModule() {
        return module;
    }

    public OffsetRange getOffsetRange() {
        return new OffsetRange(offsetStart, offsetEnd);
    }

    public FileObject getFile() {
        return file;
    }

    @Override
    public int hashCode() {
        return reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object other) {
        return reflectionEquals(this, other);
    }

    @Override
    public String toString() {
        return reflectionToString(this, MULTI_LINE_STYLE);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private RustStruct struct;
        private FileObject file;

        public Builder withStruct(RustStruct struct) {
            this.struct = struct;
            return this;
        }

        public Builder withFile(FileObject file) {
            this.file = file;
            return this;
        }

        public IndexedRustStruct build() {
            IndexedRustStruct indexedRustStruct = new IndexedRustStruct(struct);
            indexedRustStruct.file = file;
            return indexedRustStruct;
        }
    }
}
