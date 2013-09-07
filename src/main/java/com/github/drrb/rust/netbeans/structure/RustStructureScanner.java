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
package com.github.drrb.rust.netbeans.structure;

import com.github.drrb.rust.netbeans.parsing.NetbeansRustParser.NetbeansRustParserResult;
import com.github.drrb.rust.netbeans.parsing.index.RustDocComment;
import com.github.drrb.rust.netbeans.parsing.index.RustEnum;
import com.github.drrb.rust.netbeans.parsing.index.RustFunction;
import com.github.drrb.rust.netbeans.parsing.index.RustImpl;
import com.github.drrb.rust.netbeans.parsing.index.RustImplMethod;
import com.github.drrb.rust.netbeans.parsing.index.RustSourceIndex;
import com.github.drrb.rust.netbeans.parsing.index.RustStruct;
import com.github.drrb.rust.netbeans.parsing.index.RustStructField;
import com.github.drrb.rust.netbeans.parsing.index.RustTrait;
import com.github.drrb.rust.netbeans.parsing.index.RustTraitImpl;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.antlr.v4.runtime.misc.MultiMap;
import org.netbeans.api.editor.fold.FoldType;
import static org.netbeans.modules.csl.api.ElementKind.CLASS;
import static org.netbeans.modules.csl.api.ElementKind.FIELD;
import static org.netbeans.modules.csl.api.ElementKind.INTERFACE;
import static org.netbeans.modules.csl.api.ElementKind.METHOD;
import static org.netbeans.modules.csl.api.Modifier.STATIC;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.api.StructureItem;
import org.netbeans.modules.csl.api.StructureScanner;
import org.netbeans.modules.csl.spi.ParserResult;

/**
 *
 */
public class RustStructureScanner implements StructureScanner {

    private static final String CODEBLOCKS_FOLD_TYPE = FoldType.CODE_BLOCK.code();
    private static final String DOC_COMMENTS_FOLD_TYPE = FoldType.DOCUMENTATION.code();

    @Override
    public List<? extends StructureItem> scan(ParserResult info) {
        List<StructureItem> structureItems = new LinkedList<>();

        NetbeansRustParserResult parseResult = (NetbeansRustParserResult) info;
        RustSourceIndex index = parseResult.getIndex();

        List<RustFunction> functions = index.getFunctions();
        for (RustFunction function : functions) {
            structureItems.add(new RustStructureItem(function.getName(), function.getOffsetRange(), METHOD, EnumSet.of(STATIC)));
        }

        List<RustStruct> structs = index.getStructs();
        for (RustStruct struct : structs) {
            RustStructureItem structItem = new RustStructureItem(struct.getName(), struct.getOffsetRange(), CLASS);
            structureItems.add(structItem);

            List<RustStructField> fields = struct.getBody().getFields();
            for (RustStructField field : fields) {
                structItem.addNestedItem(new RustStructureItem(field.getName(), field.getOffsetRange(), FIELD));
            }
        }

        List<RustTrait> traits = index.getTraits();
        for (RustTrait trait : traits) {
            structureItems.add(new RustStructureItem(trait.getName(), trait.getOffsetRange(), INTERFACE));
        }

        return structureItems;
    }

    @Override
    public Map<String, List<OffsetRange>> folds(ParserResult info) {
        MultiMap<String, OffsetRange> folds = new MultiMap<>();

        NetbeansRustParserResult parseResult = (NetbeansRustParserResult) info;
        RustSourceIndex index = parseResult.getIndex();

        List<RustFunction> functions = index.getFunctions();
        for (RustFunction function : functions) {
            //TODO: this raises a NPE when typing a function. Insert millions of null checks?
            folds.map(CODEBLOCKS_FOLD_TYPE, function.getBody().getOffsetRange());
        }

        List<RustStruct> structs = index.getStructs();
        for (RustStruct struct : structs) {
            folds.map(CODEBLOCKS_FOLD_TYPE, struct.getBody().getOffsetRange());
        }

        List<RustEnum> enums = index.getEnums();
        for (RustEnum rustEnum : enums) {
            folds.map(CODEBLOCKS_FOLD_TYPE, rustEnum.getBody().getOffsetRange());
        }

        List<RustImpl> impls = index.getImpls();
        for (RustImpl impl : impls) {
            folds.map(CODEBLOCKS_FOLD_TYPE, impl.getBody().getOffsetRange());
            List<RustImplMethod> implMethods = impl.getBody().getMethods();
            for (RustImplMethod implMethod : implMethods) {
                folds.map(CODEBLOCKS_FOLD_TYPE, implMethod.getBody().getOffsetRange());
            }
        }

        List<RustTrait> traits = index.getTraits();
        for (RustTrait trait : traits) {
            folds.map(CODEBLOCKS_FOLD_TYPE, trait.getBody().getOffsetRange());
        }

        List<RustTraitImpl> traitImpls = index.getTraitImpls();
        for (RustTraitImpl traitImpl : traitImpls) {
            folds.map(CODEBLOCKS_FOLD_TYPE, traitImpl.getBody().getOffsetRange());
            List<RustImplMethod> traitImplMethods = traitImpl.getBody().getMethods();
            for (RustImplMethod traitImplMethod : traitImplMethods) {
                folds.map(CODEBLOCKS_FOLD_TYPE, traitImplMethod.getBody().getOffsetRange());
            }
        }

        List<RustDocComment> rustdocs = index.getDocComments();
        for (RustDocComment docComment : rustdocs) {
            if (docComment.getText().contains("\n")) { //Don't fold single line comments
                folds.map(DOC_COMMENTS_FOLD_TYPE, docComment.getOffsetRange());
            }
        }

        return folds;
    }

    @Override
    public Configuration getConfiguration() {
        return new Configuration(false, false);
    }
}
