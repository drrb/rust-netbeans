# Rust NetBeans Plugin

A NetBeans plugin for [Rust](http://www.rust-lang.org) that embeds the native
Rust compiler. It's fast, and adapts to changes in the language!

| OSX | Linux | Windows |
| --- | ----- | ------- |
| ![OSX Build Status](https://img.shields.io/badge/build-passing%20on%20my%20laptop-brightgreen.svg) | [![Linux Build Status](https://travis-ci.org/drrb/rust-netbeans.svg?branch=master)](https://travis-ci.org/drrb/rust-netbeans) | [![Windows Build status](https://ci.appveyor.com/api/projects/status/ae0ci8qvmh5pawi1/branch/master?svg=true)](https://ci.appveyor.com/project/drrb/rust-netbeans/branch/master) |

**NOTE:** This plugin has recently undergone significant design changes to
embed the native Rust compiler instead of using one written in Java.
Consequently, some features have been removed and will be gradually added back
in.

## Requirements

* NetBeans 8.x or above
* Java 8+
* Rust 1.0ish (currently developing against post-1.0-alpha nightlies)
* Cargo
* Rustup

## Features

So far, it includes

* Cargo project support:
    * project view
    * clean/build/run/test from UI
* Highlighting:
    * syntax highlighting
    * error highlighting for:
        * syntax errors
        * lifetime/borrow errors
        * lint warnings
        * other compilation errors
* Editing:
    * brace matching
    * comment toggling
* Formatting:
    * auto-indentation
    * basic auto-formatting
* Testing UI:
    * run all tests
    * run module's tests
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
