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
package com.github.drrb.rust.netbeans.rustbridge;

import org.junit.*;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.nio.file.Files;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 *
 */
@Ignore("This is for the old native parser")
public class RustParserTest {

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();
    private RustParser parser;
    private RustParser.Result result;

    @Before
    public void setUp() {
        parser = new RustParser();
    }

    @After
    public void tearDown() {
        result.destroy();
    }

    @Test
    public void shouldParseValidSource() throws Exception {
        File file = temporaryFolder.newFile("test.rs");
        result = parser.parse(file, "fn main() {}");
        assertTrue(result.isSuccess());
        assertThat(result.getAst(), not(nullValue()));
        assertThat(result.getParseMessages(), is(empty()));
    }

    @Test
    public void shouldHaveErrorsWhenSourceIsInvalid() throws Exception {
        File file = temporaryFolder.newFile("test.rs");
        result = parser.parse(file, "fn main() { 1 2 }");
        assertFalse(result.isSuccess());
        assertThat(result.getAst(), is(nullValue()));
        assertThat(result.getParseMessages(), not(empty()));
    }

    @Test
    public void shouldNotDieWhenThereAreObviousSyntaxErrors() throws Exception {
        File file = temporaryFolder.newFile("test.rs");
        result = parser.parse(file, "fn main() {");
        assertFalse(result.isSuccess());
        assertThat(result.getAst(), is(nullValue()));

        RustParseMessage firstMessage = result.getParseMessages().get(0);
        assertThat(firstMessage.getLevel(), is(RustParseMessage.Level.HELP));
        assertThat(firstMessage.getStartLine(), is(1));
        assertThat(firstMessage.getStartCol(), is(10));
        assertThat(firstMessage.getEndLine(), is(1));
        assertThat(firstMessage.getEndCol(), is(11));
        assertThat(firstMessage.getMessage(), is("did you mean to close this delimiter?"));
    }

    @Test
    public void shouldParseSourceAcrossTwoFiles() throws Exception {
        File firstFile = temporaryFolder.newFile("test.rs");
        File secondFile = temporaryFolder.newFile("other.rs");
        Files.write(secondFile.toPath(), "pub fn other() {}".getBytes(UTF_8));
        result = parser.parse(firstFile, "mod other;\nfn main() { other::other(); }");
        assertTrue(result.isSuccess());
        assertThat(result.getAst(), not(nullValue()));
        assertThat(result.getParseMessages(), is(empty()));
    }

    @Test
    public void shouldOnlyReturnMessagesFromTargetFile() throws Exception {
        File firstFile = temporaryFolder.newFile("test.rs");
        File secondFile = temporaryFolder.newFile("other.rs");
        Files.write(secondFile.toPath(), "pub fn other() { x x }".getBytes(UTF_8));
        result = parser.parse(firstFile, "mod other;\nfn main() { other::other(); }");
        assertTrue(result.isFailure());
        assertThat(result.getParseMessages(), is(empty()));
    }
}
