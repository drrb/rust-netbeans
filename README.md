# Rust NetBeans Plugin

This is a NetBeans plugin that adds support for [Rust](http://www.rust-lang.org)

[![Build Status](https://travis-ci.org/drrb/rust-netbeans.png?branch=master)](https://travis-ci.org/drrb/rust-netbeans)

## Requirements

* NetBeans 8.x or above
* Java 8
* OSX (probably also works on Linux, may work on Windows with hacks)
* Rust 1.0 installed by rustup (currently developing against post-1.0-alpha nightlies)
* Cargo (somewhere on the path)

## Features

So far, it includes

* Cargo project support:
    * Project view
    * clean/build/run/test from UI
* Highlighting:
    * basic syntax highlighting
    * parse error highlighting
* Editing:
    * brace matching
    * comment toggling
* Formatting:
    * basic auto-indentation
    * basic auto-formatting
* Coming Soon:
    * basic code completion
    * code folding
    * basic file overview
    * basic occurrence matching
    * basic variable renaming

## Installing

First, clone and build the plugin.

```console
git clone https://github.com/drrb/rust-netbeans.git
mvn package
```

You can then install the plugin from NetBeans (the plugin will have been packaged at `target/rust-netbeans-1.0.0-SNAPSHOT.nbm`).

## License

Rust NetBeans Plugin

Copyright (C) 2015 drrb

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
