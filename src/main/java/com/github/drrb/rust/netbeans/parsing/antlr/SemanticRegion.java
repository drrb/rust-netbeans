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

import com.github.drrb.rust.netbeans.parsing.antlr.RustVisibility;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import org.netbeans.modules.csl.api.ColoringAttributes;
import org.netbeans.modules.csl.api.OffsetRange;

/**
 *
 * @author Tim Boudreau
 */
final class SemanticRegion implements RustSourceRegion {

    public final RustElementKind kind;
    public final String text;
    public final OffsetRange range;
    public final boolean mutable;
    public final Set<RustVisibility> visibility;
    public final boolean statyc;
    private final RustElementKind childOf;

    SemanticRegion(RustElementKind kind, String text, OffsetRange range, boolean mutable, Set<RustVisibility> visibility, boolean statyc, RustElementKind childOf) {
        assert range != null : "Range is null";
        assert kind != null : "Kind is null";
        this.kind = kind;
        this.text = text;
        this.range = range;
        this.mutable = mutable;
        this.visibility = visibility;
        this.statyc = statyc;
        this.childOf = childOf;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder(kind.name());
        sb.append(" @").append(range.getStart()).append(':').append(range.getEnd());
        if (mutable) {
            sb.append(" mut");
        }
        if (statyc) {
            sb.append(" static");
        }
        if (!visibility.isEmpty()) {
            sb.append(" visibility: [");
            for (Iterator<RustVisibility> it = visibility.iterator(); it.hasNext();) {
                RustVisibility vis = it.next();
                sb.append(' ').append(vis);
            }
            sb.append(']');
        }
        if (text != null && !text.isEmpty()) {
            String txt = text.replaceAll("\t", "\\\\t").replaceAll("\n", "\\\\n");
            sb.append(" text: '").append(txt).append('\'');
        }
        if (childOf != null) {
            sb.append (" under: ").append(childOf);
        }
        return sb.toString();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o == null) {
            return false;
        } else if (o instanceof SemanticRegion) {
            SemanticRegion other = (SemanticRegion) o;
            return range.getStart() == other.range().getStart() && range.getEnd() == other.range().getEnd();
        }
        return false;
    }

    @Override
    public RustElementKind kind() {
        return kind;
    }

    @Override
    public Optional<RustElementKind> childOf() {
        return Optional.ofNullable(childOf);
    }

    @Override
    public OffsetRange range() {
        return range;
    }

    @Override
    public boolean isMutable() {
        return mutable;
    }

    @Override
    public Set<RustVisibility> visibility() {
        return visibility;
    }

    @Override
    public boolean isStatic() {
        return statyc;
    }

    @Override
    public boolean hasVisibility(RustVisibility vis) {
        return visibility != null && visibility.contains(vis);
    }

    @Override
    public Set<ColoringAttributes> attributes() {
        // Pending - differentiate things like trait methods from
        // struct/type methods using childOf and different colorings
        Set<ColoringAttributes> result = EnumSet.noneOf(ColoringAttributes.class);
        if (mutable) {
            result.add(ColoringAttributes.GLOBAL); //XXX
        }
        if (hasVisibility(RustVisibility.PUB)) {
            result.add(ColoringAttributes.PUBLIC);
        }
        if (hasVisibility(RustVisibility.CRATE)) {
            result.add(ColoringAttributes.PACKAGE_PRIVATE);
        }
        if (hasVisibility(RustVisibility.SUPER)) {
            result.add(ColoringAttributes.PROTECTED);
        }
        if (hasVisibility(RustVisibility.IN)) {
            result.add(ColoringAttributes.CUSTOM3);
        }
        if (statyc) {
            result.add(ColoringAttributes.STATIC);
        }
        switch (kind) {
            case ENUM_CONSTANT:
                result.add(ColoringAttributes.ENUM);
                break;
            case FIELD:
                result.add(ColoringAttributes.FIELD);
                break;
            case FUNCTION:
                result.add(ColoringAttributes.METHOD);
                break;
            case ENUM:
                result.add(ColoringAttributes.CLASS);
                break;
            case TYPE:
                result.add(ColoringAttributes.CLASS);
                break;
            case STRUCT:
                result.add(ColoringAttributes.CLASS);
                break;
            case TRAIT:
                result.add(ColoringAttributes.INTERFACE);
                break;
            case LIFETIME:
                result.add(ColoringAttributes.CUSTOM1);
                break;
            case TYPE_REFERENCE:
                result.add(ColoringAttributes.TYPE_PARAMETER_USE);
                break;
            case ATTR:
                result.add(ColoringAttributes.ANNOTATION_TYPE);
                break;
            case IMPL:
                result.add(ColoringAttributes.CLASS);
                break;
            default:
                throw new AssertionError(kind);
        }
        return result;
    }

}
