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

import com.github.drrb.rust.netbeans.parsing.NetbeansRustParser.NetbeansRustParserResult;
import com.github.drrb.rust.netbeans.parsing.RustParser;
import java.util.EnumSet;
import javax.swing.event.DocumentEvent;
import javax.swing.text.BadLocationException;

import com.github.drrb.rust.netbeans.parsing.RustFunction;
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
import com.github.drrb.rust.netbeans.parsing.RustFunctionCollectingVisitor;
import java.util.Collection;
import java.util.Collections;
import javax.swing.text.AbstractDocument;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.modules.parsing.api.ParserManager;
import org.netbeans.modules.parsing.api.ResultIterator;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.parsing.api.UserTask;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.spi.editor.fold.FoldManagerFactory;

public class RustFoldManager implements FoldManager {

    private static final FoldType COMMENT_FOLD_TYPE = new FoldType("/*...*/");
    private static final FoldType FUNCTION_FOLD_TYPE = new FoldType("fn { .. }");
    private FoldOperation operations;

    @Override
    public void init(FoldOperation operations) {
        this.operations = operations;
    }

    @Override
    public void initFolds(FoldHierarchyTransaction transaction) {
        recreateFolds(transaction);
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
        FoldHierarchy hierarchy = operations.getHierarchy();
        Fold rootFold = hierarchy.getRootFold();
        recursivelyRemoveFolds(rootFold, transaction);
    }

    private void recursivelyRemoveFolds(Fold fold, FoldHierarchyTransaction transaction) {
        for (int i = 0; i < fold.getFoldCount(); i++) {
            Fold childFold = fold.getFold(i);
            recursivelyRemoveFolds(childFold, transaction);
        }
        if (!fold.equals(fold.getHierarchy().getRootFold())) {
            operations.removeFromHierarchy(fold, transaction);
        }
    }

    private void createAllFolds(AbstractDocument document, final FoldHierarchy foldHierarchy, final FoldHierarchyTransaction transaction) {
        TokenHierarchy<AbstractDocument> tokenHierarchy = TokenHierarchy.get(document);
        TokenSequence<RustTokenId> ts = tokenHierarchy.tokenSequence(RustTokenId.getLanguage());
        while (ts.moveNext()) {
            int offset = ts.offset();
            Token<RustTokenId> token = ts.token();
            RustTokenId id = token.id();
            if (EnumSet.of(OTHER_BLOCK_COMMENT, OUTER_DOC_COMMENT).contains(id)) {
                FoldType type = COMMENT_FOLD_TYPE;
                try {
                    operations.addToHierarchy(
                            type,
                            type.toString(),
                            false,
                            offset,
                            offset + token.length(),
                            0,
                            0,
                            foldHierarchy, //Could be null, but maybe we pass this so we can get at it later?
                            transaction);
                } catch (BadLocationException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
        try {
            ParserManager.parse(Collections.singletonList(Source.create(document)), new UserTask() {
                @Override
                public void run(ResultIterator resultIterator) throws Exception {
                    NetbeansRustParserResult result = (NetbeansRustParserResult) resultIterator.getParserResult();
                    RustParser.ProgContext prog = result.getAst();
                    Collection<RustFunction> functions = prog.accept(new RustFunctionCollectingVisitor());
                    for (RustFunction function : functions) {
                        try {
                            operations.addToHierarchy(
                                    FUNCTION_FOLD_TYPE,
                                    String.format("fn %s { ... }", function.getName()),
                                    false,
                                    function.getStartIndex(),
                                    function.getEndIndex() + 1,
                                    0,
                                    0,
                                    foldHierarchy, //Could be null, but maybe we pass this so we can get at it later?
                                    transaction);
                        } catch (BadLocationException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                }
            });
        } catch (ParseException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void recreateFolds(FoldHierarchyTransaction transaction) {
        FoldHierarchy foldHierarchy = operations.getHierarchy();
        AbstractDocument document = (AbstractDocument) foldHierarchy.getComponent().getDocument();
        document.readLock();
        try {
            foldHierarchy.lock();
            try {
                FoldHierarchyTransaction foldHierarchyTransaction = operations.openTransaction();
                try {
                    removeAllFolds(foldHierarchyTransaction);
                    createAllFolds(document, foldHierarchy, foldHierarchyTransaction);
                } finally {
                    foldHierarchyTransaction.commit();
                }
            } finally {
                foldHierarchy.unlock();
            }
        } finally {
            document.readUnlock();
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