/**
 * Copyright (C) 2013 drrb
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
package com.github.drrb.rust.netbeans.refactor;

import com.github.drrb.rust.netbeans.highlighting.RustOccurrencesFinder;
import com.github.drrb.rust.netbeans.parsing.NetbeansRustParser.NetbeansRustParserResult;
import com.github.drrb.rust.netbeans.parsing.OffsetRustToken;
import com.github.drrb.rust.netbeans.parsing.RustLexUtils;
import com.github.drrb.rust.netbeans.util.Option;
import java.util.Map;
import java.util.Set;
import org.netbeans.modules.csl.api.ColoringAttributes;
import org.netbeans.modules.csl.api.InstantRenamer;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.spi.ParserResult;

/**
 *
 */
public class RustInstantRenamer implements InstantRenamer {

    public static final String CANNOT_RENAME_MESSAGE = "Cannot perform instant rename here";

    @Override
    public boolean isRenameAllowed(ParserResult info, int caretOffset, String[] explanationRetValue) {
        Option<OffsetRustToken> identifierAtCaret = RustLexUtils.getIdentifierAt(caretOffset, info);
        if (identifierAtCaret.is()) {
            return true;
        } else {
            explanationRetValue[0] = CANNOT_RENAME_MESSAGE;
            return false;
        }
    }

    @Override
    public Set<OffsetRange> getRenameRegions(ParserResult info, int caretOffset) {
        RustOccurrencesFinder occurrencesFinder = new RustOccurrencesFinder();
        occurrencesFinder.setCaretPosition(caretOffset);
        occurrencesFinder.run((NetbeansRustParserResult) info, null);
        Map<OffsetRange, ColoringAttributes> occurrences = occurrencesFinder.getOccurrences();
        return occurrences.keySet();
    }
}
