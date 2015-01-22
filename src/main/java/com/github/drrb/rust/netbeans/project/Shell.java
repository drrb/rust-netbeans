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
package com.github.drrb.rust.netbeans.project;

import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import static java.nio.charset.StandardCharsets.UTF_8;
import javax.swing.AbstractAction;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.LifecycleManager;
import org.openide.execution.ExecutionEngine;
import org.openide.execution.ExecutorTask;
import org.openide.util.Cancellable;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;
import org.openide.windows.IOColorLines;
import org.openide.windows.IOColors;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

public class Shell {

    private static final RequestProcessor EXECUTOR = new RequestProcessor("Cargo runner", 16);

    public void run(String commandLine, File workingDir) {
        LifecycleManager.getDefault().saveAll();
        InputOutput io = IOProvider.get("cargo").getIO("Cargo", false);
        ExecutorTask task = ExecutionEngine.getDefault().execute("cargo", () -> {
            try {
                runAndWatch(commandLine, workingDir, io);
            } catch (InterruptedException ex) {
                // Do nothing
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }, io);
    }

    public void runAndWatch(String commandLine, File workingDir, InputOutput io) throws InterruptedException, IOException {
        ProcessBuilder processBuilder = new ProcessBuilder()
                .command("/bin/bash", "-lc", commandLine)
                .directory(workingDir);
        Process process = processBuilder.start();
        ProgressHandle progressHandle = ProgressHandleFactory.createHandle("Cargo", new Cancellable() {

            @Override
            public boolean cancel() {
                process.destroy();
                return true;
            }
        }, new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                io.select();
            }
        });
        progressHandle.start();

        try {
            if (IOColorLines.isSupported(io)) {
                IOColorLines.println(io, commandLine, IOColors.getColor(io, IOColors.OutputType.LOG_DEBUG));
            } else {
                io.getOut().println(commandLine);
            }

            EXECUTOR.post(new Pipe(new InputStreamReader(process.getInputStream(), UTF_8), io.getOut()));
            EXECUTOR.post(new Pipe(new InputStreamReader(process.getErrorStream(), UTF_8), io.getErr()));
            EXECUTOR.post(new Pipe(io.getIn(), new OutputStreamWriter(process.getOutputStream(), UTF_8)));
            process.waitFor();
        } finally {
            progressHandle.finish();
            io.getOut().close();
            io.getErr().close();
            io.getIn().close();
        }
    }

    private static class Pipe implements Runnable {

        private final BufferedReader input;
        private final Writer output;

        public Pipe(Reader stdoutReader, Writer stdoutWriter) {
            this.input = new BufferedReader(stdoutReader);
            this.output = stdoutWriter;
        }

        @Override
        public void run() {
            try {
                for (String line = input.readLine(); line != null; line = input.readLine()) {
                    output.append(line).append("\n");
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
