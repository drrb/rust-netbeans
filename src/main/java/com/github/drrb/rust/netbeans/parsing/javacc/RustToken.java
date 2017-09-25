package com.github.drrb.rust.netbeans.parsing.javacc;

import com.github.drrb.rust.netbeans.parsing.RustTokenId;
import org.netbeans.modules.csl.api.OffsetRange;

import java.util.LinkedList;
import java.util.List;

import static com.github.drrb.rust.netbeans.parsing.RustTokenId.EOF;

public class RustToken extends Token {
    private final RustTokenId enumKind;

    public RustToken(int kind, String image) {
        this.kind = kind;
        this.enumKind = RustTokenId.get(kind);
        this.image = image;
    }

    public boolean isEof() {
        return enumKind == EOF;
    }

    public RustTokenId kind() {
        return enumKind;
    }

    public RustTokenId id() {
        return kind();
    }

    public RustToken specialToken() {
        return (RustToken) specialToken;
    }

    public boolean hasSpecialToken() {
        return specialToken != null;
    }

    public boolean hasNext() {
        return next != null;
    }

    public RustToken next() {
        return (RustToken) next;
    }

    public boolean hasNextSpecialToken() {
        return hasNext() && next().hasSpecialToken();
    }

    public RustToken nextSpecialToken() {
        return next().getEarliestSpecialToken();
    }

    public RustToken getEarliestSpecialToken() {
        if (specialToken == null) {
            return null;
        }
        Token token = this;
        while (token.specialToken != null) {
            token = token.specialToken;
        }
        return (RustToken) token;
    }

    @Override
    public String toString() {
        return enumKind + ": '" + image + "'";
    }

    public RustToken nextTokenMaybeSpecial() {
        if (hasNextSpecialToken()) {
            return nextSpecialToken();
        } else if (hasNext()) {
            return next();
        } else {
            return null;
        }
    }

    public List<RustToken> withSpecialTokens() {
        LinkedList<RustToken> thisWithSpecialTokens = new LinkedList<>();
        RustToken token = this;
        do {
            thisWithSpecialTokens.addFirst(token);
            token = token.specialToken();
        } while (token != null);
        return thisWithSpecialTokens;
    }

    public OffsetRange offsetRange() {
        return new OffsetRange(absoluteBeginPosition - 1, absoluteEndPosition - 1);
    }
}
