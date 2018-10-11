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
package com.github.drrb.rust.netbeans.structure;

import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.swing.ImageIcon;
import org.netbeans.modules.csl.api.ElementHandle;
import org.netbeans.modules.csl.api.ElementKind;
import org.netbeans.modules.csl.api.HtmlFormatter;
import org.netbeans.modules.csl.api.Modifier;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.api.StructureItem;

/**
 *
 */
public class RustStructureItem implements StructureItem.CollapsedDefault {

    private final String name;
    private final OffsetRange offsetRange;
    private final ElementKind kind;
    private final Set<Modifier> modifiers;
    private final List<RustStructureItem> nestedItems = new LinkedList<>();

    public RustStructureItem(String name, OffsetRange offsetRange, ElementKind kind) {
        this.name = name;
        this.kind = kind;
        this.offsetRange = offsetRange;
        this.modifiers = Collections.emptySet();
    }

    public RustStructureItem(String name, OffsetRange offsetRange, ElementKind kind, Set<Modifier> modifiers) {
        this.name = name;
        this.kind = kind;
        this.offsetRange = offsetRange;
        this.modifiers = EnumSet.copyOf(modifiers);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getSortText() {
        return name;
    }

    @Override
    public String getHtml(HtmlFormatter formatter) {
        return name;
    }

    @Override
    public ElementHandle getElementHandle() {
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
    public boolean isLeaf() {
        return getNestedItems().isEmpty();
    }

    @Override
    public List<? extends StructureItem> getNestedItems() {
        return Collections.unmodifiableList(nestedItems);
    }

    @Override
    public long getPosition() {
        return offsetRange.getStart();
    }

    @Override
    public long getEndPosition() {
        return offsetRange.getEnd();
    }

    @Override
    public ImageIcon getCustomIcon() {
        return null;
    }

    @Override
    public boolean isCollapsedByDefault() {
        return false;
    }

    @Override
    public String toString() {
        return "RustStructureItem{" + "name=" + name + ", offsetRange=" + offsetRange + ", kind=" + kind + ", modifiers=" + modifiers + '}';
    }

    void addNestedItem(RustStructureItem nestedItem) {
        nestedItems.add(nestedItem);
    }
}
