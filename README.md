# Rust NetBeans Plugin

This is a NetBeans plugin that adds support for [Rust](http://www.rust-lang.org)

[![Build Status](https://travis-ci.org/drrb/rust-netbeans.png?branch=master)](https://travis-ci.org/drrb/rust-netbeans)

## Requirements

Needs NetBeans 7.4.x or above, and as such needs Java 7+

## Features

So far, it includes

* basic project view
* basic syntax highlighting
* basic error highlighting
* basic code completion
* code folding
* basic file overview
* basic auto-indentation
* basic auto-formatting
* basic occurrence matching
* basic variable renaming
* brace matching
* comment toggling

## Installing

First, clone and build the pluging.

```console
git clone https://github.com/drrb/rust-netbeans.git
mvn package
```

You can then install the plugin from NetBeans (the plugin will have been packaged at `target/rust-netbeans-1.0.0-SNAPSHOT.nbm`).

## Contributing

1. Fork it
2. Create your feature branch (`git checkout -b my-new-feature`)
3. Make your changes, and add tests for them
4. Test your changes (`mvn test`)
5. Commit your changes (`git commit -am 'Add some feature'`)
6. Push to the branch (`git push origin my-new-feature`)
7. Create new Pull Request

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
