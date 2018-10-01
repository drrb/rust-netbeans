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
package com.github.drrb.rust.netbeans.test;

import com.github.drrb.rust.netbeans.parsing.antlr.RustElementKind;
import com.github.drrb.rust.netbeans.parsing.antlr.RustStructureItem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import org.netbeans.modules.csl.api.StructureItem;

/**
 *
 * @author Tim Boudreau
 */
public class Structure implements Comparable<Structure> {

    private final List<Structure> items = new ArrayList<>();
    private final String name;
    private final RustElementKind kind;

    Structure() {
        this.name = null;
        this.kind = null;
    }

    Structure(String name, RustElementKind kind) {
        this.name = name;
        this.kind = kind;
    }

    Structure(String line) {
        String[] parts = line.trim().split("\\s");
        assert parts.length == 2 : "Bad line: '" + line + "'";
        name = parts[0];
        kind = RustElementKind.valueOf(parts[1]);
    }

    Structure(List<? extends StructureItem> l) {
        this(null, null, l);
    }

    private Structure(String name, RustElementKind kind, List<? extends StructureItem> l) {
        this.name = name;
        this.kind = kind;
        for (StructureItem item : l) {
            assert item instanceof RustStructureItem : "Not a RustStructureItem: " + item.getClass().getName();
            RustStructureItem rs = (RustStructureItem) item;
            Structure child = new Structure(rs);
            add(child);
        }
    }

    Structure(RustStructureItem rsi) {
        this(rsi.getName(), rsi.rustKind(), rsi.getNestedItems());
    }

    public static Structure fromString(String s) {
        Structure root = new Structure();
        LinkedList<Structure> stack = new LinkedList<>();
        stack.push(root);
        String[] lines = s.split("\n");
        int lastIndent = -1;
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            int indent = indention(line);
            Structure nue = new Structure(line);
            if (i == 0 || indent == lastIndent) {
                if (i != 0) {
                    Structure t = stack.pop();
                }
                stack.peek().add(nue);
            } else if (indent > lastIndent) {
                stack.peek().add(nue);
            } else if (indent < lastIndent) {
                stack.pop();
                stack.pop();
                stack.peek().add(nue);
            }
            stack.push(nue);
            lastIndent = indent;
        }
        return root;
    }

    public String name() {
        return name;
    }

    public RustElementKind kind() {
        return kind;
    }

    Structure add(Structure next) {
        items.add(next);
        return this;
    }

    private static int indention(String line) {
        int ct = 0;
        for (int i = 0; i < line.length(); i++) {
            if (Character.isWhitespace(line.charAt(i))) {
                ct++;
            } else {
                break;
            }
        }
        return ct;
    }

    @Override
    public String toString() {
        return toString(0, new StringBuilder()).toString();
    }

    private StringBuilder toString(int depth, StringBuilder sb) {
        if (name == null) {
            for (Structure item : items) {
                item.toString(depth, sb);
            }
        } else {
            char[] ch = new char[depth];
            Arrays.fill(ch, ' ');
            sb.append(ch);
            sb.append(name).append(' ').append(kind.name()).append('\n');
            for (Structure item : items) {
                item.toString(depth + 1, sb);
            }
        }
        return sb;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.items);
        hash = 97 * hash + Objects.hashCode(this.name);
        hash = 97 * hash + Objects.hashCode(this.kind);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Structure other = (Structure) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.items, other.items)) {
            return false;
        }
        if (this.kind != other.kind) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(Structure o) {
        if (name == null && o.name == null) {
            return 0;
        }
        if (name == null) {
            return -1;
        }
        if (o.name == null) {
            return 1;
        }
        return name.compareTo(o.name);
    }

    public static StructureBuilder builder() {
        return new StructureBuilder();
    }

    public static final class StructureBuilder {

        private final Structure root = new Structure();
        private LinkedList<Structure> stack = new LinkedList<>();

        private StructureBuilder() {
            stack.push(root);
        }

        public StructureBuilder push(String name, RustElementKind kind) {
            Structure curr = stack.peek();
            Structure nue = new Structure(name, kind);
            curr.add(nue);
            stack.push(nue);
            return this;
        }

        public StructureBuilder add(String name, RustElementKind kind) {
            Structure curr = stack.peek();
            curr.add(new Structure(name, kind));
            return this;
        }

        public StructureBuilder pop() {
            stack.pop();
            if (stack.isEmpty()) {
                throw new IllegalStateException("pop() called asymmetrically with push()");
            }
            return this;
        }

        public Structure build() {
            return root;
        }
    }
}
