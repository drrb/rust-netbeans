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
            return Files.list(DIRECTORY).map(TestFile::new);
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
