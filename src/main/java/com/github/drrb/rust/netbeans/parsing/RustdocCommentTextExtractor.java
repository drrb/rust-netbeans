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
package com.github.drrb.rust.netbeans.parsing;

import java.util.Arrays;

/**
 *
 */
public class RustdocCommentTextExtractor {

    public String extractTextFromRustDoc(String docString) {
        if (docString == null) {
            return null;
        }

        if (docString.startsWith("///")) {
            // Remove single-line doc prefix
            return docString.replaceFirst("\\A///\\s*", "");
        }
        //Remove starting and ending doc comment bits.
        String innerBit = docString.replaceFirst("\\A/\\*\\*(\\s*\\n)?", "").replaceAll("\\s*\\*/\\Z", "");
        String[] lines = innerBit.split("\n");

        removeLineStarPrefix(lines);
        String indent = getSmallestIndent(lines);
        removePrefix(indent, lines);
        StringBuilder outdentedText = new StringBuilder(innerBit.length());
        for (String line : lines) {
            outdentedText.append(line).append("\n");
        }

        String text = outdentedText.toString().replaceAll("\n\\Z", "");
        return text;
    }

    private int indentDepth(String line) {
        int indentDepth = 0;
        while (line.length() > indentDepth && Character.isWhitespace(line.charAt(indentDepth))) {
            indentDepth++;
        }
        return indentDepth;
    }

    private String indentOfDepth(int depth) {
        char[] indent = new char[depth];
        Arrays.fill(indent, ' ');
        return String.valueOf(indent);
    }

    private void removeLineStarPrefix(String[] line) {
        for (int i = 0; i < line.length; i++) {
            String string = line[i];
            line[i] = string.replaceFirst("^\\s*\\*", "");
        }
    }

    private String getSmallestIndent(String[] formattedLines) {
        int smallestIndent = Integer.MAX_VALUE;
        for (String line : formattedLines) {
            // Don't count empty lines when calculating the indent to remove
            if (line.isEmpty()) {
                continue;
            }
            int indentDepth = indentDepth(line);
            smallestIndent = Math.min(smallestIndent, indentDepth);
        }
        String indent = indentOfDepth(smallestIndent);
        return indent;
    }

    private void removePrefix(String prefix, String[] strings) {
        for (int i = 0; i < strings.length; i++) {
            String line = strings[i];
            String stringWithoutPrefix;
            if (line.startsWith(prefix)) {
                stringWithoutPrefix = line.replaceFirst(prefix, "");
            } else {
                stringWithoutPrefix = line;
            }
            strings[i] = stringWithoutPrefix;
        }
    }
}
