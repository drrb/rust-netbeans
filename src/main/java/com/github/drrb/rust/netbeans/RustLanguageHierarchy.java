package com.github.drrb.rust.netbeans;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import org.netbeans.spi.lexer.LanguageHierarchy;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;

public class RustLanguageHierarchy extends LanguageHierarchy<RustTokenId> {
    
    private static final Map<Integer, RustTokenId> ANLTR_TOKEN_TYPE_TO_NETBANS_TOKEN_TYPE = new HashMap<Integer, RustTokenId>();
    
    static {
        for (RustTokenId rustTokenId : RustTokenId.values()) {
            ANLTR_TOKEN_TYPE_TO_NETBANS_TOKEN_TYPE.put(rustTokenId.antlrTokenType(), rustTokenId);
        }
    }

    public static RustTokenId tokenForAntlrTokenType(int type) {
        RustTokenId tokenId = ANLTR_TOKEN_TYPE_TO_NETBANS_TOKEN_TYPE.get(type);
        if (tokenId == null) {
            throw new RuntimeException(String.format("No RustTokenId for ANTLR token type '%s'", type));
        } else {
            return tokenId;
        }
    }

    @Override
    protected Collection<RustTokenId> createTokenIds() {
        return EnumSet.allOf(RustTokenId.class);
    }

    @Override
    protected Lexer<RustTokenId> createLexer(LexerRestartInfo<RustTokenId> info) {
        return new NetbeansRustLexer(info);
    }

    @Override
    protected String mimeType() {
        return RustLanguage.MIME_TYPE;
    }
    
}
