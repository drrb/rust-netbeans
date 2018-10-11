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

import com.github.drrb.rust.netbeans.configuration.RustConfiguration;
import com.github.drrb.rust.netbeans.test.PrintTestMethods;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 *
 */
@Ignore
public class RustCompilerTest {

    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();
    @Rule
    public final PrintTestMethods printTestMethods = new PrintTestMethods();

    @Test
    public void shouldCompileStringToExecutable() throws Exception {
        File file = tempFolder.newFile("test.rs");
        List<RustParseMessage> messages = new RustCompiler().compile(file, "fn main() { }", file, RustConfiguration.get().getLibrariesPaths());
        assertThat(messages, is(empty()));
    }

    @Test
    public void shouldGiveMessagesOnNonParseCompileErrors() throws Exception {
        File file = tempFolder.newFile("test.rs");
        // main() shouldn't return String, so we expect an error
        List<RustParseMessage> messages = new RustCompiler().compile(file, "fn main() -> String { }", file, RustConfiguration.get().getLibrariesPaths());
        assertThat(messages, hasSize(1));
    }

    @Test
    public void shouldCompileMultiFileCrate() throws Exception {
        File mainFile = tempFolder.newFile("main.rs");
        File modFile = tempFolder.newFile("other.rs");
        Files.write(modFile.toPath(), "pub fn other_function() { }".getBytes(UTF_8));
        List<RustParseMessage> messages = new RustCompiler().compile(mainFile, "mod other;\nfn main() { other::other_function() }", mainFile, RustConfiguration.get().getLibrariesPaths());
        assertThat(messages, is(empty()));
    }

    @Test
    public void shouldOnlyReturnMessagesFromTargetFile() throws Exception {
        File mainFile = tempFolder.newFile("main.rs");
        File modFile = tempFolder.newFile("other.rs");
        Files.write(modFile.toPath(), "pub fn other_function() { x x }".getBytes(UTF_8));
        List<RustParseMessage> messages = new RustCompiler().compile(mainFile, "mod other;\nfn main() { other::other_function() }", mainFile, RustConfiguration.get().getLibrariesPaths());
        assertThat(messages, is(empty()));
    }
}
