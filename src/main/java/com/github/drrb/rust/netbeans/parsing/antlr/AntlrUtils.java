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

import java.util.Arrays;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.netbeans.modules.csl.api.OffsetRange;

/**
 *
 * @author Tim Boudreau
 */
public final class AntlrUtils {

    private AntlrUtils() {
        throw new AssertionError();
    }

    public static OffsetRange toOffsetRange(ParserRuleContext ctx) {
        return new OffsetRange(
                ctx.getStart().getStartIndex(),
                ctx.getStop().getStopIndex() + 1);
    }

    public static void print(ParserRuleContext ctx) {
        StringBuilder sb = new StringBuilder("\n*******************************\n")
                .append(ctx.getText()).append('\n');
        unwind(ctx, sb);
        System.out.println(sb.toString());
    }

    public static String stringify(ParserRuleContext ctx) {
        StringBuilder sb = new StringBuilder();
        unwind(ctx, sb);
        return sb.toString();
    }

    private static void unwind(ParserRuleContext ctx, StringBuilder sb) {
        unwind(ctx, 0, sb);
    }

    private static void unwind(ParseTree ctx, int depth, StringBuilder sb) {
        char[] ind = new char[depth * 2];
        Arrays.fill(ind, ' ');
        sb.append(ind);
        sb.append(ctx.getClass().getSimpleName()).append(" - ").append(ctx.getText());
        if (ctx instanceof ParserRuleContext) {
            ParserRuleContext rule = (ParserRuleContext) ctx;
            if (rule.children == null || rule.children.isEmpty()) {
                sb.append(" with no children\n");
            } else {
                if (sb.length() > 0 && sb.charAt(sb.length() - 1) != '\n') {
                    sb.append('\n');
                }
                for (ParseTree c : rule.children) {
                    unwind(c, depth + 1, sb);
                    if (sb.length() > 0 && sb.charAt(sb.length() - 1) != '\n') {
                        sb.append('\n');
                    }
                }
            }
        }
    }

}
