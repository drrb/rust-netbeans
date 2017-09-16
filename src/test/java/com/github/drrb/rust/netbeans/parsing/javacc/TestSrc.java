package com.github.drrb.rust.netbeans.parsing.javacc;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Stack;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class TestSrc extends TestFile {

    public static TestSrc get(TestFile file) {
        return new TestSrc(file.path);
    }

    public static TestSrc named(String fileName) {
        return all().filter(f -> f.path.getFileName().toString().equals(fileName)).findFirst().get();
    }

    public static Stream<TestSrc> all() {
        return TestFile.all()
                .map(TestFile::getPath)
                .filter(isRustFile())
                .map(TestSrc::new);
    }

    private TestSrc(Path path) {
        super(path);
    }

    private static Predicate<Path> isRustFile() {
        return p -> p.toString().endsWith(".rs");
    }

    public ParseResult tokenize() {
        try (FileInputStream source = new FileInputStream(path.toFile())) {
            RustParserTokenManager tokenManager = new RustParserTokenManager(new SimpleCharStream(source));
            ParseResult result = new ParseResult();
            while (true) {
                RustToken token = (RustToken) tokenManager.getNextToken();
                Stack<RustToken> tokens = new Stack<>();
                tokens.push(token);
                while (token.hasSpecialToken()) {
                    tokens.push(token.specialToken());
                    token = token.specialToken();
                }
                while (!tokens.empty()) {
                    token = tokens.pop();
                    result.tokens.add(new ParseResult.Token(token.image, token.kind(), token.beginLine, token.beginColumn));
                }
                if (token.isEof()) {
                    break;
                }
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ExpectedTokensFile expectedTokensFile() {
        return new ExpectedTokensFile(path.getParent().resolve(path.getFileName() + ".expected_tokens.json"));
    }

    @Override
    public boolean equals(Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
