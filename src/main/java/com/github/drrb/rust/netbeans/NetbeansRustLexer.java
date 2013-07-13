package com.github.drrb.rust.netbeans;

import org.netbeans.api.lexer.Token;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;

public class NetbeansRustLexer implements Lexer<RustTokenId> {
    private final RustLexer lexer;
    private final LexerRestartInfo<RustTokenId> info;

    public NetbeansRustLexer(LexerRestartInfo<RustTokenId> info) {
        AntlrCharStream charStream = new AntlrCharStream(info.input(), "RustEditor");
        this.lexer = new RustLexer(charStream);
        this.info = info;
    }

    @Override
    public Token<RustTokenId> nextToken() {
        org.antlr.v4.runtime.Token token = lexer.nextToken();
        if (token.getType() != RustLexer.EOF) {
            RustTokenId tokenId = RustLanguageHierarchy.tokenForAntlrTokenType(token.getType());
            return info.tokenFactory().createToken(tokenId);
        }
        return null;
    }

    @Override
    public Object state() {
        //Tutorial returned null
        return null;
    }

    @Override
    public void release() {
        //Tutorial left this blank
    }
    
}
