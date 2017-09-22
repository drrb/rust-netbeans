package com.github.drrb.rust.netbeans.parsing.javacc;

import java.nio.file.Path;

public class ExpectedTokensFile extends JsonGoldenFile<TokenizationResult> {
    public ExpectedTokensFile(Path path) {
        super(TokenizationResult.class, path);
    }

    public TokenizationResult tokens() {
        return deserialize();
    }
}
