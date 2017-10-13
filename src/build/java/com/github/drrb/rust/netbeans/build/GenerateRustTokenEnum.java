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
package com.github.drrb.rust.netbeans.build;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;

public class GenerateRustTokenEnum {
    public static void main(String[] args) throws Exception {
        Path rustTokenIdSource = findFile("src/main/java", "RustTokenId.java");
        Path constantsSource = findFile("target/generated-sources", "RustParserConstants.java");
        List<MatchResult> constantsDecls = getOccurrences(constantsSource, "/\\*\\* (?:RegularExpression Id|End of File). \\*/\\n  int ([^ ]+)");
        List<String> constants = constantsDecls.stream().map((d) -> d.group(1)).collect(toList());

        List<MatchResult> tokenAndCategoryDecls = getOccurrences(rustTokenIdSource, "([A-Za-z0-9_]+)\\(RustParserConstants\\..*, TokenCategory\\.(.*)\\)");
        Map<String, String> tokenCategories = new HashMap<>();
        for (MatchResult tokenAndCategoryDecl : tokenAndCategoryDecls) {
            tokenCategories.put(tokenAndCategoryDecl.group(1), tokenAndCategoryDecl.group(2));
        }

        List<String> tokenKindDecls = constants.stream().map((c) ->  "    " + c + "(RustParserConstants." + c + ", TokenCategory." + tokenCategories.getOrDefault(c, "IDENTIFIER") + ")").collect(toList());
        String newTokenIdSource = read(rustTokenIdSource)
                .replaceAll(".*[A-Z_]*\\(RustParserConstants.*\n", "")
                .replaceAll("(enum RustTokenId implements TokenId \\{\n\n)", "$1" + String.join(",\n", tokenKindDecls) + ";\n")
                ;
        System.err.println("Writing constants to " + rustTokenIdSource);

        Files.write(rustTokenIdSource, newTokenIdSource.getBytes(UTF_8));
    }

    private static String read(Path file) throws IOException {
        return new String(Files.readAllBytes(file), UTF_8);
    }

    private static List<MatchResult> getOccurrences(Path constantsSourceFile, String regex) throws IOException {
        Pattern pattern = Pattern.compile(regex);
        Scanner scanner = new Scanner(constantsSourceFile, "UTF-8");
        List<MatchResult> occurences = new LinkedList<>();
        while (scanner.findWithinHorizon(pattern, 0) != null) {
            occurences.add(scanner.match());
        }
        return occurences;
    }

    private static Path findFile(String dir, String name) throws IOException {
        return findFile(Paths.get(dir), name);
    }

    private static Path findFile(Path baseDir, String name) throws IOException {
        return Files.find(baseDir, 10, fileNamed(name)).findFirst().get();
    }

    private static BiPredicate<Path, BasicFileAttributes> fileNamed(String name) {
        return (file, attrs) -> attrs.isRegularFile() && file.getFileName().endsWith(name);
    }
}
