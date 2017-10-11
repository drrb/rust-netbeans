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
package com.github.drrb.rust.netbeans.parsing.javacc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class TestFile implements Comparable<TestSrc> {
    public static final Path DIRECTORY = Paths.get("src/test/data/parse/javacc");
    protected final Path path;

    public TestFile(Path path) {
        this.path = path;
    }

    public static TestFile get(Path file) {
        return new TestFile(DIRECTORY.resolve(file));
    }

    public static Stream<? extends TestFile> all() {
        try {
            return Files.walk(DIRECTORY).map(TestFile::new);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Path getPath() {
        return path;
    }

    public boolean exists() {
        return Files.exists(path);
    }

    @Override
    public String toString() {
        return path.getFileName().toString();
    }

    @Override
    public int compareTo(TestSrc other) {
        return this.path.compareTo(other.path);
    }
}
