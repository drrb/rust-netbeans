/**
 * Copyright (C) 2017 drrb
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
package com.github.drrb.rust.netbeans.parsing.index;

import com.github.drrb.rust.netbeans.util.Option;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public class RustSourceIndex {

    private final List<RustStruct> structs = new LinkedList<>();
    private final List<RustImpl> impls = new LinkedList<>();
    private final List<RustTrait> traits = new LinkedList<>();
    private final List<RustTraitImpl> traitImpls = new LinkedList<>();
    private final List<RustEnum> enums = new LinkedList<>();
    private final List<RustFunction> functions = new LinkedList<>();
    private final RangeMap<RustFunction> functionRanges = new RangeMap<>();
    private final List<RustDocComment> docComments = new LinkedList<>();

    public List<RustFunction> getFunctions() {
        return Collections.unmodifiableList(functions);
    }

    public Option<RustFunction> getFunctionAt(int offset) {
        return functionRanges.get(offset);
    }

    void addFunction(RustFunction function) {
        functions.add(function);
        functionRanges.put(function.getOffsetRange(), function);
    }

    public List<RustDocComment> getDocComments() {
        return Collections.unmodifiableList(docComments);
    }

    void addDocComment(RustDocComment docComment) {
        docComments.add(docComment);
    }

    public List<RustStruct> getStructs() {
        return Collections.unmodifiableList(structs);
    }

    void addStruct(RustStruct struct) {
        structs.add(struct);
    }

    public List<RustEnum> getEnums() {
        return Collections.unmodifiableList(enums);
    }

    void addEnum(RustEnum rustEnum) {
        enums.add(rustEnum);
    }

    public List<RustImpl> getImpls() {
        return Collections.unmodifiableList(impls);
    }

    void addImpl(RustImpl impl) {
        impls.add(impl);
    }

    public List<RustTrait> getTraits() {
        return Collections.unmodifiableList(traits);
    }

    void addTrait(RustTrait trait) {
        traits.add(trait);
    }

    public List<RustTraitImpl> getTraitImpls() {
        return Collections.unmodifiableList(traitImpls);
    }

    void addTraitImpl(RustTraitImpl traitImpl) {
        traitImpls.add(traitImpl);
    }
}
