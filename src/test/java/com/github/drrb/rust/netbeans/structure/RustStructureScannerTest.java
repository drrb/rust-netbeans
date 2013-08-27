/**
 * Copyright (C) 2013 drrb
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
package com.github.drrb.rust.netbeans.structure;

import com.github.drrb.rust.netbeans.RustSource;
import com.github.drrb.rust.netbeans.parsing.NetbeansRustParser.NetbeansRustParserResult;
import static com.github.drrb.rust.netbeans.parsing.RustLexUtils.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.netbeans.modules.csl.api.OffsetRange;
import static com.github.drrb.rust.netbeans.test.Matchers.*;
import static org.netbeans.modules.csl.api.ElementKind.*;
import static org.netbeans.modules.csl.api.Modifier.*;
import org.netbeans.modules.csl.api.StructureItem;

/**
 *
 */
public class RustStructureScannerTest {

    private RustStructureScanner structureScanner;

    @Before
    public void setUp() {
        structureScanner = new RustStructureScanner();
    }

    @Test
    public void shouldFoldFunctions() {
        RustSource source = new RustSource();
        source.appendln("/// Entry point");
        source.appendln("fn main () {");
        source.appendln("   let a = 1;");
        source.appendln("}");
        source.appendln("");
        source.appendln("fn other() {");
        source.appendln("}");
        NetbeansRustParserResult parseResult = source.parse();
        Map<String, List<OffsetRange>> folds = structureScanner.folds(parseResult);

        assertThat(folds, containsKey("codeblocks").mappedToValue(listOf(range(27, 44), range(57, 60))));
    }

    @Test
    public void shouldFoldStructs() {
        RustSource source = new RustSource();
        source.appendln("/// Point in space");
        source.appendln("struct Point {");
        source.appendln("   x: float,");
        source.appendln("   y: float");
        source.appendln("}");

        NetbeansRustParserResult parseResult = source.parse();
        Map<String, List<OffsetRange>> folds = structureScanner.folds(parseResult);

        assertThat(folds, containsKey("codeblocks").mappedToValue(listOf(range(32, 60))));
    }

    @Test
    public void shouldFoldImpls() {
        RustSource source = new RustSource();
        source.appendln("");
        source.appendln("impl Point {");
        source.appendln("   fn transpose(&self, dx: float, dy: float) {");
        source.appendln("       Point { x: self.x + dx, y: self.y + dy }");
        source.appendln("   }");
        source.appendln("}");
        source.appendln("");
        source.appendln("");

        NetbeansRustParserResult parseResult = source.parse();
        Map<String, List<OffsetRange>> folds = structureScanner.folds(parseResult);

        assertThat(folds.get("codeblocks"), contains(range(12, 115)));
    }

    @Test
    public void shouldFoldTraits() {
        RustSource source = new RustSource();
        source.appendln("/// A thing that can be printed");
        source.appendln("trait Printable {");
        source.appendln("   fn print(&self);");
        source.appendln("}");
        source.appendln("");
        source.appendln("");
        NetbeansRustParserResult parseResult = source.parse();
        Map<String, List<OffsetRange>> folds = structureScanner.folds(parseResult);

        assertThat(folds, containsKey("codeblocks").mappedToValue(listOf(range(48, 71))));
    }

    @Test
    public void shouldFoldTraitImpls() {
        RustSource source = new RustSource();
        source.appendln("");
        source.appendln("impl Printable for Point {");
        source.appendln("   fn print(&self) {");
        source.appendln("       println(fmt!(\"%?, %?\"));");
        source.appendln("   }");
        source.appendln("}");
        source.appendln("");
        source.appendln("");

        NetbeansRustParserResult parseResult = source.parse();
        Map<String, List<OffsetRange>> folds = structureScanner.folds(parseResult);

        assertThat(folds.get("codeblocks"), contains(range(26, 87)));
    }

