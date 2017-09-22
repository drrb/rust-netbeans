package com.github.drrb.rust.netbeans.parsing.javacc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.charset.StandardCharsets.UTF_8;

public class JsonGoldenFile<T extends JsonSerializable> extends TestFile {
    private final Class<T> type;

    public JsonGoldenFile(Class<T> type, Path path) {
        super(path);
        this.type = type;
    }

    public void createWith(T tokenizationResult) {
        try {
            Files.write(path, gson().toJson(tokenizationResult).getBytes(UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public T deserialize() {
        try {
            return gson().fromJson(new String(Files.readAllBytes(path), UTF_8), type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Gson gson() {
        return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    }
}
