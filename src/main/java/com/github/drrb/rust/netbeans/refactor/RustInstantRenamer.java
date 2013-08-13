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
package com.github.drrb.rust.netbeans.refactor;

import com.github.drrb.rust.netbeans.highlighting.RustOccurrencesFinder;
import com.github.drrb.rust.netbeans.parsing.RustTokenId;
import java.util.Map;
import java.util.Set;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.csl.api.ColoringAttributes;
import org.netbeans.modules.csl.api.InstantRenamer;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.spi.ParserResult;

/**
 *
 */
public class RustInstantRenamer implements InstantRenamer {

    @Override
    public boolean isRenameAllowed(ParserResult info, int caretOffset, String[] explanationRetValue) {
        TokenHierarchy<?> tokenHierarchy = info.getSnapshot().getTokenHierarchy();
        TokenSequence<RustTokenId> tokenSequence = tokenHierarchy.tokenSequence(RustTokenId.getLanguage());
        tokenSequence.move(caretOffset);
        if (tokenSequence.moveNext()) {
            return tokenSequence.token().id() == RustTokenId.IDENT;
        } else {
            return false;
        }
    }

    @Override
    public Set<OffsetRange> getRenameRegions(ParserResult info, int caretOffset) {
        RustOccurrencesFinder occurrencesFinder = new RustOccurrencesFinder();
        occurrencesFinder.setCaretPosition(caretOffset);
        occurrencesFinder.run(info, null);
        Map<OffsetRange, ColoringAttributes> occurrences = occurrencesFinder.getOccurrences();
        return occurrences.keySet();
    }
}
