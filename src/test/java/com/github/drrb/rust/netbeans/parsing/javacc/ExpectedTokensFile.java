package com.github.drrb.rust.netbeans.parsing.javacc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ExpectedTokensFile extends TestFile {
    public ExpectedTokensFile(Path path) {
        super(path);
    }

    public void createWith(ParseResult parseResult) {
        try {
            Files.write(path, gson().toJson(parseResult).getBytes(UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ParseResult tokens() {
        try {
            return gson().fromJson(new String(Files.readAllBytes(path), UTF_8), ParseResult.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Gson gson() {
        return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    }
}
