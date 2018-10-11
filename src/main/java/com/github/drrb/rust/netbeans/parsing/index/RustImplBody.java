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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.netbeans.modules.csl.api.OffsetRange;

/**
 *
 */
public class RustImplBody {

    private final OffsetRange offsetRange;
    private final List<RustImplMethod> methods;

    RustImplBody(OffsetRange offsetRange, List<RustImplMethod> methods) {
        this.offsetRange = offsetRange;
        this.methods = methods;
    }

    public OffsetRange getOffsetRange() {
        return offsetRange;
    }

    public List<RustImplMethod> getMethods() {
        return Collections.unmodifiableList(methods);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private OffsetRange offsetRange;
        private RustImplMethodBody body;
        private final List<RustImplMethod> methods = new LinkedList<>();

        RustImplBody build() {
            return new RustImplBody(offsetRange, methods);
        }

        Builder setOffsetRange(OffsetRange offsetRange) {
            this.offsetRange = offsetRange;
            return this;
        }

        Builder addMethod(RustImplMethod method) {
            this.methods.add(method);
            return this;
        }
    }
}
