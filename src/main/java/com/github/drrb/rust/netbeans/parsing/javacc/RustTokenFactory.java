package com.github.drrb.rust.netbeans.parsing.javacc;

public class RustTokenFactory {

    public static Token newToken(int ofKind, String tokenImage) {
        return new RustToken(ofKind, tokenImage);
    }
}

