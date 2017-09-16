package com.github.drrb.rust.netbeans.parsing.javacc;

import com.github.drrb.rust.netbeans.parsing.RustTokenId;

import static com.github.drrb.rust.netbeans.parsing.RustTokenId.EOF;

public class RustToken extends Token {
    private final RustTokenId enumKind;

    public RustToken(int kind, String image) {
        kind = maybeTranslateSubkind(kind);
        this.kind = kind;
        this.enumKind = RustTokenId.get(kind);
        this.image = image;
    }

    private int maybeTranslateSubkind(int kind) {
        switch(kind) {
            case RustParserConstants.RAW_STRING_LITERAL_0:
            case RustParserConstants.RAW_STRING_LITERAL_1:
            case RustParserConstants.RAW_STRING_LITERAL_2:
            case RustParserConstants.RAW_STRING_LITERAL_3:
                return RustParserConstants.RAW_STRING_LITERAL;
            case RustParserConstants.RAW_BYTE_STRING_LITERAL_0:
            case RustParserConstants.RAW_BYTE_STRING_LITERAL_1:
            case RustParserConstants.RAW_BYTE_STRING_LITERAL_2:
            case RustParserConstants.RAW_BYTE_STRING_LITERAL_3:
                return RustParserConstants.RAW_BYTE_STRING_LITERAL;
            default:
                return kind;
        }
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

    @Override
    public String toString() {
        return enumKind + ": '" + image + "'";
    }
}
