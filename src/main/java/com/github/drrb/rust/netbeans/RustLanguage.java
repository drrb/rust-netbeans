package com.github.drrb.rust.netbeans;

import org.netbeans.api.lexer.Language;
import org.netbeans.modules.csl.spi.DefaultLanguageConfig;
import org.netbeans.modules.csl.spi.LanguageRegistration;
import org.netbeans.modules.parsing.spi.Parser;
import org.openide.util.NbBundle;

@LanguageRegistration(mimeType = RustLanguage.MIME_TYPE)
public class RustLanguage extends DefaultLanguageConfig {
    
    public static final String MIME_TYPE = "text/x-rust-source";

    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(RustLanguage.class, MIME_TYPE);
    }

    @Override
    public Language getLexerLanguage() {
        return RustTokenId.getLanguage();
    }
    
    @Override
    public Parser getParser() {
        return new NetbeansRustParser();
    }
}