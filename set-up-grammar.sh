#!/bin/bash

cd "$( dirname "${BASH_SOURCE[0]}" )"
mkdir -p src/main/antlr4
cd src/main/antlr4
wget https://raw.github.com/jbclements/rust-antlr/master/Rust.g4
wget https://raw.github.com/jbclements/rust-antlr/master/xidstart.g4
wget https://raw.github.com/jbclements/rust-antlr/master/xidcont.g4
cd -

