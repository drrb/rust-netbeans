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
package com.github.drrb.rust.netbeans.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import static java.nio.charset.StandardCharsets.UTF_8;
import org.openide.util.Exceptions;

/**
 *
 */
public class Pipe implements Runnable {

    private final BufferedReader input;
    private final PrintWriter output;

    public static Pipe between(InputStream in, Writer out) {
        return new Pipe(new InputStreamReader(in, UTF_8), out);
    }

    public static Pipe between(Reader in, OutputStream out) {
        return new Pipe(in, new OutputStreamWriter(out, UTF_8));
    }

    public Pipe(Reader stdoutReader, Writer stdoutWriter) {
        this.input = new BufferedReader(stdoutReader);
        this.output = new PrintWriter(stdoutWriter);
    }

    @Override
    public void run() {
        try {
            transferAllLines();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void transferAllLines() throws IOException {
        for (String line = input.readLine(); line != null; line = input.readLine()) {
            output.println(line);
        }
    }
}
