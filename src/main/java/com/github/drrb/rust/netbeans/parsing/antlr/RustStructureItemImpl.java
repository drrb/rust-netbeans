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
import static com.github.drrb.rust.netbeans.parsing.antlr.AntlrUtils.toOffsetRange;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.swing.ImageIcon;
import org.antlr.v4.runtime.ParserRuleContext;
import org.netbeans.modules.csl.api.ElementHandle;
import org.netbeans.modules.csl.api.ElementKind;
import org.netbeans.modules.csl.api.HtmlFormatter;
import org.netbeans.modules.csl.api.Modifier;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.api.Snapshot;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Tim Boudreau
 */
final class RustStructureItemImpl implements ElementHandle, RustStructureItem {

    private final String name;
    final RustElementKind kind;
    List<RustStructureItemImpl> nested;
    private final FileObject file;
    private final OffsetRange range;
    private String in = "";

    RustStructureItemImpl(String name, RustElementKind kind, Snapshot snapshot, ParserRuleContext ctx) {
        this.name = name;
        this.kind = kind;
        this.file = snapshot.getSource().getFileObject();
        this.range = toOffsetRange(ctx);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(" ").append(kind).append(" @").append(range.getStart()).append(":").append(range.getEnd());
        if (!in.isEmpty()) {
            sb.append(" in " + in);
        }
        if (nested != null && !nested.isEmpty()) {
            sb.append(" children: [");
            for (Iterator<RustStructureItemImpl> it = nested.iterator(); it.hasNext();) {
                sb.append(it.next());
                if (it.hasNext()) {
                    sb.append(",");
                }
            }
            sb.append("]");
        }
        return sb.toString();
    }

    List<RustStructureItemImpl> nested() {
        if (nested == null) {
            nested = new ArrayList<>(5);
        }
        return nested;
    }

    String qName() {
        if (!in.isEmpty()) {
            return in + "." + name;
        } else {
            return name;
        }
    }

    void setIn(String in) {
        this.in = in;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getSortText() {
        return getName();
    }

    @Override
    public String getHtml(HtmlFormatter hf) {
        return getName();
    }

    @Override
    public ElementHandle getElementHandle() {
        return this;
    }

    public RustElementKind rustKind() {
        return kind;
    }

    @Override
    public ElementKind getKind() {
        return kind.toElementKind();
    }

    @Override
    public Set<Modifier> getModifiers() {
        return Collections.emptySet();
    }

    @Override
    public boolean isLeaf() {
        return nested == null;
    }

    @Override
    public List<? extends RustStructureItem> getNestedItems() {
        if (nested == null) {
            return Collections.emptyList();
        }
        return nested;
    }

    @Override
    public long getPosition() {
        return range.getStart();
    }

    @Override
    public long getEndPosition() {
        return range.getEnd();
    }

    public OffsetRange range() {
        return range;
    }

    @Override
    public ImageIcon getCustomIcon() {
        return null;
    }

    @Override
    public FileObject getFileObject() {
        return this.file;
    }

    @Override
    public String getMimeType() {
        return RustLanguage.MIME_TYPE;
    }

    @Override
    public String getIn() {
        return in;
    }

    @Override
    public boolean signatureEquals(ElementHandle eh) {
        return getName().equals(eh.getName()) && Objects.equals(eh.getIn(), getIn());
    }

    @Override
    public OffsetRange getOffsetRange(ParserResult pr) {
        return range;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o == null) {
            return false;
        } else if (o instanceof RustStructureItemImpl) {
            RustStructureItemImpl other = (RustStructureItemImpl) o;
            return file.equals(other.file) && qName().equals(other.qName()) && range.getStart() == other.range.getStart() && range.getEnd() == other.range.getEnd();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(kind, name, file, range.getStart(), range.getEnd());
    }

}
