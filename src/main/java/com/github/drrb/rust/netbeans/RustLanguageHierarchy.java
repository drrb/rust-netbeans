package com.github.drrb.rust.netbeans;

import java.util.Collection;
import static java.util.Collections.*;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import org.netbeans.spi.lexer.LanguageHierarchy;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;

public class RustLanguageHierarchy extends LanguageHierarchy<RustTokenId> {
    
    private static final Map<Integer, RustTokenId> ANLTR_TOKEN_TYPE_TO_NETBANS_TOKEN_TYPE = unmodifiableMap(buildTokenMap());
    private static final Collection<RustTokenId> TOKEN_IDS = unmodifiableSet(EnumSet.allOf(RustTokenId.class));

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
        return TOKEN_IDS;
    }

    @Override
    protected Lexer<RustTokenId> createLexer(LexerRestartInfo<RustTokenId> info) {
        return new NetbeansRustLexer(info);
    }

    @Override
    protected String mimeType() {
        return RustLanguage.MIME_TYPE;
    }
    
    private static Map<Integer, RustTokenId> buildTokenMap() {
        Map<Integer, RustTokenId> tokens = new HashMap<Integer, RustTokenId>(RustTokenId.values().length);
        for (RustTokenId rustTokenId : RustTokenId.values()) {
            tokens.put(rustTokenId.antlrTokenType(), rustTokenId);
        }
        return tokens;
    }
}
