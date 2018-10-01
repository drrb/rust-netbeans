/**
 * Copyright (C) 2018 Tim Boudreau
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
package com.github.drrb.rust.netbeans.parsing.antlr;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.misc.Interval;
import org.netbeans.spi.lexer.LexerInput;

public class AntlrStreamAdapter implements CharStream {

    private final String name;
    private int index = 0;
    private int mark = 0;
    private final LexerInput input;

    public AntlrStreamAdapter(LexerInput input, String name) {
        this.input = input;
        this.name = name;
    }

    @Override
    public int index() {
        return index;
    }

    @Override
    public int size() {
        return -1;
    }

    @Override
    public String getSourceName() {
        return name;
    }

    @Override
    public void consume() {
        int character = read();
        if ( character == EOF ) {
            backup( 1 );
            throw new IllegalStateException( "Attempting to consume EOF" );
        }
    }

    @Override
    public int LA(int lookaheadAmount) {
        if ( lookaheadAmount < 0 ) {
            return lookBack( -lookaheadAmount );
        } else if ( lookaheadAmount > 0 ) {
            return lookAhead( lookaheadAmount );
        } else {
            return 0;
        }
    }

    private int lookBack(int amount) {
        backup( amount );
        int character = read();
        for ( int i = 1; i < amount; i++ ) {
            read();
        }
        return character;
    }

    private int lookAhead(int amount) {
        int character = 0;
        for ( int i = 0; i < amount; i++ ) {
            character = read();
        }
        backup( amount );
        return character;
    }

    @Override
    public int mark() {
        return ++mark;
    }

    @Override
    public void release(int marker) {
        mark = marker;
        mark--;
    }

    @Override
    public void seek(int index) {
        if ( index < 0 ) {
            throw new IllegalArgumentException( String.format( "Invalid index (%s < 0)", index ) );
        }

        if ( index < this.index ) {
            backup( this.index - index );
            return;
        }
        while ( this.index < index ) {
            consume();
        }
    }

    private int read() {
        int result = input.read();
        index++;

        if ( result == LexerInput.EOF ) {
            return EOF;
        } else {
            return result;
        }
    }

    private void backup(int count) {
        input.backup( count );
        index -= count;
    }

    @Override
    public String getText(Interval interval) {
        int start = interval.a;
        int stop = interval.b;

        if ( start < 0 || stop < start ) {
            return "";
        }

        final int pos = this.index;
        final int length = interval.length();
        final char[] data = new char[length];

        seek( interval.a );
        int r = 0;
        while ( r < length ) {
            final int character = read();
            if ( character == EOF ) {
                break;
            }

            data[r] = (char) character;
            r++;
        }
        seek( pos );

        if ( r > 0 ) {
            return new String( data, 0, r );
        } else {
            return "";
        }
    }
}