    @Test
    public void shouldFoldEnums() {
        RustSource source = new RustSource();
        source.appendln("/// A shape");
        source.appendln("enum Shape {");
        source.appendln("   Circle(Point, float),");
        source.appendln("   Rectangle(Point, Point)");
        source.appendln("}");
        source.appendln("");
        source.appendln("");

        NetbeansRustParserResult parseResult = source.parse();
        Map<String, List<OffsetRange>> folds = structureScanner.folds(parseResult);

        assertThat(folds, containsKey("codeblocks").mappedToValue(listOf(range(23, 78))));
    }

    @Test
    public void shouldFoldImplMethods() {
        RustSource source = new RustSource();
        source.appendln("/// Point methods");
        source.appendln("impl Point {");
        source.appendln();
        source.appendln("    fn transpose(&self, dx: float, dy: float) -> Point {");
        source.appendln("        Point{ x: self.x + dx, y: self.y + dy}");
        source.appendln("    }");
        source.appendln("}");
        source.appendln();

        NetbeansRustParserResult parseResult = source.parse();
        Map<String, List<OffsetRange>> folds = structureScanner.folds(parseResult);

        assertThat(folds.get("codeblocks"), contains(range(87, 141)));
    }

    @Test
    public void shouldFoldTraitImplMethods() {
        RustSource source = new RustSource();
        source.appendln();
        source.appendln("impl Printable for Point {");
        source.appendln("   fn print(&self) {");
        source.appendln("       println(fmt!(\"%?, %?\"));");
        source.appendln("   }");
        source.appendln("}");
        source.appendln();

        NetbeansRustParserResult parseResult = source.parse();
        Map<String, List<OffsetRange>> folds = structureScanner.folds(parseResult);

        assertThat(folds.get("codeblocks"), contains(range(47, 85)));
    }

    @Test
    public void shouldFoldMultilineDocComments() {
        RustSource source = new RustSource();
        source.appendln("/**");
        source.appendln(" * Entry point");
        source.appendln(" */");
        source.appendln("fn main () {");
        source.appendln("   let a = 1;");
        source.appendln("}");
        source.appendln("");
        source.appendln("/// Comment that shouldn't be foldable because it's just one line");
        source.appendln("fn other() {");
        source.appendln("}");
        NetbeansRustParserResult parseResult = source.parse();
        Map<String, List<OffsetRange>> folds = structureScanner.folds(parseResult);

        assertThat(folds, containsKey("comments").mappedToValue(listOf(range(0, 22))));
    }

    @Test
    public void shouldIncludeFunctionsInNavigatorPanel() {
        RustSource source = new RustSource();
        source.appendln("/// Entry point");
        source.appendln("fn main () {");
        source.appendln("   let a = 1;");
        source.appendln("}");
        source.appendln("");
        source.appendln("fn other() {");
        source.appendln("}");
        NetbeansRustParserResult parseResult = source.parse();
        List<StructureItem> structure = (List<StructureItem>) structureScanner.scan(parseResult);

        assertThat(structure, contains(structureItem("main", range(16, 44), METHOD, STATIC)));
        assertThat(structure, contains(structureItem("other", range(46, 60), METHOD, STATIC)));
    }

    @Test
    public void shouldIncludeStructsInNavigatorPanel() {
        RustSource source = new RustSource();
        source.appendln("/// Point in space");
        source.appendln("struct Point {");
        source.appendln("   x: float,");
        source.appendln("   y: float");
        source.appendln("}");
        NetbeansRustParserResult parseResult = source.parse();
        List<StructureItem> structure = (List<StructureItem>) structureScanner.scan(parseResult);

        assertThat(structure, contains(structureItem("Point", range(19, 60), CLASS)));
    }

    @Test
    public void shouldIncludeTraitsInNavigatorPanel() {
        RustSource source = new RustSource();
        source.appendln("/// A thing that can be printed");
        source.appendln("trait Printable {");
        source.appendln("   fn print(&self);");
        source.appendln("}");
        source.appendln("");
        source.appendln("");
        NetbeansRustParserResult parseResult = source.parse();
        List<StructureItem> structure = (List<StructureItem>) structureScanner.scan(parseResult);

        assertThat(structure, contains(structureItem("Printable", range(32, 71), INTERFACE)));
    }

    private <T> List<T> listOf(T... values) {
        return Arrays.asList(values);
    }
}
