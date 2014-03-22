#!/bin/bash
#
# Copyright (C) 2013 drrb
#
# This program is free software: you can redistribute it and/or modify it under
# the terms of the GNU General Public License as published by the Free Software
# Foundation, either version 3 of the License, or (at your option) any later
# version.
#
# This program is distributed in the hope that it will be useful, but WITHOUT
# ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
# FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
# details.
#
# You should have received a copy of the GNU General Public License along with
# this program. If not, see <http://www.gnu.org/licenses/>.
#

set -e

ANTLR_URL="https://raw.github.com/jbclements/rust-antlr/master"

get() {
    local file=$1

    if [ -z "$ANTLR_DIR" ]
    then
	wget $ANTLR_URL/$file --no-check-certificate
    else
	cp -v $ANTLR_DIR/$file .
    fi
}

log() {
    echo "$@" >&2
}

cd `dirname "${BASH_SOURCE[0]}"`/..

GRAMMAR_DIR_POINTER=.antlr-grammar-location
if [ -f "$GRAMMAR_DIR_POINTER" ]
then
    ANTLR_DIR=$(cd `cat $GRAMMAR_DIR_POINTER` && pwd)
    log "Offline mode: sourcing Rust ANTLR grammar from '$ANTLR_DIR'"
    if [ ! -d "$ANTLR_DIR" ]
    then
	log "Error: directory not found - '$ANTLR_DIR'"
	exit 1
    fi
else
    log "Downloading Rust ANTLR grammar from '$ANTLR_URL'"
fi

mkdir -p src/main/antlr4/com/github/drrb/rust/netbeans/parsing
cd src/main/antlr4
get xidstart.g4
get xidcont.g4
cd -
cd src/main/antlr4/com/github/drrb/rust/netbeans/parsing
get Rust.g4
cd -
git apply --reverse src/etc/rust-antlr.patch 
