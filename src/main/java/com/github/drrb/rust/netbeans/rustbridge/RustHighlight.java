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
package com.github.drrb.rust.netbeans.rustbridge;

import com.sun.jna.Structure;
import java.io.File;
import static java.util.Arrays.asList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.netbeans.modules.csl.api.ColoringAttributes;

/**
 *
 */
public class RustHighlight extends Structure {

    public static class ByValue extends RustHighlight implements Structure.ByValue {
    }

    public enum Kind {
        ENUM_TYPE(ColoringAttributes.CLASS_SET),
        ENUM_VARIANT(ColoringAttributes.ENUM),
        FIELD(ColoringAttributes.FIELD_SET),
        FUNCTION(ColoringAttributes.STATIC, ColoringAttributes.METHOD),
        METHOD(ColoringAttributes.METHOD_SET),
        STRUCT(ColoringAttributes.CLASS_SET),
        TRAIT(ColoringAttributes.INTERFACE);

        private final Set<ColoringAttributes> colors;

        private Kind(Set<ColoringAttributes> colors) {
            this.colors = Collections.unmodifiableSet(colors);
        }

        private Kind(ColoringAttributes colors, ColoringAttributes... otherColors) {
            this(EnumSet.of(colors, otherColors));
        }

        public Set<ColoringAttributes> colors() {
            return colors;
        }
    }

    public String fileName;
    public int startLine;
    public int startCol;
    public int startByte;
    public int startChar;
    public int endLine;
    public int endCol;
    public int endByte;
    public int endChar;
    public int kind;

    public File getFile() {
        return new File(fileName);
    }

    public int getStartLine() {
        return startLine;
    }

    public int getStartCol() {
        return startCol;
    }

    public int getStartByte() {
        return startByte;
    }

    public int getStartChar() {
        return startChar;
    }

    public int getEndLine() {
        return endLine;
    }

    public int getEndCol() {
        return endCol;
    }

    public int getEndByte() {
        return endByte;
    }

    public int getEndChar() {
        return endChar;
    }

    public Kind getKind() {
        return Kind.values()[kind];
    }

    @Override
    protected List<String> getFieldOrder() {
        return asList("fileName", "startLine", "startCol", "startByte", "startChar", "endLine", "endCol", "endByte", "endChar", "kind");
    }

}
