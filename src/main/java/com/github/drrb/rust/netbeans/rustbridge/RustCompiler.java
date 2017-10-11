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
package com.github.drrb.rust.netbeans.rustbridge;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public class RustCompiler {

    //TODO: this looks similar to RustParser.parse()
    public List<RustParseMessage> compile(File sourcePath, String source, File relevantFile, List<String> searchPaths) {
        // It's important to tell rustc the full path, because it uses it to find
        //  mods referenced from this file.
        if (!sourcePath.isAbsolute()) {
            throw new IllegalArgumentException("Expected an absolute file, but got " + sourcePath);
        }
        RustNative.ParseMessageAccumulator messageAccumulator = new RustNative.ParseMessageAccumulator();
        RustNative.INSTANCE.compile(
                sourcePath.getAbsolutePath(),
                source,
                searchPaths.toArray(new String[searchPaths.size()]),
                searchPaths.size(),
                messageAccumulator
        );
        //TODO: it'd be faster to filter these while we're collecting them.
        // Can we do that, or do we need the other files' messages for later?
        List<RustParseMessage> relevantParseMessages = new LinkedList<>();
        for (RustParseMessage message : messageAccumulator.getMessages()) {
            if (message.getFile().equals(relevantFile)) {
                relevantParseMessages.add(message);
            }
        }
        return relevantParseMessages;
    }

}
