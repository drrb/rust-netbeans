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
    public void shouldFoldMethods() {
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

    private <T> List<T> listOf(T... values) {
        return Arrays.asList(values);
    }
}
