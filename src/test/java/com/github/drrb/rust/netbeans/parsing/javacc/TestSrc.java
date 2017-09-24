package com.github.drrb.rust.netbeans.parsing.javacc;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;

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

    public enum Dump {
        DUMP,
        NO_DUMP
    }

    public ParseResult parse(Dump dump) {
        Path file = getPath();
        RustParser.Result result;
        try (FileInputStream inputStream = new FileInputStream(file.toFile())) {
            RustParser parser = new RustParser(new SimpleCharStream(inputStream, UTF_8.name()));
            try {
                parser.Input();
            } catch (ParseException ex) {
            }
            result = parser.new Result();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (dump == Dump.DUMP) {
            dump(result.rootNode(), "");
        }

        return new ParseResult(result);
    }

    public TokenizationResult tokenize() {
        try (FileInputStream source = new FileInputStream(path.toFile())) {
            RustParserTokenManager tokenManager = new RustParserTokenManager(new SimpleCharStream(source));
            TokenizationResult result = new TokenizationResult();
            while (true) {
                RustToken token = (RustToken) tokenManager.getNextToken();
                token.withSpecialTokens().stream()
                        .map(TokenizationResult.Token::new)
                        .forEach(result.tokens::add);
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

    public ExpectedParseResultFile expectedParseResultFile() {
        return new ExpectedParseResultFile(path.getParent().resolve(path.getFileName() + ".expected_parse_result.json"));
    }

    private void dump(SimpleNode node, String prefix) {
        System.out.println(prefix + RustParserTreeConstants.jjtNodeName[node.id] + "[" + node.value + "]");
        if (node.children != null) {
            for (int i = 0; i < node.children.length; ++i) {
                SimpleNode n = (SimpleNode) node.children[i];
                if (n != null) {
                    dump(n, prefix + " ");
                }
            }
        }
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
