/*
 * Copyright (C) 2015 drrb
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.drrb.rust.netbeans.build;

import java.io.File;
import java.io.IOException;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Comparator.reverseOrder;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collector;
import static java.util.stream.Collector.Characteristics.CONCURRENT;
import static java.util.stream.Collector.Characteristics.UNORDERED;
import static java.util.stream.Collectors.joining;
import java.util.stream.Stream;
import org.openide.util.Exceptions;

/**
 *
 */
public class CompileRustBridge {

    private static final ZonedDateTime EPOCH = FileTime.fromMillis(0).toInstant().atZone(ZoneId.systemDefault());
    private static final Path RUST_OUTPUT_DIR = Paths.get("target", "rust-libs");

    public static void main(String[] args) throws Exception {
        Paths.get("target", "rust-libs").toFile().mkdirs();
        if (changesDetected()) {
            System.out.println("Changes detected. Compiling all Rust crates!");
            crates().forEach(CompileRustBridge::compile);
        } else {
            System.out.println("No changes detected. Not recompiling Rust crates.");
        }
    }

    private static boolean changesDetected() throws IOException {
        ZonedDateTime lastSourceChange = rustSources().collect(newestChange());
        ZonedDateTime lastCompilation = compiledRustLibraries().collect(newestChange());
        return lastSourceChange.isAfter(lastCompilation);
    }

    private static void compile(Path sourceFile) {
        System.out.format("Compiling crate %s%n", sourceFile);
        try {
            Process process = rustcProcess(sourceFile).inheritIO().start();
            process.waitFor(2, TimeUnit.MINUTES);
            if (process.exitValue() != 0) {
                throw new RuntimeException(String.format("rustc exited nonzero (status code = %s)", process.exitValue()));
            }
            compiledRustLibraries().forEach(CompileRustBridge::moveLibIntoClasspath);
        } catch (IOException | InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static ProcessBuilder rustcProcess(Path crateFile) {
        List<String> commandParts;
        List<String> rustcArgs = asList("--out-dir", RUST_OUTPUT_DIR.toString(), crateFile.toString());
        if (inNetbeans() && new File("/bin/bash").isFile()) {
            System.out.println("(running rustc via bash because we're in NetBeans)");
            commandParts = asList("/bin/bash", "-lc", "rustc " + rustcArgs.stream().collect(joining(" ")));
        } else {
            commandParts = new LinkedList<>(asList("rustc"));
            commandParts.addAll(rustcArgs);
        }
        System.out.format("Running command: %s%n", commandParts);
        return new ProcessBuilder(commandParts);
    }

    private static void moveLibIntoClasspath(Path library) {
        try {
            Path outputDir = outputDir();
            outputDir.toFile().mkdirs();
            System.out.format("Installing %s into %s%n", library, outputDir);
            Files.copy(library, outputDir.resolve(library.getFileName()), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static Path outputDir() {
        return Paths.get("target", "classes", osArchName());
    }

    private static String osArchName() {
        return Os.getCurrent().jnaArchString();
    }

    private static Stream<Path> crates() throws IOException {
        return rustSources().filter(CompileRustBridge::isCrate);
    }

    private static Stream<Path> rustSources() throws IOException {
        return Files.find(Paths.get("src", "main", "rust"), 10, CompileRustBridge::isRustSource);
    }

    private static Stream<Path> compiledRustLibraries() throws IOException {
        return Files.find(RUST_OUTPUT_DIR, 1, CompileRustBridge::isDylib);
    }

    private static boolean inNetbeans() {
        for (Map.Entry<String, String> envVars : System.getenv().entrySet()) {
            String key = envVars.getKey();
            String value = envVars.getValue();
            if (key.matches("JAVA_MAIN_CLASS_\\d+") && value.equals("org.netbeans.Main")) {
                return true;
            }
        }
        return false;
    }

    private static boolean isRustSource(Path path, BasicFileAttributes attributes) {
        return attributes.isRegularFile() && path.toString().endsWith(".rs");
    }

    private static boolean isCrate(Path path) {
        try {
            if (path.toFile().isFile() && path.toString().endsWith(".rs")) {
                List<String> lines = Files.readAllLines(path, UTF_8);
                return lines.stream().anyMatch((line) -> line.matches(".*#!\\[crate_type.*\\].*"));
            } else {
                return false;
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static boolean isDylib(Path path, BasicFileAttributes attributes) {
        String pathString = path.toString();
        List<String> dylibExtensions = asList(".dylib", ".so", ".dll");
        return attributes.isRegularFile() && dylibExtensions.stream().anyMatch(pathString::endsWith);
    }

    private static Collector<Path, List<Path>, ZonedDateTime> newestChange() {
        return Collector.of(LinkedList::new,
                List::add,
                (t, u) -> { t.addAll(u); return t; },
                (allPaths) -> allPaths.stream().map(CompileRustBridge::mtime).sorted(reverseOrder()).findFirst().orElse(EPOCH),
                CONCURRENT, UNORDERED);
    }

    private static ZonedDateTime mtime(Path path) {
        try {
            return Files.getLastModifiedTime(path).toInstant().atZone(ZoneId.systemDefault());
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            return EPOCH;
        }
    }

    private enum Os {
        MAC_OS("mac", "darwin") {
            @Override
            public String jnaArchString() {
                return "darwin";
            }
        },
        WINDOWS("win") {
            @Override
            public String jnaArchString() {
                if (currentIs64Bit()) {
                    return "win32-x86-64";
                } else {
                    return "win32-x86";
                }
            }
        },
        GNU_SLASH_LINUX("nux") {
            @Override
            public String jnaArchString() {
                if (currentIs64Bit()) {
                    return "linux-x86-64";
                } else {
                    return "linux-x86";
                }
            }
        },
        UNKNOWN() {
            @Override
            public String jnaArchString()  {
                throw new RuntimeException("Unknown platform. Can't tell what platform we're running on!");
            }
        };
        private final String[] substrings;

        private Os(String... substrings) {
            this.substrings = substrings;
        }

        public abstract String jnaArchString();

        public static Os getCurrent() {
            for (Os os : values()) {
                if (os.isCurrent()) {
                    return os;
                }
            }
            return UNKNOWN;
        }

        public boolean isCurrent() {
            return stream(substrings).anyMatch((substring) -> currentOsString().contains(substring));
        }

        private static boolean currentIs64Bit() {
            return System.getProperty("os.arch").contains("64");
        }

        private static String currentOsString() {
            return System.getProperty("os.name", "unknown").toLowerCase(Locale.ENGLISH);
        }
    }
}
