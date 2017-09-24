package com.github.drrb.rust.netbeans.parsing.javacc;

public class RustTokenFactory {

    public static Token newToken(int ofKind, String tokenImage) {
        return new RustToken(maybeTranslateSubkind(ofKind), tokenImage);
    }

    private static int maybeTranslateSubkind(int kind) {
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
}

