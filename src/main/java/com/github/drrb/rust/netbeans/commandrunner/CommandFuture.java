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

package com.github.drrb.rust.netbeans.commandrunner;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.util.LinkedList;
import java.util.List;

//XXX: make me threadsafe!
public class CommandFuture {
    private final List<Listener> listeners = new LinkedList<>();
    private final List<String> lines = new LinkedList<>();
    private boolean started;
    private boolean finished;

    public static class Listener {
        public void onStart() {
        }

        public void onLinePrinted(String line) {
        }

        public void onFinish() {
        }
    }

    public InputStream wrap(final InputStream delegate) {
        return new InputStream() {

            private final ByteBuffer lineBuffer = ByteBuffer.allocate(1024);

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
        };
    }

    public void addListener(Listener listener) {
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

    void start() {
        started = true;
        for (Listener listener : listeners) {
            listener.onStart();
        }
    }

    void printLine(String line) {
        lines.add(line);
        for (Listener listener : listeners) {
            listener.onLinePrinted(line);
        }
    }

    void finish() {
        finished = true;
        for (Listener listener : listeners) {
            listener.onFinish();
        }
    }
}
