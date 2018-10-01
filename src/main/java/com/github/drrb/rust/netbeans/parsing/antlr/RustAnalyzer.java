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

import com.github.drrb.rust.antlr.RustBaseVisitor;
import com.github.drrb.rust.antlr.RustParser;
import com.github.drrb.rust.antlr.RustParser.BlockContext;
import static com.github.drrb.rust.netbeans.parsing.antlr.AntlrUtils.toOffsetRange;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.NoViableAltException;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.netbeans.modules.parsing.api.Snapshot;

/**
 *
 * @author Tim Boudreau
 */
class RustAnalyzer extends RustBaseVisitor<Void> {

    private final RustParseInfo info;
    private final Snapshot snapshot;
    private final AtomicBoolean cancelled;

    RustAnalyzer(Snapshot snapshot, AtomicBoolean cancelled) {
        this.info = new RustParseInfo();
        this.snapshot = snapshot;
        this.cancelled = cancelled;
    }

    @Override
    public Void visit(ParseTree tree) {
        if (cancelled.get()) {
            return null;
        }
        return super.visit(tree); //To change body of generated methods, choose Tools | Templates.
    }

    static RustParseInfo analyze(RustParser parser, Snapshot snapshot, AtomicBoolean cancelled) {
        RustAnalyzer result = new RustAnalyzer(snapshot, cancelled);
        parser.setErrorHandler(new ErrStrategy());
        parser.addErrorListener(new ErrorCapturer(snapshot, parser, result.info));
        parser.crate().accept(result);
        return result.info;
    }

    static boolean FULL_MESSAGES = Boolean.getBoolean("rust.antlr.full.messages");

    static class ErrStrategy extends DefaultErrorStrategy {

        // Report full error messages for some tests
        @Override
        protected Token getMissingSymbol(Parser recognizer) {
            Token oldRes = super.getMissingSymbol(recognizer);
            if (true) {
                return oldRes;
            }
            Token curr = recognizer.getCurrentToken();
//            System.out.println("oldRes type " + oldRes.getType() + " index " + oldRes.getTokenIndex());
            if (oldRes.getTokenIndex() == -1) {
                IntervalSet expecting = getExpectedTokens(recognizer);
                int expectedTokenType = Token.INVALID_TYPE;
                if (!expecting.isNil()) {
//                    System.out.println("EXPECTING " + expecting.toString(recognizer.getVocabulary()));
                    expectedTokenType = expecting.getMinElement(); // get any element
                }
//                System.out.println("EXPECTED TOKEN TYPE " + recognizer.getVocabulary().getDisplayName(nextTokensState));
                String toInsert = null;
                switch (curr.getText()) {
                    case "(":
                        toInsert = ")";
                        break;
                    case "[":
                        toInsert = "]";
                        break;
                    case "{":
                        toInsert = "}";
                        break;
                    case "<":
                        toInsert = ">";
                        break;
                }
                if (toInsert != null) {
                    System.out.println("CONJURE MISSING TOKEN " + toInsert
                            + " for " + curr.getText() + " index "
                            + curr.getTokenIndex() + " type " + curr.getType());

                    int start = curr.getStartIndex();
                    int stop = curr.getStopIndex();
                    return recognizer.getTokenFactory().create(new Pair<TokenSource, CharStream>(
                            curr.getTokenSource(), curr.getTokenSource().getInputStream()),
                            expectedTokenType, toInsert,
                            Token.DEFAULT_CHANNEL,
                            start, stop,
                            curr.getLine(), curr.getCharPositionInLine());
                }
            }

            System.out.println("GET MISSING SYMBOL at " + recognizer.getCurrentToken()
                    + " super returns " + oldRes);
            return oldRes;
        }

        @Override
        protected void reportNoViableAlternative(Parser recognizer, NoViableAltException e) {
            Token offending = e.getOffendingToken();
            if (offending.getText().length() == 1 && !FULL_MESSAGES) {
                recognizer.notifyErrorListeners(offending, "Unexpected symbol: '" + offending.getText() + "'", e);
            } else {
                super.reportNoViableAlternative(recognizer, e);
            }
        }

