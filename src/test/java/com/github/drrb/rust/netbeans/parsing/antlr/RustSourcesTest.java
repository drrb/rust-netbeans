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
package com.github.drrb.rust.netbeans.parsing.antlr;

import com.github.drrb.rust.netbeans.test.NetbeansWithRust;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.antlr.v4.runtime.Token;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.netbeans.modules.csl.api.Error;

/**
 *
 * @author Tim Boudreau
 */
@Ignore("Grammar needs further work")
public class RustSourcesTest {

    private static File[] files;

    @Rule
    public NetbeansWithRust netbeansWithRust = new NetbeansWithRust();

    @BeforeClass
    public static void setup() {
        System.setProperty("rust.antlr.full.messages", "true");
        File basedir = new File(System.getProperty("basedir"));
        File dir = new File(basedir, "tmp/rust");
        System.out.println("DIR IS " + dir);
        assertTrue(dir.exists());
        assertTrue(dir.isDirectory());
        files = dir.listFiles((File dir1, String name) -> name.endsWith(".rs"));
    }

    @Test
    public void test() throws Exception {
        StringBuilder sb = new StringBuilder();
        final AtomicInteger count = new AtomicInteger();
        for (int i = 0; i < files.length; i++) {
            testOneFile(files[i], sb, count);
        }
        if (sb.length() > 0) {
            sb.insert(0, count.get() + " of " + files.length + " failed to parse\n\n");
            fail(sb.toString());
        }
    }

    private void testOneFile(File f, StringBuilder sb, AtomicInteger count) throws Exception {
        netbeansWithRust.parse(f, result -> {
            System.out.println(result);
            if (!result.getDiagnostics().isEmpty()) {
                count.getAndIncrement();
                for (Error e : result.getDiagnostics()) {
                    extractError(e, f, sb);
                    break;
                }
//                fail("Error parsing " + f + ": " + sb);
            }
        });
    }

    private static String join(Collection<String> lines) {
        StringBuilder sb = new StringBuilder();
        for (String s : lines) {
            sb.append(s).append('\n');
        }
        return sb.toString();
    }

    private String extractError(Error err, File file, StringBuilder sb) throws IOException {
        List<String> lines = Files.readAllLines(file.toPath());
        int errPos = err.getStartPosition();
        int errEnd = err.getEndPosition();
        Token offendingToken = null;
        int lineStart = -1;
        int startOffset = -1;
        int targetLine = -1;
        int endLine = -1;
        int endOffset = 0;
        if (!(err instanceof SyntaxError)) {
            sb.append("\n---------------------------------------------------\n");
            sb.append(file.getName());
            sb.append('\n').append(err.getDescription());
            com.github.drrb.rust.antlr.RustLexer lexer = RustAntlrLexer.fromString(join(lines));
            for (Token t = lexer.nextToken(); t.getType() != -1; t = lexer.nextToken()) {
                if (lexer.getCharIndex() > errPos) {
                    offendingToken = t;
                    break;
                }
            }
            if (offendingToken != null) {
                sb.append("\nOffendingToken: ").append(CommonRustTokenIDs.forTokenType(offendingToken.getType()));
            }
            sb.append(" @").append(errPos).append(':').append(errEnd)
                    .append(" line ").append(targetLine).append(" char ").append(startOffset).append("\n\n");
        } else {
            sb.append("\n---------------------------------------------------\n");
            sb.append(file.getName()).append("\n");
            SyntaxError serr = (SyntaxError) err;
            lineStart = serr.line();
            startOffset = serr.charPositionInLine();
            sb.append(serr);
            endOffset = startOffset + (serr.getEndPosition() - serr.getStartPosition());
        }
        if (targetLine == -1) {
            lineStart = 0;
            startOffset = 0;
            int[] starts = new int[lines.size()];
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                int chars = line.length() + 1;
                if (errPos > lineStart && errPos <= lineStart + chars) {
                    targetLine = i;
                    startOffset = errPos - lineStart;
                }
                if (errEnd > lineStart && errEnd <= lineStart + chars) {
                    endLine = i;
                    endOffset = errEnd - lineStart;
                    break;
                }
                starts[i] = lineStart;
                lineStart += chars;
            }
        }
        if (targetLine == -1 || endLine == -1) {
//            fail("Error not found");
            sb.append("Did not find start line of error");
            return sb.toString();
        }
        for (int i = Math.max(0, targetLine - 3); i < Math.min(targetLine + 3, lines.size()); i++) {
            String line = lines.get(i);
            sb.append(line).append('\n');
            if (i == targetLine) {
                char[] c = new char[startOffset];
                Arrays.fill(c, ' ');
                sb.append(c);
                char[] fill = new char[Math.max(1, endOffset - startOffset)];
                Arrays.fill(fill, '^');
                sb.append(fill).append('\n');
            }
        }
        tokenizeLines(file, Math.max(0, targetLine - 3), Math.min(targetLine + 2, lines.size()), sb);
        return sb.toString();
    }

    private void tokenizeLines(File f, int startLine, int endLine, StringBuilder into) throws IOException {
        assert startLine <= endLine;
        List<String> lines = Files.readAllLines(f.toPath());
        int currLine = -1;
        com.github.drrb.rust.antlr.RustLexer lexer = RustAntlrLexer.fromString(join(lines));
        for (Token t = lexer.nextToken(); t.getType() != -1; t = lexer.nextToken()) {
            int ln = t.getLine();
            if (ln < startLine) {
                continue;
            }
            if (ln > endLine) {
                break;
            }
            if (currLine != ln) {
                into.append("\n");
                currLine = ln;
            }
            AntlrTokenID id = CommonRustTokenIDs.forTokenType(t.getType());
            if (id == CommonRustTokenIDs.whitespaceTokenID()) {
                continue;
            }
            into.append("'").append(t.getText()).append("'")
                    .append('(').append(id.name()).append(')').append(' ');
        }
    }
}
