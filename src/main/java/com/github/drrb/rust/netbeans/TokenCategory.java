package com.github.drrb.rust.netbeans;

public enum TokenCategory {
    CHARACTER("character"),
    ERRORS("errors"),
    IDENTIFIER("identifier"),
    KEYWORD("keyword"),
    LITERAL("literal"),
    COMMENT("comment"),
    NUMBER("number"),
    OPERATOR("operator"),
    STRING("string"),
    SEPARATOR("separator"),
    WHITESPACE("whitespace"),
    METHOD_DECLARATION("method-declaration");
    private final String name;
    
    private TokenCategory(String categoryName) {
        this.name = categoryName;
    }
    
    public String getName() {
        return name;
    }
}
