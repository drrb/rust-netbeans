.PHONY: all

all: src/main/java/com/github/drrb/rust/netbeans/parsing/javacc/RustTokenKind.java

src/main/java/com/github/drrb/rust/netbeans/parsing/javacc/RustTokenKind.java: src/scripts/generate-rust-token-kind-enum target/generated-sources/javacc/com/github/drrb/rust/netbeans/parsing/javacc/RustParserConstants.java
	src/scripts/generate-rust-token-kind-enum

target/generated-sources/jjtree/com/github/drrb/rust/netbeans/parsing/javacc/Rust.jj: src/main/jjtree/com/github/drrb/rust/netbeans/parsing/javacc/Rust.jjt
	java -cp ~/.m2/repository/net/java/dev/javacc/javacc/7.0.2/javacc-7.0.2.jar jjtree -OUTPUT_DIRECTORY=target/generated-sources/jjtree/com/github/drrb/rust/netbeans/parsing/javacc $<

target/generated-sources/javacc/com/github/drrb/rust/netbeans/parsing/javacc/RustParserConstants.java: target/generated-sources/jjtree/com/github/drrb/rust/netbeans/parsing/javacc/Rust.jj
	java -cp ~/.m2/repository/net/java/dev/javacc/javacc/7.0.2/javacc-7.0.2.jar javacc -OUTPUT_DIRECTORY=target/generated-sources/javacc/com/github/drrb/rust/netbeans/parsing/javacc $<
