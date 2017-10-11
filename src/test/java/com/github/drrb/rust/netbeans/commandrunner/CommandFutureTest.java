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

import com.github.drrb.rust.netbeans.test.PrintTestMethods;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.util.LinkedList;
import java.util.List;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;

public class CommandFutureTest {

    @Rule
    public final PrintTestMethods printTestMethods = new PrintTestMethods();
    private CommandFuture commandFuture;
    private CommandFutureListener listener;

    @Before
    public void setUp() {
        commandFuture = new HumbleCommandFuture();
        listener = new CommandFutureListener();
    }

    @Test
    public void shouldNotifyListenersOnStart() throws Exception {
        commandFuture.addListener(listener);
        commandFuture.processEvents();
        assertFalse(listener.notifiedOfStart());

        commandFuture.start();
        commandFuture.processEvents();
        assertTrue(listener.notifiedOfStart());
    }

    @Test
    public void shouldNotifyNewListenersAfterStart() throws Exception {
        commandFuture.start();
        commandFuture.addListener(listener);
        commandFuture.processEvents();
        assertTrue(listener.notifiedOfStart());
    }

    @Test
    public void shouldNotifyListenersOnFinish() throws Exception {
        commandFuture.addListener(listener);
        commandFuture.processEvents();
        assertFalse(listener.notifiedOfFinish());

        commandFuture.finish();
        commandFuture.processEvents();
        assertTrue(listener.notifiedOfFinish());
    }

    @Test
    public void shouldNotifyNewListenersAfterFinish() throws Exception {
        commandFuture.finish();
        commandFuture.addListener(listener);
        commandFuture.processEvents();
        assertTrue(listener.notifiedOfFinish());
    }

    @Test
    public void shouldNotifyListenersOnLinePrinted() throws Exception {
        commandFuture.addListener(listener);
        commandFuture.processEvents();
        assertThat(listener.linesPrinted, is(empty()));

        commandFuture.printLine("hello");
        commandFuture.processEvents();
        assertThat(listener.linesPrinted, contains("hello"));
    }

    @Test
    public void shouldNotifyNewListenersAfterLinesPrinted() throws Exception {
        commandFuture.printLine("hello");
        commandFuture.printLine("goodbye");
        commandFuture.processEvents();
        assertThat(listener.linesPrinted, is(empty()));

        commandFuture.addListener(listener);
        commandFuture.processEvents();
        assertThat(listener.linesPrinted, contains("hello", "goodbye"));
    }

    @Test
    public void shouldNotifyStartBeforeEndForLateListeners() throws Exception {
        commandFuture.start();
        commandFuture.printLine("hello");
        commandFuture.finish();
        commandFuture.addListener(listener);
        commandFuture.processEvents();
        assertThat(listener.notifications, is(3));
        assertThat(listener.startNotificationOrder, is(1));
        assertThat(listener.linePrintNotificationOrder, is(2));
        assertThat(listener.finishNotificationOrder, is(3));
    }

    @Test
    public void shouldSynchronouslyNotifyListenersAddedAfterEnd() throws Exception {
        commandFuture.start();
        commandFuture.printLine("hello");
        commandFuture.finish();
        commandFuture.processEvents();
        commandFuture.addListener(listener);
        assertThat(listener.notifications, is(3));
    }

    @Test
    public void shouldDeriveEventsFromInputStream() throws Exception {
        InputStream source = new ByteArrayInputStream("hello\ngoodbye".getBytes(UTF_8));

        commandFuture.addListener(listener);
        SynchronousInputStream stream = new SynchronousInputStream(commandFuture.wrap(source));

        assertFalse(listener.notifiedOfStart());
        stream.consume("h");
        assertTrue(listener.notifiedOfStart());
        stream.consume("ello");
        assertThat(listener.linesPrinted, is(empty()));
        stream.consume("\n");
        assertThat(listener.linesPrinted, contains("hello"));
        stream.consume("goodbye");
        assertFalse(listener.notifiedOfFinish());
        stream.read();
        assertTrue(listener.notifiedOfFinish());
    }

    private class CommandFutureListener extends CommandFuture.Listener {
        final List<String> linesPrinted = new LinkedList<>();
        int notifications = 0;
        int startNotificationOrder = 0;
        int linePrintNotificationOrder = 0;
        int finishNotificationOrder = 0;

        @Override
        public void onStart() {
            startNotificationOrder = ++notifications;
        }

        @Override
        public void onLinePrinted(String line) {
            linePrintNotificationOrder = ++notifications;
            linesPrinted.add(line);
        }

        @Override
        public void onFinish() {
            finishNotificationOrder = ++notifications;
        }

        public boolean notifiedOfStart() {
            return startNotificationOrder != 0;
        }

        public boolean notifiedOfFinish() {
            return finishNotificationOrder != 0;
        }
    }

    private class SynchronousInputStream extends InputStream {
        private final InputStream delegate;

        private SynchronousInputStream(InputStream delegate) {
            this.delegate = delegate;
        }

        @Override
        public int read() throws IOException {
            try {
                return delegate.read();
            } finally {
                try {
                    commandFuture.processEvents();
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

        public void consume(String toRead) throws IOException {
            for (byte expectedByte : toRead.getBytes(UTF_8)) {
                int actualByte = read();
                assertEquals(expectedByte, actualByte);
            }
        }
    }
}
