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
import static com.github.drrb.rust.netbeans.parsing.antlr.RustElementKind.ENUM;
import static com.github.drrb.rust.netbeans.parsing.antlr.RustElementKind.FIELD;
import static com.github.drrb.rust.netbeans.parsing.antlr.RustElementKind.FUNCTION;
import static com.github.drrb.rust.netbeans.parsing.antlr.RustElementKind.TRAIT;
import static com.github.drrb.rust.netbeans.parsing.antlr.RustElementKind.TYPE;
import com.github.drrb.rust.netbeans.parsing.antlr.RustStructureItem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.swing.ImageIcon;
import static junit.framework.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.netbeans.modules.csl.api.ElementHandle;
import org.netbeans.modules.csl.api.ElementKind;
import org.netbeans.modules.csl.api.HtmlFormatter;
import org.netbeans.modules.csl.api.Modifier;
import org.netbeans.modules.csl.api.OffsetRange;

/**
 *
 * @author Tim Boudreau
 */
public class StructureTest {

    Structure struct1;

    @Before
    public void setup() {
        struct1 = new Structure();
        Structure child1 = new Structure("Foo", ENUM);
        struct1.add(child1);
        child1.add(new Structure("ONE", FIELD));
        child1.add(new Structure("TWO", FIELD));
        child1.add(new Structure("THREE", FIELD));
        Structure child2 = new Structure("Bar", TRAIT);
        struct1.add(child2);
        child2.add(new Structure("doSomething", FUNCTION));
        Structure child3 = new Structure("Quux", TYPE);
        child2.add(child3);
        child3.add(new Structure("whunk", FIELD));
    }

    @Test
    public void testStructureFromStructureItems() {
        FakeStructureItem first = new FakeStructureItem("Foo", ENUM);
        first.add("ONE", FIELD);
        first.add("TWO", FIELD);
        first.add("THREE", FIELD);
        FakeStructureItem second = new FakeStructureItem("Bar", TRAIT);
        second.add("doSomething", FUNCTION);
        FakeStructureItem child3 = second.add("Quux", TYPE);
        child3.add("whunk", FIELD);
        Structure fromItems = new Structure(Arrays.asList(first, second));
        assertEquals(struct1, fromItems);
    }

    @Test
    public void testStructureBuilder() {
        Structure structure =
                Structure.builder().push("Foo", ENUM)
                .add("ONE", FIELD)
                .add("TWO", FIELD)
                .add("THREE", FIELD)
                .pop()
                .push("Bar", TRAIT)
                .add("doSomething", FUNCTION)
                .push("Quux", TYPE)
                .add("whunk", FIELD)
                .build();
        assertEquals(struct1, structure);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalid() {
        Structure.fromString("foo WOOKIE");
    }

    @Test
    public void testToString() {
        String expect = "Foo ENUM\n ONE FIELD\n TWO FIELD\n THREE FIELD\nBar TRAIT\n doSomething FUNCTION\n"
                + " Quux TYPE\n  whunk FIELD\n";
        assertEquals(expect, struct1.toString());
    }
    
    @Test
    public void testStringConstructor() {
        Structure st = new Structure("  foo TRAIT");
        assertEquals("foo", st.name());
        assertEquals(TRAIT, st.kind());
    }

    @Test
    public void testStructureSerialization() {
        Structure reconstituted = Structure.fromString(struct1.toString());
        assertEquals(struct1, reconstituted);
        assertEquals(struct1.hashCode(), reconstituted.hashCode());
        assertEquals(struct1.toString(), reconstituted.toString());
    }

    private static final class FakeStructureItem implements RustStructureItem {

        private final String name;
        private final RustElementKind kind;
        private final List<FakeStructureItem> kids = new ArrayList<>();

        FakeStructureItem(String name, RustElementKind kind) {
            this.name = name;
            this.kind = kind;
        }

        FakeStructureItem add(String name, RustElementKind kind) {
            FakeStructureItem result = new FakeStructureItem(name, kind);
            kids.add(result);
            return result;
        }

        @Override
        public RustElementKind rustKind() {
            return kind;
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
        public String getHtml(HtmlFormatter hf) {
            return name;
        }

        @Override
        public ElementHandle getElementHandle() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public ElementKind getKind() {
            return ElementKind.TEST;
        }

        @Override
        public Set<Modifier> getModifiers() {
            return Collections.emptySet();
        }

        @Override
        public boolean isLeaf() {
            return kids.isEmpty();
        }

        @Override
        public List<? extends RustStructureItem> getNestedItems() {
            return kids;
        }

        @Override
        public long getPosition() {
            return 1;
        }

        @Override
        public long getEndPosition() {
            return 2;
        }

        @Override
        public ImageIcon getCustomIcon() {
            return null;
        }

        @Override
        public OffsetRange range() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }
}
