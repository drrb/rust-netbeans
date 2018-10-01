/*
 * Copyright (C) 2018 Tim Boudreau
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
package com.github.drrb.rust.antlr;

import com.github.drrb.rust.antlr.*;
import com.github.drrb.rust.netbeans.parsing.antlr.AntlrUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.BitSet;
import java.util.List;
import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Sanity checks the antlr parser.
 *
 * @author Tim Boudreau
 */
@Ignore("Used for grammar development")
public class TestAntlr {

    @Test
    public void testStruct() throws Throwable {
        String struct = "struct User {\n"
                + "  first_name: String,\n"
                + "  family_name: String,\n"
                + "  address: String,\n"
                + "  phone_num: String,\n"
                + "}";
        CommonTokenStream ts = new CommonTokenStream(new RustLexer(CharStreams.fromString(struct)), 0);
        ts.fill();
        RustParser rp = new RustParser(ts);

        rp.addErrorListener(new ANTLRErrorListener(){
            @Override
            public void syntaxError(Recognizer<?, ?> rcgnzr, Object o, int i, int i1, String string, RecognitionException re) {
                System.out.println("syntax error");
            }

            @Override
            public void reportAmbiguity(Parser parser, DFA dfa, int i, int i1, boolean bln, BitSet bitset, ATNConfigSet atncs) {
                System.out.println("ambiguity ");
            }

            @Override
            public void reportAttemptingFullContext(Parser parser, DFA dfa, int i, int i1, BitSet bitset, ATNConfigSet atncs) {
                System.out.println("fullContext");
            }

            @Override
            public void reportContextSensitivity(Parser parser, DFA dfa, int i, int i1, int i2, ATNConfigSet atncs) {
                System.out.println("contextSensitivity");
            }
        });

        rp.addParseListener(new RustBaseListener() {

            @Override
            public void enterBlock(RustParser.BlockContext ctx) {
                System.out.println("enter block ");
                super.enterBlock(ctx); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void enterIdent(RustParser.IdentContext ctx) {
                System.out.println("enter ident '" + ctx.getText() + "'");
            }

            @Override
            public void exitIdent(RustParser.IdentContext ctx) {
                System.out.println("exit ident '" + ctx.getText() + "'");
                super.exitIdent(ctx); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void visitErrorNode(ErrorNode node) {
                System.out.println("visit error node " + node.getText());
            }

            @Override
            public void enterStruct_decl(RustParser.Struct_declContext ctx) {
                System.out.println("en struct");
                super.enterStruct_decl(ctx); //To change body of generated methods, choose Tools | Templates.
            }
        });
        rp.crate().accept(new RustBaseVisitor<Void>() {
            @Override
            public Void visitErrorNode(ErrorNode node) {
                fail("Error node encountered: " + node);
                return super.visitErrorNode(node);
            }

            @Override
            public Void visitIdent(RustParser.IdentContext ctx) {
                System.out.println("IDENT " + ctx.getText());
                return super.visitIdent(ctx); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Void visitStruct_decl(RustParser.Struct_declContext ctx) {
                System.out.println("visit struct decl " + ctx.getText());
                unwind(ctx);
                return super.visitStruct_decl(ctx); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Void visit(ParseTree tree) {
                System.out.println("VISIT " + tree.getClass().getName());
                return super.visit(tree); //To change body of generated methods, choose Tools | Templates.
            }
        });
        System.out.println("done");
    }

    public static void unwind(ParserRuleContext ctx) {
        AntlrUtils.print(ctx);
    }

    @Test
    public void testTokenize() throws Throwable {
        for (String src : SOURCES) {
            CommonTokenStream ts = tokenize(src);
            ts.fill();
            List<org.antlr.v4.runtime.Token> toks = ts.getTokens();
//            System.out.println("TOK " + src + " toks " + toks.size());
            for (org.antlr.v4.runtime.Token t : toks) {
//                System.out.println("TOK " + RustLexer.VOCABULARY.getDisplayName(t.getType()) + " - " + t.getText());
//                System.out.println(t);
                assertNotNull(t);
            }
        }
    }

    @Test
    public void testParse() throws Throwable {
        for (String src : SOURCES) {
            System.out.println("\nPARSE " + src);
            RustParser rp = parse(src);
            ((CommonTokenStream) rp.getTokenStream()).fill();

            rp.addParseListener(new RustBaseListener() {

                @Override
                public void enterBlock(RustParser.BlockContext ctx) {
                    System.out.println("enter block ");
                    super.enterBlock(ctx); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public void enterIdent(RustParser.IdentContext ctx) {
                    System.out.println("enter ident " + ctx.getText());
                }

                @Override
                public void visitErrorNode(ErrorNode node) {
                    System.out.println("visit error node " + node.getText());
                }
            });
            rp.crate().accept(new RustBaseVisitor() {
                @Override
                public Object visitIdent(RustParser.IdentContext ctx) {
                    System.out.println("VISIT IDENT " + ctx.getText());
                    return super.visitIdent(ctx);
                }

                @Override
                public Object visitMod_decl(RustParser.Mod_declContext ctx) {
                    System.out.println("visitMod_decl " + ctx);
                    return super.visitMod_decl(ctx); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public Object visit(ParseTree tree) {
                    System.out.println("VISIT " + tree.getClass().getName());
                    return super.visit(tree); //To change body of generated methods, choose Tools | Templates.
                }

            });
        }
    }

    @Test
    public void sanityCheckTestFilesPresent() throws Throwable {
        for (String src : SOURCES) {
            String body = load(src);
            // XXX, why are these all empty?
            switch (src) {
                case "data/ClasspathSettingProjectOpenedHookTest/testrustproject/src/package/main.rs":
                case "data/crates/dependencies/src/orphan.rs":
                case "data/crates/dependencies/src/other/third.rs":
                case "data/crates/dependencies/src/toplevelmod.rs":
                case "data/crates/types/src/greetings.rs":
                case "data/crates/types/src/hello.rs":
                case "data/project/noname/src/main.rs":
                case "data/project/simple/src/dirmod/mod.rs":
                case "data/project/simple/src/main.rs":
                    continue;
            }
            assertTrue("Zero length: " + src, body.length() > 0);
        }
    }

    public static final CommonTokenStream tokenize(String scriptName) throws IOException {
        InputStream stream = locate(scriptName);
        ANTLRInputStream in = new ANTLRInputStream(stream);
        CommonTokenStream tokens = new CommonTokenStream(new RustLexer(in));
        return tokens;
    }

    public static final RustParser parse(String scriptName) throws IOException {
        RustParser p = new RustParser(tokenize(scriptName));
        return p;
    }

    public static String load(String filename) throws IOException {
        InputStream stream = locate(filename);
        return readString(stream, "UTF-8");
    }

    private static String readString(final InputStream in,
            final CharSequence encoding) throws IOException {
        try (Reader r = new BufferedReader(new InputStreamReader(in, encoding.toString()))) {
            return readString(r);
        }
    }

    /**
     * Reads all input from a reader into a string.
     *
     * @param in The input
     * @return The string
     * @throws IOException
     */
    private static String readString(final Reader in) throws IOException {
        final StringBuilder buffer = new StringBuilder(2_048);
        int value;

        while ((value = in.read()) != -1) {
            buffer.append((char) value);
        }

        return buffer.toString();
    }

    private static final Path DIRECTORY = Paths.get("src/test");

    private static InputStream locate(String location) throws IOException {
        Path pth = DIRECTORY.resolve(location);
        assertTrue(Files.exists(pth));
        return Files.newInputStream(pth, StandardOpenOption.READ);
    }

    static String[] SOURCES = new String[]{
        "data/ClasspathSettingProjectOpenedHookTest/testrustproject/src/package/main.rs",
        "data/compile/errorinotherfile/src/in_editor.rs",
        "data/compile/errorinotherfile/src/main.rs",
        "data/compile/siblingmoduleimport/src/imported.rs",
        "data/compile/siblingmoduleimport/src/in_editor.rs",
        "data/compile/siblingmoduleimport/src/main.rs",
        "data/compile/singlefile/src/main.rs",
        "data/crates/dependencies/src/main.rs",
        "data/crates/dependencies/src/orphan.rs",
        "data/crates/dependencies/src/other/mod.rs",
        "data/crates/dependencies/src/other/third.rs",
        "data/crates/dependencies/src/toplevelmod.rs",
        "data/crates/types/src/greetings.rs",
        "data/crates/types/src/hello.rs",
        "data/format/function.rs",
        "data/format/struct.rs",
        "data/indent/after_brace.rs",
        "data/indent/ignore_trailing_whitespace.rs",
        "data/indent/inside_block.rs",
        "data/indent/into_existing_function.rs",
        "data/index/project/struct/src/main.rs",
        "data/index/structs.rs",
        "data/parse/errors/errors_in_items.rs",
        "data/parse/errors/missing_parenthesis.rs",
        "data/parse/errors/two_errors.rs",
        "data/parse/javacc/blocks/blocks.rs",
        "data/parse/javacc/comments/block_comment.rs",
        "data/parse/javacc/comments/comments.rs",
        "data/parse/javacc/comments/doc_block_comment.rs",
        "data/parse/javacc/comments/doc_comment.rs",
        "data/parse/javacc/comments/inner_doc_block_comment.rs",
        "data/parse/javacc/comments/inner_doc_comment.rs",
        "data/parse/javacc/comments/line_comment.rs",
        "data/parse/javacc/const_items.rs",
        "data/parse/javacc/errors/errors_in_items.rs",
        "data/parse/javacc/errors/missing_parenthesis.rs",
        "data/parse/javacc/errors/two_errors.rs",
        "data/parse/javacc/escapes.rs",
        "data/parse/javacc/expressions/binary_operator_expressions.rs",
        "data/parse/javacc/expressions/binop_assignments.rs",
        "data/parse/javacc/expressions/expressions.rs",
        "data/parse/javacc/extern_crate_decls.rs",
        "data/parse/javacc/functions/functions.rs",
        "data/parse/javacc/functions/functions_and_semicolons.rs",
        "data/parse/javacc/if_then_else/if_statement.rs",
        "data/parse/javacc/if_then_else/if_then.rs",
        "data/parse/javacc/if_then_else/if_then_else.rs",
        "data/parse/javacc/if_then_else/if_then_elseif_else.rs",
        "data/parse/javacc/invalid_char.rs",
        "data/parse/javacc/literals/booleans.rs",
        "data/parse/javacc/literals/bytes.rs",
        "data/parse/javacc/literals/numbers.rs",
        "data/parse/javacc/literals/strings.rs",
        "data/parse/javacc/loops/for_loop.rs",
        "data/parse/javacc/loops/for_loop_enumerate.rs",
        "data/parse/javacc/loops/for_loop_labels.rs",
        "data/parse/javacc/loops/realistic_while_loop.rs",
        "data/parse/javacc/loops/simple_loop.rs",
        "data/parse/javacc/loops/while_loop.rs",
        "data/parse/javacc/macro_complex.rs",
        "data/parse/javacc/macro_simple.rs",
        "data/parse/javacc/modules.rs",
        "data/parse/javacc/ranges/range.rs",
        "data/parse/javacc/realistic_examples/guess.rs",
        "data/parse/javacc/realistic_examples/hello.rs",
        "data/parse/javacc/returns.rs",
        "data/parse/javacc/static_items.rs",
        "data/parse/javacc/structs.rs",
        "data/parse/javacc/tests/basic_test.rs",
        "data/parse/javacc/typecasts.rs",
        "data/parse/javacc/unit_expresions.rs",
        "data/parse/javacc/use_decls.rs",
        "data/parse/javacc/whitespace.rs",
        "data/project/noname/src/main.rs",
        "data/project/simple/src/dirmod/mod.rs",
        "data/project/simple/src/main.rs",
        "data/semantic/annotation.rs",
        "data/semantic/enum.rs",
        "data/semantic/function.rs",
        "data/semantic/impl.rs",
        "data/semantic/struct.rs",
        "data/semantic/trait.rs",
        "data/semantic/trait_impl.rs",};

}
