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
package com.github.drrb.rust.netbeans;

import java.util.List;
import org.antlr.v4.runtime.tree.TerminalNode;

public class RustdocCollectingParseListener extends RustBaseListener {

    private final List<? super Rustdoc> rustdocs;
    private boolean inFunction;
    private TerminalNode rustdocJustPassed;

    public RustdocCollectingParseListener(List<? super Rustdoc> rustdocs) {
        this.rustdocs = rustdocs;
    }

    @Override
    public void visitTerminal(TerminalNode node) {
        if (node.getSymbol().getType() == RustParser.OUTER_DOC_COMMENT) {
            rustdocJustPassed = node;
        }
    }

    @Override
    public void enterItem_fn_decl(RustParser.Item_fn_declContext context) {
        inFunction = true;
    }

    @Override
    public void exitItem_fn_decl(RustParser.Item_fn_declContext context) {
        inFunction = false;
        rustdocJustPassed = null;
    }

    @Override
    public void enterIdent(RustParser.IdentContext context) {
        if (rustdocJustPassed != null && inFunction) {
            rustdocs.add(new Rustdoc(context.getStart().getText(), rustdocJustPassed.getText()));
            rustdocJustPassed = null;
        }
    }
}