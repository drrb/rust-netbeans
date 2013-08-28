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

import java.util.Collections;
import java.util.Set;
import org.netbeans.modules.csl.api.ElementHandle;
import org.netbeans.modules.csl.api.Modifier;
import org.netbeans.modules.csl.spi.DefaultCompletionProposal;

/**
 *
 */
public class RustCompletionProposal extends DefaultCompletionProposal {

    public static RustCompletionProposal forElement(ElementHandle element) {
        return new RustCompletionProposal(element);
    }
    private final ElementHandle element;
    private final Set<Modifier> modifiers;

    public RustCompletionProposal(ElementHandle element) {
        this.element = element;
        this.elementKind = element.getKind();
        this.modifiers = element.getModifiers();
    }

    @Override
    public String getName() {
        return element.getName();
    }

    @Override
    public ElementHandle getElement() {
        return element;
    }

    @Override
    public Set<Modifier> getModifiers() {
        return Collections.unmodifiableSet(modifiers);
    }
}
