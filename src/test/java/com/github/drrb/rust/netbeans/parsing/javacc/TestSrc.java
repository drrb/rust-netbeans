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

import com.github.drrb.rust.antlr.RustBaseVisitor;
import com.github.drrb.rust.antlr.RustLexer;
import com.github.drrb.rust.antlr.RustParser;
import com.github.drrb.rust.netbeans.parsing.antlr.CommonRustTokenIDs;
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
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;

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
            public void dump(RustParser node, String prefix) {
            }
        };

        public void dump(RustParser node, String prefix) {
            node.crate().accept(new RustBaseVisitor<Void>(){
                int depth;
                @Override
                public Void visit(ParseTree tree) {
                    depth++;
                    char[] indent = new char[depth*2];
                    Arrays.fill(indent, ' ');
                    String ds = new String(indent);
                    System.out.println(ds + tree.getClass().getSimpleName() + " " + tree.getText());
                    super.visit(tree);
                    depth--;
                    return null;
                }
            });
            node.reset();
        }
    }

    public ParseResult parse(Dump dump) throws IOException {
        Path file = getPath();
        ParseResult result = withFile(file, reader -> {
            try {
                RustLexer lexer = new RustLexer(CharStreams.fromReader(reader));
                CommonTokenStream str = new CommonTokenStream(lexer, 0);
                str.fill();
                RustParser parser = new RustParser(str);
                dump.dump(parser, file.toString());
                return new ParseResult(parser);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        return result;
    }

    public List<String> readLines() throws IOException {
        return Files.readAllLines(path, UTF_8);
    }

    public TokenizationResult tokenize() {
        return withFile(path, reader -> {
            try {
                TokenizationResult result = new TokenizationResult();
                RustLexer lexer = new RustLexer(CharStreams.fromReader(reader));
                for (Token tok = lexer.nextToken(); tok.getType() != -1; tok=lexer.nextToken()) {
                    result.tokens.add(new TokenizationResult.Token(tok.getText(), CommonRustTokenIDs.forTokenType(tok.getType()),
                            tok.getLine(), tok.getCharPositionInLine()));
                }
                return result;
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    private <T> T withFile(Path file, Function<Reader, T> function) {
        try (FileInputStream inputStream = new FileInputStream(file.toFile())) {
            try (InputStreamReader reader = new InputStreamReader(inputStream, UTF_8)) {
                return function.apply(reader);
            }
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
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
