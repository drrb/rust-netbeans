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
public class RustImplMethod {

    private final String name;
    private final OffsetRange offsetRange;
    private final RustImplMethodBody body;
    private final Map<String, RustImplMethodParameterName> functionParameterNamesByName = new HashMap<>();
    private final RangeMap<RustImplMethodParameterName> functionParameterNameRanges = new RangeMap<>();

    RustImplMethod(String name, OffsetRange offsetRange, RustImplMethodBody body, List<RustImplMethodParameterName> parameterNames) {
        this.name = name;
        this.offsetRange = offsetRange;
        this.body = body;
        for (RustImplMethodParameterName parameterName : parameterNames) {
            functionParameterNameRanges.put(parameterName.getOffsetRange(), parameterName);
            functionParameterNamesByName.put(parameterName.getText(), parameterName);
        }
    }

    public String getName() {
        return name;
    }

    public RustImplMethodBody getBody() {
        return body;
    }

    public OffsetRange getOffsetRange() {
        return offsetRange;
    }

    public Option<RustImplMethodParameterName> getParameterNameAt(int offeset) {
        return functionParameterNameRanges.get(offeset);
    }

    public Option<RustImplMethodParameterName> getParameterNameMatching(String text) {
        return Option.isIfNotNull(functionParameterNamesByName.get(text));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String name;
        private OffsetRange offsetRange;
        private RustImplMethodBody body;
        private final List<RustImplMethodParameterName> parameterNames = new LinkedList<>();

        RustImplMethod build() {
            return new RustImplMethod(name, offsetRange, body, parameterNames);
        }

        Builder setName(String name) {
            this.name = name;
            return this;
        }

        Builder setOffsetRange(OffsetRange offsetRange) {
            this.offsetRange = offsetRange;
            return this;
        }

        Builder setBody(RustImplMethodBody body) {
            this.body = body;
            return this;
        }

        Builder addParameterName(RustImplMethodParameterName parameterName) {
            parameterNames.add(parameterName);
            return this;
        }
    }
}