        @Override
        protected void reportUnwantedToken(Parser recognizer) {
            if (FULL_MESSAGES) {
                super.reportUnwantedToken(recognizer);
                return;
            }
            if (inErrorRecoveryMode(recognizer)) {
                return;
            }

            beginErrorCondition(recognizer);

            Token t = recognizer.getCurrentToken();
            String tokenName = getTokenErrorDisplay(t);
            IntervalSet expecting = getExpectedTokens(recognizer);
            String msg;
            // Keep error messages to a reasonable size - if we're going
            // to print out every keyword in the language, that doesn't
            // help anyone
            if (expecting.size() > 4) {
                msg = "extraneous input " + tokenName;
            } else {
                msg = "extraneous input " + tokenName + " expecting "
                        + expecting.toString(recognizer.getVocabulary());
            }
            recognizer.notifyErrorListeners(t, msg, null);
        }
    }

    static class ErrorCapturer implements ANTLRErrorListener {

        private final Snapshot snapshot;
        private final RustParser parser;
        private final RustParseInfo info;

        public ErrorCapturer(Snapshot snapshot, RustParser parser, RustParseInfo info) {
            this.snapshot = snapshot;
            this.parser = parser;
            this.info = info;
        }

        @Override
        public void syntaxError(Recognizer recognizer, Object offendingSymbol,
                int line,
                int charPositionInLine,
                String msg,
                RecognitionException e) {
            org.antlr.v4.runtime.Token currentToken = parser.getCurrentToken();
            if (!FULL_MESSAGES) {
                switch (msg) {
                    case "extraneous input '<EOF>'":
                        msg = "Premature end of file";
                }
            }
            info.addError(new SyntaxError(currentToken, msg, snapshot.getSource().getFileObject()));

//            info.addError(new DefaultError(currentToken.toString(), "Syntax error", msg,
//                    snapshot.getSource().getFileObject(),
//                    currentToken.getStartIndex(), currentToken.getStopIndex() + 1,
//                    true, Severity.FATAL));
        }

        @Override
        public void reportAmbiguity(Parser parser, DFA dfa, int i, int i1, boolean bln, BitSet bitset, ATNConfigSet atncs) {
//            System.out.println("AE ambiguity at " + i + ":" + i1 + " " + dfa.toLexerString()
//                + " " + parser.getCurrentToken());
//            String exp = parser.getExpectedTokens().toString(parser.getVocabulary());
//            System.out.println("EXPECTED: " + exp);
        }

        @Override
        public void reportAttemptingFullContext(Parser parser, DFA dfa, int i, int i1, BitSet bitset, ATNConfigSet atncs) {
//            System.out.println("AE attempt full context at " + i + ":" + i1);
        }

        @Override
        public void reportContextSensitivity(Parser parser, DFA dfa, int i, int i1, int i2, ATNConfigSet atncs) {
//            System.out.println("AE context sensitivity at " + i + ":" + i1);
        }

    }

    @Override
    public Void visitField(RustParser.FieldContext ctx) {
        return super.visitField(ctx);
    }

