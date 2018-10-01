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

import com.github.drrb.rust.netbeans.parsing.antlr.RustAntlrParserResult;
import static com.github.drrb.rust.netbeans.parsing.antlr.RustFoldTypeProvider.BLOCKS;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.api.StructureItem;
import org.netbeans.modules.csl.api.StructureScanner;
import org.netbeans.modules.csl.spi.ParserResult;

/**
 *
 * @author Tim Boudreau
 */
public class RustAntlrStructureScanner implements StructureScanner {

    @Override
    public List<? extends StructureItem> scan(ParserResult pr) {
        return scan((RustAntlrParserResult) pr);
    }

    @Override
    public Map<String, List<OffsetRange>> folds(ParserResult pr) {
        return folds((RustAntlrParserResult) pr);
    }

    @Override
    public Configuration getConfiguration() {
        return new Configuration( true, true, 4 );
    }

    private Map<String, List<OffsetRange>> folds(RustAntlrParserResult pr) {
        // Need to return a mutable list here for sorting
        return Collections.singletonMap(BLOCKS, new ArrayList<>(pr.blocks()));
    }

    private List<? extends StructureItem> scan(RustAntlrParserResult pr) {
        // Need to return a mutable list here for sorting
        return new ArrayList<>(pr.structureItems());
    }
}
