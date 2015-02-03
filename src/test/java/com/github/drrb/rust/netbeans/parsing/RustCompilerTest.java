/*
 * Copyright (C) 2015 drrb
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
package com.github.drrb.rust.netbeans.parsing;

import java.io.File;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.nio.file.Files;
import java.util.List;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.iterableWithSize;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

/**
 *
 */
public class RustCompilerTest {

    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void shouldCompileFile() throws Exception {
        File file = tempFolder.newFile("test.rs");
        List<RustParseMessage> messages = new RustCompiler().compile(file, "fn main() { }");
        assertThat(messages, is(empty()));
    }

    @Test
    public void shouldGiveMessagesOnNonParseCompileErrors() throws Exception {
        File file = tempFolder.newFile("test.rs");
        // main() shouldn't return String, so we expect an error
        List<RustParseMessage> messages = new RustCompiler().compile(file, "fn main() -> String { }");
        assertThat(messages, is(iterableWithSize(1)));
    }
}