    @Override
    public Void visitAttr(RustParser.AttrContext ctx) {
        info.addSemanticRegion(RustElementKind.ATTR, toOffsetRange(ctx));
        info.addStructureItem(new RustStructureItemImpl(ctx.toString(), RustElementKind.ATTR, snapshot, ctx));
        return super.visitAttr(ctx); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Void visitField_decl(RustParser.Field_declContext ctx) {
        String id = findIdentifier(ctx, RustElementKind.FIELD);
        if (id != null) {
            info.addStructureItem(new RustStructureItemImpl(id, RustElementKind.FIELD, snapshot, ctx));
        }
        return super.visitField_decl(ctx);
    }

    @Override
    public Void visitEnum_variant(RustParser.Enum_variantContext ctx) {
        String id = findIdentifier(ctx, RustElementKind.ENUM_CONSTANT);
        if (id != null) {
            info.addStructureItem(new RustStructureItemImpl(id, RustElementKind.FIELD, snapshot, ctx));
        }
        return super.visitEnum_variant(ctx); //To change body of generated methods, choose Tools | Templates.
    }

    public static void unwind(ParserRuleContext ctx) {
        AntlrUtils.print(ctx);
    }

    @Override
    public Void visitFn_head(RustParser.Fn_headContext ctx) {
        String id = findIdentifier(ctx, RustElementKind.FUNCTION);
        if (id != null) {
            id = id.trim();
            RustStructureItemImpl item = new RustStructureItemImpl(id, RustElementKind.FUNCTION, snapshot, ctx);
            info.pushStructureItem(item, () -> {
                super.visitFn_head(ctx);
            });
        } else {
            super.visitFn_head(ctx);
        }
        return null;
    }

    @Override
    public Void visitLifetime_list(RustParser.Lifetime_listContext ctx) {
        info.addSemanticRegion(RustElementKind.LIFETIME, toOffsetRange(ctx));
        return super.visitLifetime_list(ctx);
    }

    @Override
    public Void visitEnum_decl(RustParser.Enum_declContext ctx) {
        String id = findIdentifier(ctx, RustElementKind.ENUM);
        if (id != null) {
            RustStructureItemImpl item = new RustStructureItemImpl(id, RustElementKind.ENUM, snapshot, ctx);
            info.pushStructureItem(item, () -> {
                super.visitEnum_decl(ctx);
            });
        } else {
            super.visitEnum_decl(ctx);
        }
        return null;
    }

    @Override
    public Void visitType_decl(RustParser.Type_declContext ctx) {
        String id = findIdentifier(ctx, RustElementKind.TYPE);
        if (id != null) {
            RustStructureItemImpl item = new RustStructureItemImpl(id, RustElementKind.TYPE, snapshot, ctx);
            info.pushStructureItem(item, () -> {
                super.visitType_decl(ctx);
            });
        } else {
            super.visitType_decl(ctx);
        }
        return null;
    }

    @Override
    public Void visitImpl_block(RustParser.Impl_blockContext ctx) {
        String id = findIdentifier(ctx, RustElementKind.IMPL);
//        System.out.println("IMPL BLOCK FOUND ID " + id);
        if (id != null) {
            RustStructureItemImpl item = new RustStructureItemImpl(id, RustElementKind.IMPL, snapshot, ctx);
            info.pushStructureItem(item, () -> {
                super.visitImpl_block(ctx);
            });
        } else {
            super.visitImpl_block(ctx);
        }
        return null;
    }

    @Override
    public Void visitStruct_decl(RustParser.Struct_declContext ctx) {
        String id = findIdentifier(ctx.getParent(), RustElementKind.STRUCT);
        if (id != null) {
            RustStructureItemImpl item = new RustStructureItemImpl(id, RustElementKind.STRUCT, snapshot, ctx);
            info.pushStructureItem(item, () -> {
                super.visitStruct_decl(ctx);
            });
        } else {
            super.visitStruct_decl(ctx);
        }
        return null;
    }

    @Override
    public Void visitMacro_tail(RustParser.Macro_tailContext ctx) {
        return super.visitMacro_tail(ctx);
    }

    @Override
    public Void visitTrait_decl(RustParser.Trait_declContext ctx) {
        String id = findIdentifier(ctx, RustElementKind.TRAIT);
        if (id != null) {
            RustStructureItemImpl item = new RustStructureItemImpl(id, RustElementKind.TRAIT, snapshot, ctx);
            info.pushStructureItem(item, () -> {
                super.visitTrait_decl(ctx);
            });
        } else {
            super.visitTrait_decl(ctx);
        }
        return null;
    }

    private final IdentifierFinder idFinder = new IdentifierFinder();

    private String findIdentifier(ParserRuleContext ctx, RustElementKind kind) {
        String result = idFinder.find(ctx);
        if (result != null) {
            info.addSemanticRegion(idFinder.name, kind, toOffsetRange(idFinder.context),
                    idFinder.mutable, idFinder.visibility(), idFinder.statyc);
        }
        return result;
    }

    static final class IdentifierFinder extends RustBaseVisitor<Void> {

        private String name;
        private RustParser.IdentContext context;
        private boolean mutable;
        private boolean statyc;
        private final Set<RustVisibility> visibility = EnumSet
                .noneOf(RustVisibility.class);

        Set<RustVisibility> visibility() {
            return EnumSet.copyOf(visibility);
        }

        void reset() {
            name = null;
            context = null;
            mutable = false;
            statyc = false;
            visibility.clear();
        }

        String find(ParserRuleContext ctx) {
            reset();
            ctx.accept(this);
            return this.name;
        }

        @Override
        public Void visit(ParseTree tree) {
            if (name != null) {
                return null;
            }
            return super.visit(tree);
        }

        private void maybeAddVisibility(RustVisibility vis) {
            if (vis != null) {
                visibility.add(vis);
            }
        }

        boolean inVisitVisibility;

        @Override
        public Void visitVisibility(RustParser.VisibilityContext ctx) {
            inVisitVisibility = true;
            try {
                RustParser.Visibility_restrictionContext vr = ctx.visibility_restriction();
                if (vr != null) {
                    Token stop = vr.stop;
                    TokenSource src = vr.start.getTokenSource();
                    CharStream in = src.getInputStream();
                    for (Token tk = src.nextToken(); in.index() < in.size() && tk.getStopIndex() <= stop.getStopIndex(); tk = src.nextToken()) {
                        maybeAddVisibility(RustVisibility.forToken(tk));
                        switch (tk.getText()) {
                            // XXX could look these up faster by token type, but
                            // because they aren't named in the grammar, they are,
                            // e.g. T__13, which is not a stable identifier.  If
                            // we wind up patching the grammar, this is something
                            // to fix
                            case "pub":
                            case "crate":
                            case "super":
                            case "in":
                        }
                    }
                }
                return super.visitVisibility(ctx);
            } finally {
                inVisitVisibility = false;
            }
        }

        @Override
        public Void visitIdent(RustParser.IdentContext ctx) {
            if (inVisitVisibility) {
                // "in" visibility will have an identifier which
                // is not the one we want
                return super.visitIdent(ctx);
            }
            if (name != null) {
                return null;
            }
            assert name == null && context == null : "Already called with " + name;
            name = ctx.getText();
            context = ctx;
            // Don't call super - we're done visiting after we find
            // the identifier
            return null;
        }

        @Override
        public Void visitStatic_decl(RustParser.Static_declContext ctx) {
            statyc = true;
            return super.visitStatic_decl(ctx);
        }

        @Override
        public Void visitMut_or_const(RustParser.Mut_or_constContext ctx) {
            if ("mut".equals(ctx.start.getText())) {
                mutable = true;
            }
            return super.visitMut_or_const(ctx);
        }
    }

    @Override
    public Void visitBlock(BlockContext ctx) {
        info.addBlock(ctx);
        return super.visitBlock(ctx);
    }

    @Override
    public Void visitTerminal(TerminalNode tn) {
        return super.visitTerminal(tn);
    }

    @Override
    public Void visitErrorNode(ErrorNode en) {
        if (en.getSourceInterval().a != -1) {
            info.addError(new ErrImpl(en, snapshot));
        }
        return super.visitErrorNode(en);
    }

    @Override
    public Void visitFn_decl(RustParser.Fn_declContext ctx) {
//        unwind(ctx);
        return super.visitFn_decl(ctx); //To change body of generated methods, choose Tools | Templates.
    }
}
