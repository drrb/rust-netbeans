package com.github.drrb.rust.netbeans.parsing.javacc;

import com.github.drrb.rust.netbeans.parsing.RustTokenId;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.LinkedList;
import java.util.List;

public class ParseResult {
    public static class Token {
        public String image;
        public RustTokenId kind;
        private transient Integer beginLine;
        private transient Integer beginColumn;

        public Token() {
            this(null, null);
        }

        public Token(String image, RustTokenId kind) {
            this(image, kind, null, null);
        }

        public Token(String image, RustTokenId kind, Integer beginLine, Integer beginColumn) {
            this.image = image;
            this.kind = kind;
            this.beginLine = beginLine;
            this.beginColumn = beginColumn;
        }

        @Override
        public String toString() {
            if (beginLine == null) {
                return ToStringBuilder.reflectionToString(this);
            } else {
                return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE, true);
            }
        }

        @Override
        public boolean equals(Object other) {
            return EqualsBuilder.reflectionEquals(this, other);
        }

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this);
        }

        public boolean isGarbage() {
            return kind == RustTokenId.GARBAGE;
        }
    }

    public List<Token> tokens = new LinkedList<>();

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public boolean equals(Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    public String json() {
        return gson().toJson(this);
    }

    private Gson gson() {
        return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    }

}
