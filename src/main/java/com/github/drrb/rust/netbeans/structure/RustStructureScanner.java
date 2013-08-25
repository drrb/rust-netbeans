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

import com.github.drrb.rust.netbeans.parsing.NetbeansRustParser.NetbeansRustParserResult;
import com.github.drrb.rust.netbeans.parsing.index.RustDocComment;
import com.github.drrb.rust.netbeans.parsing.index.RustFunction;
import com.github.drrb.rust.netbeans.parsing.index.RustSourceIndex;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.antlr.v4.runtime.misc.MultiMap;
import org.netbeans.modules.csl.api.ElementKind;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.api.StructureItem;
import org.netbeans.modules.csl.api.StructureScanner;
import org.netbeans.modules.csl.spi.ParserResult;

/**
 *
 */
public class RustStructureScanner implements StructureScanner {

    @Override
    public List<? extends StructureItem> scan(ParserResult info) {
        List<StructureItem> structureItems = new LinkedList<StructureItem>();

        NetbeansRustParserResult parseResult = (NetbeansRustParserResult) info;
        RustSourceIndex index = parseResult.getIndex();

        List<RustFunction> functions = index.getFunctions();
        for (RustFunction function : functions) {
            structureItems.add(new RustStructureItem(function.getName(), ElementKind.METHOD, function.getOffsetRange()));
        }

        return structureItems;
    }

    @Override
    public Map<String, List<OffsetRange>> folds(ParserResult info) {
        MultiMap<String, OffsetRange> folds = new MultiMap<String, OffsetRange>();

        NetbeansRustParserResult parseResult = (NetbeansRustParserResult) info;
        RustSourceIndex index = parseResult.getIndex();

        List<RustFunction> functions = index.getFunctions();
        for (RustFunction function : functions) {
            folds.map("codeblocks", function.getBody().getOffsetRange());
        }

        List<RustDocComment> rustdocs = index.getDocComments();
        for (RustDocComment docComment : rustdocs) {
            if (docComment.getText().contains("\n")) { //Don't fold single line comments
                folds.map("comments", docComment.getOffsetRange());
            }
        }

        return folds;
    }

    @Override
    public Configuration getConfiguration() {
        return new Configuration(false, false);
    }
}
