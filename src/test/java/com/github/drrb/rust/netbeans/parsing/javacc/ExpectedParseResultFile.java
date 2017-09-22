package com.github.drrb.rust.netbeans.parsing.javacc;

import java.nio.file.Path;

public class ExpectedParseResultFile extends JsonGoldenFile<ParseResult> {
    public ExpectedParseResultFile(Path path) {
        super(ParseResult.class, path);
    }

    public ParseResult result() {
        return deserialize();
    }
}
