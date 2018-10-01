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

import com.github.drrb.rust.antlr.RustLexer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.regex.Pattern;
import org.antlr.v4.runtime.Vocabulary;

/**
 * Generic support for generating a set of token IDs from any Antlr 4
 * Vocabulary - not specific to Rust.
 *
 * @author Tim Boudreau
 */
public class AntlrTokenIDs implements Iterable<AntlrTokenID> {

    private static Map<Vocabulary, AntlrTokenIDs> mapping
            = new WeakHashMap<>();

    static synchronized AntlrTokenIDs forVocabulary(Vocabulary vocabulary, TokenCategorizer cat) {
        AntlrTokenIDs result = mapping.get(vocabulary);
        if (result == null) {
            result = new AntlrTokenIDs(vocabulary, cat);
            mapping.put(vocabulary, result);
        }
        return result;
    }

    private Map<String, AntlrTokenID> byName = new HashMap<>();
    private final AntlrTokenID[] ids;
    private final Map<Character, AntlrTokenID> byCharacter = new HashMap<>();
    private final Map<String, AntlrTokenID> bySymbolicName = new HashMap<>();

    AntlrTokenIDs(Vocabulary vocabulary, TokenCategorizer categorizer) {
        int end = vocabulary.getMaxTokenType() + 1;
        ids = new AntlrTokenID[end];
        for (int i = 0; i < end; i++) {
            String displayName = stripSingleQuotes(vocabulary.getDisplayName(i));
            String symName = stripSingleQuotes(vocabulary.getSymbolicName(i));
            String litName = stripSingleQuotes(vocabulary.getLiteralName(i));
            String category = categorizer.categoryFor(i, displayName,
                    symName, litName);
            ids[i] = new AntlrTokenID(i, litName, displayName, symName, category);
            if (litName != null) {
                byName.put(litName, ids[i]);
                if (litName.length() == 1) {
                    byCharacter.put(litName.charAt(0), ids[i]);
                }
            }
            if (symName != null) {
                bySymbolicName.put(symName, ids[i]);
            }
        }
    }

    public AntlrTokenID forSymbolicName(String name) {
        if ("EOF".equals(name)) {
            return AntlrTokenID.EOF;
        }
        return bySymbolicName.get(name);
    }

    public AntlrTokenID forSymbol(char symbol) {
        if (-1 == symbol) {
            return AntlrTokenID.EOF;
        }
        return byCharacter.get(symbol);
    }

    static String stripSingleQuotes(String s) {
        if (s != null && s.length() > 1) {
            char a = s.charAt(0);
            char b = s.charAt(s.length() -1);
            if (a == '\'' && b == '\'') {
                s = s.substring(1, s.length()-1);
            }
        }
        return s;
    }

    public AntlrTokenID get(int tokenType) {
        if (-1 == tokenType) {
            return AntlrTokenID.EOF;
        }
        assert tokenType >= 0 && tokenType < ids.length : "Invalid token type " + tokenType;
        return ids[tokenType];
    }

    public AntlrTokenID get(String literalName) {
        if ("EOF".equals(literalName)) {
            return AntlrTokenID.EOF;
        }
        AntlrTokenID result = byName.get(literalName);
        if (result == null) {
            throw new IllegalArgumentException("No token with symbolic name " + literalName);
        }
        return result;
    }

    public int size() {
        return ids.length;
    }

    public List<AntlrTokenID> all() {
        return Arrays.asList(ids);
    }

    public Iterator<AntlrTokenID> iterator() {
        return new ArrayIterator<>(ids);
    }

    private static final class ArrayIterator<T> implements Iterator<T> {

        private final T[] arr;
        private int index = -1;

        public ArrayIterator(T[] arr) {
            this.arr = arr;
        }

        @Override
        public boolean hasNext() {
            return index < arr.length - 1;
        }

        @Override
        public T next() {
            return arr[++index];
        }
    }

    public static void main(String[] args) {
        TokenCategorizer cat = new TokenCategorizer() {
            private final Pattern WORD = Pattern.compile("^[a-zA-Z]+$");
            @Override
            public String categoryFor(int tokenType, String displayName, String symbolicName, String literalName) {
                if (tokenType == 0) {
                    return "eof";
                }
                System.out.println("DN " + displayName + " SN " + symbolicName + " LN " + literalName + " " + tokenType);
                if (literalName != null && WORD.matcher(literalName).lookingAt()) {
                    return "keyword";
                } else if (literalName != null && literalName.length() == 1 && !Character.isAlphabetic(literalName.charAt(0))) {
                    switch(literalName.charAt(0)) {
                        case '*':
                        case '/':
                        case '%':
                        case '+':
                        case '-':
                        case '^':
                        case '|':
                        case '&':
                            return "operator";
                        case '.' :
                        case '{':
                        case '}':
                        case '(':
                        case ')':
                        case '[':
                        case ']':
                        case ',':
                            return "delimiter";
                        case '<' :
                        case '>' :
                            return "comparisonOperator";
                        case '=' :
                            return "assignmentOperator";
                    }
                    return "symbol";
                } else if (literalName != null && literalName.length() == 2 && !Character.isAlphabetic(literalName.charAt(0)) && !Character.isAlphabetic(literalName.charAt(1))) {
                    switch(literalName) {
                        case "::" :
                        case "=>":
                            return "delimiter";
                        case "+=" :
                        case "-=" :
                        case "/=" :
                        case "*=" :
                        case "%=" :
                        case "|=" :
                        case "&=" :
                        case "^=" :
                            return "assignmentOperator";
                        case "==":
                            return "comparisonOperator";

                    }
                    return "symbol";
                } else if (literalName != null && literalName.length() == 3 && !Character.isAlphabetic(literalName.charAt(0)) && !Character.isAlphabetic(literalName.charAt(1)) && !Character.isAlphabetic(literalName.charAt(2))) {
                    switch(literalName) {
                        case "<<=" :
                        case ">>=" :
                            return "assignmentOperator";
                    }
                    return "symbol";
                } else if (symbolicName != null) {
                    if (symbolicName.endsWith("Comment")) {
                        return "comment";
                    } else if (symbolicName.endsWith("Lit")) {
                        return "literal";
                    }
                    switch(symbolicName) {
                        case "Lifetime" :
                            return "keyword";
                        case "Whitespace":
                            return "whitespace";
                        case "Ident":
                            return "identifier";
                    }
                }
                return "other";
            }
        };
        AntlrTokenIDs ids = new AntlrTokenIDs(RustLexer.VOCABULARY, cat);
        for (AntlrTokenID id : ids) {
            System.out.println(id.primaryCategory() + "\t\t" + id.name());
        }
    }
}
