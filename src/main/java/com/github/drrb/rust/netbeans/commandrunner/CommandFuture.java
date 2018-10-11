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

import com.google.common.annotations.VisibleForTesting;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class CommandFuture {
    private final ExecutorService eventThread = Executors.newSingleThreadExecutor();
    protected final BlockingQueue<Event> eventQueue = new LinkedBlockingQueue<>();
    private final List<Listener> listeners = new LinkedList<>();
    private final List<String> lines = new LinkedList<>();
    private volatile boolean started = false;
    private volatile boolean finished = false;

    public static class Listener {
        public void onStart() {
        }

        public void onLinePrinted(String line) {
        }

        public void onFinish() {
        }
    }

    public InputStream wrap(InputStream delegate) {
        startEventThread();
        return new CommandInputStream(delegate);
    }

    public void addListener(Listener listener) {
        ListenerAdded event = new ListenerAdded(listener);
        if (finished) {
            event.process();
        } else {
            registerEvent(event);
        }
    }

    @VisibleForTesting
    void start() {
        registerEvent(new CommandStarted());
    }

    @VisibleForTesting
    void printLine(String line) {
        registerEvent(new LinePrinted(line));
    }

    @VisibleForTesting
    void finish() {
        registerEvent(new CommandFinished());
    }

    @VisibleForTesting
    protected void startEventThread() {
        eventThread.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                processEvents(); return null;
            }
        });
    }

    @VisibleForTesting
    protected void processEvents() throws InterruptedException {
        while (true) {
            Event nextEvent = eventQueue.take();
            nextEvent.process();
            if (finished) {
                eventThread.shutdown();
                return;
            }
        }
    }

    private void registerEvent(Event event) {
        eventQueue.add(event);
    }

    @VisibleForTesting
    protected interface Event {
        void process();
    }

    private class ListenerAdded implements Event {
        private final Listener listener;

        public ListenerAdded(Listener listener) {
            this.listener = listener;
        }

        @Override
        public void process() {
            if (started) {
                listener.onStart();
            }
            for (String line : lines) {
                listener.onLinePrinted(line);
            }
            if (finished) {
                listener.onFinish();
            }
            listeners.add(listener);
        }
    }

    private class CommandStarted implements Event {
        @Override
        public void process() {
            started = true;
            for (Listener listener : listeners) {
                listener.onStart();
            }
        }
    }

    private class LinePrinted implements Event {
        private final String line;
        public LinePrinted(String line) {
            this.line = line;
        }
        @Override
        public void process() {
            lines.add(line);
            for (Listener listener : listeners) {
                listener.onLinePrinted(line);
            }
        }
    }

    private class CommandFinished implements Event {
        @Override
        public void process() {
            finished = true;
            for (Listener listener : listeners) {
                listener.onFinish();
            }
        }
    }

    private class CommandInputStream extends InputStream {
        private final InputStream delegate;
        private final ByteBuffer lineBuffer = ByteBuffer.allocate(1024);

        public CommandInputStream(InputStream delegate) {
            this.delegate = delegate;
        }

        @Override
        public int read() throws IOException {
            if (!started) {
                start();
            }
            int nextByte = delegate.read();
            if (nextByte == '\n') {
                printLine(consumeCurrentLine());
            } else if (nextByte == -1 && !finished) {
                finish();
            } else {
                appendToCurrentLine(nextByte);
            }
            return nextByte;
        }

        private void appendToCurrentLine(int aByte) {
            lineBuffer.put((byte) aByte);
        }

        private String consumeCurrentLine() {
            lineBuffer.flip();
            try {
                return UTF_8.decode(lineBuffer).toString();
            } finally {
                lineBuffer.clear();
            }
        }
    }
}
