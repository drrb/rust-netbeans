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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.netbeans.modules.csl.api.OffsetRange;

/**
 *
 */
public class RustFunction {

    private final String name;
    private final OffsetRange offsetRange;
    private final RustDocComment docComment;
    private final RustFunctionBody body;
    private final Map<String, RustFunctionParameterName> functionParameterNamesByName = new HashMap<>();
    private final RangeMap<RustFunctionParameterName> functionParameterNameRanges = new RangeMap<>();

    RustFunction(String name, OffsetRange offsetRange, RustDocComment docComment, RustFunctionBody body, List<RustFunctionParameterName> parameterNames) {
        this.name = name;
        this.offsetRange = offsetRange;
        this.docComment = docComment;
        this.body = body;
        for (RustFunctionParameterName parameterName : parameterNames) {
            functionParameterNameRanges.put(parameterName.getOffsetRange(), parameterName);
            functionParameterNamesByName.put(parameterName.getText(), parameterName);
        }
    }

    public String getName() {
        return name;
    }

    public RustFunctionBody getBody() {
        return body;
    }

    public OffsetRange getOffsetRange() {
        return offsetRange;
    }

    public RustDocComment getDocComment() {
        return docComment;
    }

    public Option<RustFunctionParameterName> getParameterNameAt(int offeset) {
        return functionParameterNameRanges.get(offeset);
    }

    public Option<RustFunctionParameterName> getParameterNameMatching(String text) {
        return Option.isIfNotNull(functionParameterNamesByName.get(text));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String name;
        private OffsetRange offsetRange;
        private RustFunctionBody body;
        private RustDocComment docComment;
        private List<RustFunctionParameterName> parameterNames = new LinkedList<>();

        RustFunction build() {
            return new RustFunction(name, offsetRange, docComment, body, parameterNames);
        }

        Builder setName(String name) {
            this.name = name;
            return this;
        }

        Builder setOffsetRange(OffsetRange offsetRange) {
            this.offsetRange = offsetRange;
            return this;
        }

        Builder setBody(RustFunctionBody body) {
            this.body = body;
            return this;
        }

        Builder addParameterName(RustFunctionParameterName parameterName) {
            parameterNames.add(parameterName);
            return this;
        }

        Builder setDocComment(RustDocComment docComment) {
            this.docComment = docComment;
            return this;
        }
    }
}
