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
package com.github.drrb.rust.netbeans.commandrunner;

import com.github.drrb.rust.netbeans.util.IoColorLines;
import com.github.drrb.rust.netbeans.util.Pipe;
import com.github.drrb.rust.netbeans.util.Untested;
import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.swing.AbstractAction;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.LifecycleManager;
import org.openide.execution.ExecutionEngine;
import org.openide.execution.ExecutorTask;
import org.openide.util.Cancellable;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.util.TaskListener;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

@Untested(excuses = "Lots of UI stuff")
public class CommandRunnerUi {

    private static final RequestProcessor EXECUTOR = new RequestProcessor("CommandRunnerUi", 12);

    public static class Factory {
        public CommandRunnerUi get(String name) {
            InputOutput io = IOProvider.get(name).getIO(name, false);
            return new CommandRunnerUi(name, io);
        }
    }

    private final String name;
    private final InputOutput io;

    protected CommandRunnerUi(String name, InputOutput io) {
        this.name = name;
        this.io = io;
    }

    public void printText(String command) {
        IoColorLines.printDebug(io, command);
    }

    public CommandFuture runAndWatch(ProcessBuilder processBuilder) {
        LifecycleManager.getDefault().saveAll();
        WatchingProcessRunner processRunner = new WatchingProcessRunner(processBuilder, io);
        ExecutorTask task = ExecutionEngine.getDefault().execute(processBuilder.command().toString(), processRunner, io);
        task.addTaskListener(new CleanUpStreamsWhenFinished(io));
        return processRunner.future;
    }

    private class WatchingProcessRunner implements Runnable {

        private final ProcessBuilder processBuilder;
        private final InputOutput io;
        final CommandFuture future = new CommandFuture();

        WatchingProcessRunner(ProcessBuilder processBuilder, InputOutput io) {
            this.processBuilder = processBuilder;
            this.io = io;
        }

        @Override
        public void run() {
            try {
                Process process = processBuilder.start();
                watch(process);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        public void watch(Process process) {
            ProgressHandle progressHandle = ProgressHandleFactory.createHandle(name, new KillOnCancel(process), new SelectOnClick(io));
            progressHandle.start();

            try {
                //TODO: wait for these?
                EXECUTOR.post(Pipe.between(future.wrap(process.getInputStream()), io.getOut()));
                EXECUTOR.post(Pipe.between(process.getErrorStream(), io.getErr()));
                EXECUTOR.post(Pipe.between(io.getIn(), process.getOutputStream()));
                process.waitFor();
            } catch (InterruptedException ex) {
                process.destroy();
            } finally {
                progressHandle.finish();
            }
        }

        private class KillOnCancel implements Cancellable {

            private final Process process;

            KillOnCancel(Process process) {
                this.process = process;
            }

            @Override
            public boolean cancel() {
                process.destroy();
                return true;
            }
        }

        private class SelectOnClick extends AbstractAction {

            private final InputOutput io;

            private SelectOnClick(InputOutput io) {
                this.io = io;
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                io.select();
            }
        }
    }

    private class CleanUpStreamsWhenFinished implements TaskListener {

        private final InputOutput io;

        CleanUpStreamsWhenFinished(InputOutput io) {
            this.io = io;
        }

        @Override
        public void taskFinished(Task task) {
            io.getOut().append("\n");
            io.getOut().close();
            io.getErr().close();
            try {
                io.getIn().close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
