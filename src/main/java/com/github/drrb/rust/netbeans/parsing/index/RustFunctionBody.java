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
import java.util.LinkedList;
import java.util.List;
import org.netbeans.modules.csl.api.OffsetRange;

/**
 *
 */
public class RustFunctionBody {

    private final MultiMap<String, RustLocalVariableIdentifier> localVariableIdentifiersByName = new MultiMap<>();
    private final RangeMap<RustLocalVariableIdentifier> localVariableIdentifiersByOffset = new RangeMap<>();
    private final String text;
    private final OffsetRange offsetRange;

    RustFunctionBody(String text, OffsetRange offsetRange, List<RustLocalVariableIdentifier> localVariableIdentifiers) {
        this.text = text;
        this.offsetRange = offsetRange;
        for (RustLocalVariableIdentifier identifier : localVariableIdentifiers) {
            localVariableIdentifiersByName.add(identifier.getText(), identifier);
            localVariableIdentifiersByOffset.put(identifier.getOffsetRange(), identifier);
        }
    }

    public OffsetRange getOffsetRange() {
        return offsetRange;
    }

    public List<RustLocalVariableIdentifier> getLocalVariableIdentifiersNamed(String text) {
        return localVariableIdentifiersByName.get(text);
    }

    public Option<RustLocalVariableIdentifier> getLocalVariableIdentifierAt(int offset) {
        return localVariableIdentifiersByOffset.get(offset);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final List<RustLocalVariableIdentifier> localVariableIdentifiers = new LinkedList<>();
        private String text;
        private OffsetRange offsetRange;

        public RustFunctionBody build() {
            return new RustFunctionBody(text, offsetRange, localVariableIdentifiers);
        }

        public Builder setText(String text) {
            this.text = text;
            return this;
        }

        public Builder setOffsetRange(OffsetRange offsetRange) {
            this.offsetRange = offsetRange;
            return this;
        }

        public Builder addLocalVariableIdentifier(RustLocalVariableIdentifier localVariableIdentifier) {
            this.localVariableIdentifiers.add(localVariableIdentifier);
            return this;
        }
    }
}
