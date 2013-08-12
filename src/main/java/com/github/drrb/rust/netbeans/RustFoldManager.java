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

import com.github.drrb.rust.netbeans.parsing.CollectingVisitor;
import com.github.drrb.rust.netbeans.parsing.NetbeansRustParser.NetbeansRustParserResult;
import com.github.drrb.rust.netbeans.parsing.RustParser;
import javax.swing.event.DocumentEvent;
import javax.swing.text.BadLocationException;

import com.github.drrb.rust.netbeans.parsing.RustTokenId;
import org.netbeans.api.editor.fold.Fold;
import org.netbeans.api.editor.fold.FoldHierarchy;
import org.netbeans.api.editor.fold.FoldType;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.spi.editor.fold.FoldHierarchyTransaction;
import org.netbeans.spi.editor.fold.FoldManager;
import org.netbeans.spi.editor.fold.FoldOperation;
import org.openide.util.Exceptions;
import static com.github.drrb.rust.netbeans.parsing.RustTokenId.*;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.swing.text.AbstractDocument;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.parsing.api.ParserManager;
import org.netbeans.modules.parsing.api.ResultIterator;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.parsing.api.UserTask;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.spi.editor.fold.FoldManagerFactory;

public class RustFoldManager implements FoldManager {

    private static final FoldType DOC_COMMENT_FOLD_TYPE = new FoldType("/**...*/");
    private static final FoldType COMMENT_FOLD_TYPE = new FoldType("/*...*/");
    private static final FoldType FUNCTION_FOLD_TYPE = new FoldType("{...}");
    private FoldOperation operations;

    @Override
    public void init(FoldOperation operations) {
        this.operations = operations;
    }

    @Override
    public void initFolds(FoldHierarchyTransaction transaction) {
        createAllFolds(transaction);
    }

    @Override
    public void insertUpdate(DocumentEvent event, FoldHierarchyTransaction transaction) {
        recreateFolds(transaction);
    }

    @Override
    public void removeUpdate(DocumentEvent event, FoldHierarchyTransaction transaction) {
        recreateFolds(transaction);
    }

    @Override
    public void changedUpdate(DocumentEvent event, FoldHierarchyTransaction transaction) {
        recreateFolds(transaction);
    }

    @Override
    public void removeEmptyNotify(Fold fold) {
    }

    @Override
    public void removeDamagedNotify(Fold fold) {
    }

    @Override
    public void expandNotify(Fold fold) {
    }

    @Override
    public void release() {
    }

    private void removeAllFolds(FoldHierarchyTransaction transaction) {
        FoldHierarchy hierarchy = getFoldHierarchy();
        Fold rootFold = hierarchy.getRootFold();
        recursivelyRemoveFolds(rootFold, transaction);
    }

    private void recursivelyRemoveFolds(Fold fold, FoldHierarchyTransaction transaction) {
        for (int i = 0; i < fold.getFoldCount(); i++) {
            Fold childFold = fold.getFold(i);
            recursivelyRemoveFolds(childFold, transaction);
        }
        if (!fold.equals(fold.getHierarchy().getRootFold()) && operations.owns(fold)) {
            operations.removeFromHierarchy(fold, transaction);
        }
    }

    private void createAllFolds(final FoldHierarchyTransaction transaction) {
        System.out.println("    creating folds");
        TokenHierarchy<AbstractDocument> tokenHierarchy = TokenHierarchy.get(getDocument());
        TokenSequence<RustTokenId> tokenSequence = tokenHierarchy.tokenSequence(RustTokenId.getLanguage());
        List<OffsetRange> comments = new LinkedList<OffsetRange>();
        List<OffsetRange> docComments = new LinkedList<OffsetRange>();
        while (tokenSequence.moveNext()) {
            int offset = tokenSequence.offset();
            Token<RustTokenId> token = tokenSequence.token();
            RustTokenId id = token.id();
            if (id == OTHER_BLOCK_COMMENT) {
                comments.add(new OffsetRange(offset, offset + token.length()));
            } else if (id == OUTER_DOC_COMMENT) {
                docComments.add(new OffsetRange(offset, offset + token.length()));
            }
        }
        createFolds(COMMENT_FOLD_TYPE, comments, transaction);
        createFolds(DOC_COMMENT_FOLD_TYPE, docComments, transaction);
        try {
            ParserManager.parse(Collections.singletonList(Source.create(getDocument())), new UserTask() {
                @Override
                public void run(ResultIterator resultIterator) throws Exception {
                    NetbeansRustParserResult result = (NetbeansRustParserResult) resultIterator.getParserResult();
                    RustParser.ProgContext prog = result.getAst();
                    Collection<OffsetRange> functions = prog.accept(new FunctionBodyCollectingVisitor());
                    createFolds(FUNCTION_FOLD_TYPE, functions, transaction);
                }
            });
        } catch (ParseException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void createFolds(FoldType foldType, Collection<OffsetRange> foldRanges, FoldHierarchyTransaction transaction) {
        for (OffsetRange foldrange : foldRanges) {
            try {
                operations.addToHierarchy(
                        foldType,
                        foldType.toString(),
                        false,
                        foldrange.getStart(),
                        foldrange.getEnd(),
                        0,
                        0,
                        getFoldHierarchy(), //Could be null, but maybe we pass this so we can get at it later?
                        transaction);
            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    private void recreateFolds(FoldHierarchyTransaction transaction) {
        removeAllFolds(transaction);
        createAllFolds(transaction);
    }

    private FoldHierarchy getFoldHierarchy() {
        return operations.getHierarchy();
    }

    private AbstractDocument getDocument() {
        return (AbstractDocument) getFoldHierarchy().getComponent().getDocument();
    }

    private class FunctionBodyCollectingVisitor extends CollectingVisitor<OffsetRange> {

        @Override
        public List<OffsetRange> visitFun_body(RustParser.Fun_bodyContext functionBody) {
            List<OffsetRange> result = new LinkedList<OffsetRange>();
            int functionStartIndex = functionBody.getStart().getStartIndex();
            int functionEndIndex = functionBody.getStop().getStopIndex() + 1;
            OffsetRange functionLocation = new OffsetRange(functionStartIndex, functionEndIndex);
            result.add(functionLocation);
            return aggregateResult(result, visitChildren(functionBody));
        }
    }

    @MimeRegistration(mimeType = RustLanguage.MIME_TYPE, service = FoldManagerFactory.class)
    public static class RustFoldManagerFactory implements FoldManagerFactory {

        @Override
        public FoldManager createFoldManager() {
            return new RustFoldManager();
        }
    }
}