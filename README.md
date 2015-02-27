# Rust NetBeans Plugin

A NetBeans plugin for [Rust](http://www.rust-lang.org) that embeds the native
Rust compiler. It's fast, and adapts to changes in the language!

| Linux | Windows |
| ----- | ------- |
| [![Linux Build Status](https://travis-ci.org/drrb/rust-netbeans.svg?branch=master)](https://travis-ci.org/drrb/rust-netbeans) | [![Windows Build status](https://ci.appveyor.com/api/projects/status/ae0ci8qvmh5pawi1/branch/master?svg=true)](https://ci.appveyor.com/project/drrb/rust-netbeans/branch/master) |

**NOTE:** This plugin has recently undergone significant design changes to
embed the native Rust compiler instead of using one written in Java.
Consequently, some features have been removed and will be gradually added back
in.

## Requirements

* NetBeans 8.x or above
* Java 7+
* OSX (Hopefully Linux soon (waiting on [this issue](https://github.com/rust-lang/rust/issues/22528)). May work on Windows. Please let me know if you get it working on either!)
* Rust 1.0 installed by rustup (currently developing against post-1.0-alpha nightlies)
* Cargo (somewhere on the path)

## Features

So far, it includes

* Cargo project support:
    * Project view
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
