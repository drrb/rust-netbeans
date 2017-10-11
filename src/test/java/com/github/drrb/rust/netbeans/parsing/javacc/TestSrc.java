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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Path;
import java.util.function.Function;
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
        NO_DUMP {
            public void dump(SimpleNode node, String prefix) {
            }
        };

        public void dump(SimpleNode node, String prefix) {
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
    }

    public ParseResult parse(Dump dump) {
        Path file = getPath();
        RustParser.Result result;
        result = withFile(file, RustParser::parseFailOnError);

        dump.dump(result.rootNode(), "");

        return new ParseResult(result);
    }

    public TokenizationResult tokenize() {
        return withFile(path, reader -> {
            RustParserTokenManager tokenManager = new RustParserTokenManager(new SimpleCharStream(reader));
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
        });
    }

    private <T> T withFile(Path file, Function<Reader, T> function) {
        try (FileInputStream inputStream = new FileInputStream(file.toFile())) {
            try (InputStreamReader reader = new InputStreamReader(inputStream, UTF_8)) {
                return function.apply(reader);
            }
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

    @Override
    public boolean equals(Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
