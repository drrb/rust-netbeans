/**
 * Copyright (C) 2017 drrb
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
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
