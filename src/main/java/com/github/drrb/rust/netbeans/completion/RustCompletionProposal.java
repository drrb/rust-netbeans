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
package com.github.drrb.rust.netbeans.completion;

import java.util.Collections;
import java.util.Set;
import javax.swing.ImageIcon;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.modules.csl.api.ElementHandle;
import org.netbeans.modules.csl.api.ElementKind;
import org.netbeans.modules.csl.api.Modifier;
import org.netbeans.modules.csl.spi.DefaultCompletionProposal;
import org.openide.util.ImageUtilities;

/**
 *
 */
public class RustCompletionProposal extends DefaultCompletionProposal {

    @StaticResource
    private static final String RUST_ICON_PATH = "com/github/drrb/rust/netbeans/rust-icon_16x16.png";

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
    public ImageIcon getIcon() {
        if (element.getKind() == ElementKind.KEYWORD) {
            return ImageUtilities.loadImageIcon(RUST_ICON_PATH, false);
        } else {
            return null;
        }
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
