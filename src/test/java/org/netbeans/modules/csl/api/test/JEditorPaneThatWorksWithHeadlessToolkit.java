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
package org.netbeans.modules.csl.api.test;

import sun.awt.HeadlessToolkit;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;

class JEditorPaneThatWorksWithHeadlessToolkit extends JEditorPane {
    @Override
    public Toolkit getToolkit() {
        Toolkit originalToolkit = super.getToolkit();
        return new HeadlessToolkit(originalToolkit) {
            @Override
            public Clipboard getSystemSelection() throws HeadlessException {
                return null; // The default implementation raises a HeadlessException in tests.
            }
        };
    }
}
