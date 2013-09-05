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
package com.github.drrb.rust.netbeans.completion;

import com.github.drrb.rust.netbeans.RustLanguage;
import com.github.drrb.rust.netbeans.completion.RustCompletionResult.Truncated;
import static com.github.drrb.rust.netbeans.completion.RustElementDocumentation.forDocComment;
import com.github.drrb.rust.netbeans.parsing.NetbeansRustParser.NetbeansRustParserResult;
import com.github.drrb.rust.netbeans.parsing.index.RustDocComment;
import com.github.drrb.rust.netbeans.parsing.index.RustFunction;
import com.github.drrb.rust.netbeans.parsing.index.RustSourceIndex;
import com.github.drrb.rust.netbeans.parsing.index.RustStruct;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.modules.csl.api.CodeCompletionContext;
import org.netbeans.modules.csl.api.CodeCompletionHandler;
import org.netbeans.modules.csl.api.CodeCompletionResult;
import org.netbeans.modules.csl.api.CompletionProposal;
import org.netbeans.modules.csl.api.ElementHandle;
import org.netbeans.modules.csl.api.ElementKind;
import org.netbeans.modules.csl.api.Modifier;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.api.ParameterInfo;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.api.Snapshot;

/**
 *
 */
public class RustCodeCompletionHandler implements CodeCompletionHandler {

    private static final EnumSet<ElementKind> ELEMENT_TYPES_WITH_DOCS = EnumSet.of(ElementKind.CLASS, ElementKind.METHOD);

    @Override
    public CodeCompletionResult complete(CodeCompletionContext context) {
        String prefix = context.getPrefix();
        List<CompletionProposal> proposals = new LinkedList<CompletionProposal>();

        NetbeansRustParserResult parseResult = (NetbeansRustParserResult) context.getParserResult();
        RustSourceIndex index = parseResult.getIndex();
        for (RustFunction function : index.getFunctions()) {
            String functionName = function.getName();
            if (functionName.startsWith(prefix)) {
                RustDocComment docComment = function.getDocComment();
                RustElementHandle element =
                        RustElementHandle.with(functionName, function.getOffsetRange(), ElementKind.METHOD)
                        .withModifier(Modifier.STATIC)
                        .withDocumentation(forDocComment(docComment))
                        .build();
                proposals.add(RustCompletionProposal.forElement(element));
            }
        }

        for (RustStruct struct : index.getStructs()) {
            String structName = struct.getName();
            if (structName.startsWith(prefix)) {
                RustDocComment docComment = struct.getDocComment();
                RustElementHandle element =
                        RustElementHandle.with(structName, struct.getOffsetRange(), ElementKind.CLASS)
                        .withDocumentation(forDocComment(docComment))
                        .build();
                proposals.add(RustCompletionProposal.forElement(element));
            }
        }

        for (RustKeyword keyword : RustKeyword.values()) {
            final String keywordImage = keyword.image();
            if (keywordImage.startsWith(prefix)) {
                proposals.add(RustCompletionProposal.forElement(RustElementHandle.with(keywordImage, OffsetRange.NONE, ElementKind.KEYWORD).build()));
            }
        }

        CodeCompletionResult result = new RustCompletionResult(proposals, Truncated.NO);
        return result;
    }

    @Override
    public String document(ParserResult info, ElementHandle element) {
        RustElementHandle elementHandle = (RustElementHandle) element;
        return elementHandle.getDocumentationHtml();
    }

    @Override
    public ElementHandle resolveLink(String link, ElementHandle originalHandle) {
        return null;
    }

    @Override
    public String getPrefix(ParserResult info, int caretOffset, boolean upToOffset) {
        Snapshot snapshot = info.getSnapshot();
        CharSequence text = snapshot.getText();
        int startOfWord = startOfWordAt(caretOffset, text);
        return text.subSequence(startOfWord, caretOffset).toString();
    }

    @Override
    public QueryType getAutoQuery(JTextComponent component, String typedText) {
        return QueryType.NONE;
    }

    @Override
    public String resolveTemplateVariable(String variable, ParserResult info, int caretOffset, String name, @SuppressWarnings("rawtypes") Map parameters) {
        return null;
    }

    @Override
    public Set<String> getApplicableTemplates(Document doc, int selectionBegin, int selectionEnd) {
        return Collections.emptySet();
    }

    @Override
    public ParameterInfo parameters(ParserResult info, int caretOffset, CompletionProposal proposal) {
        return ParameterInfo.NONE;
    }

    private int startOfWordAt(int offset, CharSequence text) {
        int startOfWord = offset;
        while (startOfWord > 0 && RustLanguage.isRustIdentifierChar(text.charAt(startOfWord - 1))) {
            startOfWord--;
        }
        return startOfWord;
    }
}
