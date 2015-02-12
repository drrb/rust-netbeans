/*
 * Copyright (C) 2015 drrb
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
#![crate_type = "dylib"]
#![feature(collections)]
#![feature(core)]
#![feature(libc)]
#![feature(path)]
#![feature(rustc_private)]
#![feature(std_misc)]

extern crate libc;
extern crate rustc;
extern crate rustc_driver;
extern crate rustc_trans;
extern crate syntax;

mod compiler;
mod highlights;
mod lexer;
mod parser;
mod raw;

use compiler::CompileRequest;
use highlights::Highlight;
use highlights::HighlightVisitor;
use lexer::RustLexer;
use lexer::RustToken;
use parser::Ast;
use parser::MessageCollector;
use parser::ParseMessage;

use libc::c_char;
use libc::c_int;
use std::rt::unwind;

#[no_mangle]
#[allow(non_snake_case)]
pub extern fn createLexer<'a>(source: *const c_char) -> Box<RustLexer<'a>> {
    let file_name = "<file in netbeans>".to_string();
    let source = raw::to_string(&source);
    Box::new(RustLexer::new(file_name, source))
}

#[no_mangle]
#[allow(non_snake_case)]
pub extern fn getNextToken(lexer: &mut RustLexer, callback: extern "C" fn (RustToken)) {
    let result = unsafe {
        unwind::try(|| {
            callback(lexer.next_token());
        })
    };
    //TODO return something from here?
    match result {
        Ok(_) => {},
        Err(_) => {}
    }
}

#[no_mangle]
#[allow(non_snake_case)]
pub extern fn destroyLexer(_: Box<RustLexer>) {
    //Do nothing: lexer will be released
}

#[no_mangle]
pub extern fn parse(
    file_name: *const c_char,
    source: *const c_char,
    result_callback: extern "C" fn (Box<Ast>),
    message_callback: extern "C" fn (ParseMessage),
) {
    let result = unsafe {
        unwind::try(|| {
            let message_collector = MessageCollector::new(message_callback);
            let ast = parser::parse(raw::to_string(&file_name), raw::to_string(&source), message_collector);
            result_callback(Box::new(ast));
        })
    };
    //TODO return something from here?
    match result {
        Ok(_) => {},
        Err(_) => {}
    }
}

#[no_mangle]
#[allow(non_snake_case)]
pub extern fn destroyAst(_: Box<Ast>) {
    //Do nothing: ast will be released
}

#[no_mangle]
#[allow(non_snake_case)]
pub extern fn getHighlights(
    ast: &Ast,
    callback: extern "C" fn (Highlight),
) {
    let mut visitor = HighlightVisitor::new(&ast.parse_session.span_diagnostic.cm, callback);
    highlights::get_highlights(&*ast.krate, &mut visitor);
}

#[no_mangle]
pub extern fn compile(
    input_path: *const c_char,
    input_source: *const c_char,
    search_paths: *const *const c_char,
    search_paths_length: c_int,
    message_callback: extern "C" fn (ParseMessage),
) -> c_int {
    let input_path = raw::to_string(&input_path);
    let input_source = raw::to_string(&input_source);
    let search_paths = unsafe { Vec::from_raw_buf(search_paths, search_paths_length as usize) };
    let search_paths = search_paths.iter().map(|cstring| raw::to_string(&cstring)).collect();
    let message_collector = MessageCollector::new(message_callback);
    unsafe {
        let result = unwind::try(|| {
            compiler::compile(CompileRequest {
                input_path: input_path,
                input_source: input_source,
                search_paths: search_paths,
                message_collector: message_collector,
            })
        });
        match result {
            Ok(_) => 0,
            Err(_) => 1
        }
    }
}
