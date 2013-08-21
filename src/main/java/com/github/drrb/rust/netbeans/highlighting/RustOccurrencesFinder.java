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
package com.github.drrb.rust.netbeans.highlighting;

import com.github.drrb.rust.netbeans.parsing.NetbeansRustParser.NetbeansRustParserResult;
import com.github.drrb.rust.netbeans.parsing.RustBaseVisitor;
import com.github.drrb.rust.netbeans.parsing.RustLexUtils;
import com.github.drrb.rust.netbeans.parsing.RustParser;
import com.github.drrb.rust.netbeans.parsing.RustTokenId;
import com.github.drrb.rust.netbeans.util.Option;
import java.util.HashMap;
import java.util.Map;
import org.antlr.v4.runtime.ParserRuleContext;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.modules.csl.api.ColoringAttributes;
import static org.netbeans.modules.csl.api.ColoringAttributes.*;
import org.netbeans.modules.csl.api.OccurrencesFinder;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.parsing.spi.Scheduler;
import org.netbeans.modules.parsing.spi.SchedulerEvent;

/**
 *
 */
public class RustOccurrencesFinder extends OccurrencesFinder {

    private int caretPosition;
    private final Map<OffsetRange, ColoringAttributes> occurrences = new HashMap<OffsetRange, ColoringAttributes>();

    @Override
    public void setCaretPosition(int caretPosition) {
        this.caretPosition = caretPosition;
    }

    @Override
    public void run(Parser.Result result, SchedulerEvent event) {
        occurrences.clear();

        NetbeansRustParserResult parseResult = (NetbeansRustParserResult) result;
        TokenHierarchy<?> tokenHierarchy = result.getSnapshot().getTokenHierarchy();
        final Option<Token<RustTokenId>> tokenAtCaret = RustLexUtils.getIdentifierAt(caretPosition, tokenHierarchy);
        if (tokenAtCaret.is()) {
            addOccurrence(getRange(tokenAtCaret.value(), tokenHierarchy), LOCAL_VARIABLE);
            RustParser.ProgContext prog = parseResult.getAst();
            prog.accept(new RustBaseVisitor<Void>() {
                @Override
                public Void visitFun_body(RustParser.Fun_bodyContext ctx) {
                    if (getRange(ctx).containsInclusive(caretPosition)) {
                        ctx.accept(new RustBaseVisitor<Void>() {
                            @Override
                            public Void visitIdent(RustParser.IdentContext ctx) {
                                if (tokenAtCaret.value().text().toString().equals(ctx.getText())) {
                                    addOccurrence(getRange(ctx), LOCAL_VARIABLE);
                                }
                                return null;
                            }
                        });
                    }
                    return null;
                }
            });
        }
    }

    @Override
    public Map<OffsetRange, ColoringAttributes> getOccurrences() {
        return new HashMap<OffsetRange, ColoringAttributes>(occurrences);
    }

    @Override
    public int getPriority() {
        return 20; //Arbitrarily copied from CSS module
    }

    @Override
    public Class<? extends Scheduler> getSchedulerClass() {
        return null; //Copied from CSS module
    }

    @Override
    public void cancel() {
        //TODO: do something here
    }

    private void addOccurrence(OffsetRange range, ColoringAttributes type) {
        this.occurrences.put(range, type);
    }

    private OffsetRange getRange(ParserRuleContext identifier) {
        return new OffsetRange(identifier.getStart().getStartIndex(), identifier.getStop().getStopIndex() + 1);
    }

    private OffsetRange getRange(Token<RustTokenId> offsetToken, TokenHierarchy<?> tokenHierarchy) {
        int tokenOffset = offsetToken.offset(tokenHierarchy);
        return new OffsetRange(tokenOffset, tokenOffset + offsetToken.length());
    }
}
