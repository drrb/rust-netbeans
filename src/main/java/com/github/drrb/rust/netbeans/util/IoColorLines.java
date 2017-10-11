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
package com.github.drrb.rust.netbeans.util;

import java.io.IOException;
import org.openide.util.Exceptions;
import org.openide.windows.IOColorLines;
import org.openide.windows.IOColors;
import org.openide.windows.InputOutput;

/**
 *
 */
public class IoColorLines {

    public static void printDebug(InputOutput io, CharSequence text) {
        if (IOColorLines.isSupported(io)) {
            try {
                IOColorLines.println(io, text, IOColors.getColor(io, IOColors.OutputType.LOG_DEBUG));
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        } else {
            io.getOut().println(text);
        }
    }
}
