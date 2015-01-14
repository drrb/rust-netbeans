/*
 * Copyright (C) 2014 drrb
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

extern crate libc;
extern crate rustc;
extern crate rustc_driver;
extern crate syntax;

use libc::c_char;
use libc::c_int;
use rustc::DIAGNOSTICS;
use rustc::session::build_session;
use rustc::session::config::Input;
use rustc::session::config;
use rustc::session::search_paths::SearchPaths;
use rustc_driver::driver;
use std::ffi::CString;
use std::ffi;
use std::mem;
use std::rt::unwind;
use std::str;
use syntax::ast::Crate;
use syntax::ast;
use syntax::codemap::BytePos;
use syntax::codemap::CharPos;
use syntax::codemap::CodeMap;
use syntax::codemap::Span;
use syntax::diagnostic::Auto;
use syntax::diagnostic::SpanHandler;
use syntax::diagnostic;
use syntax::diagnostics;
use syntax::parse::lexer::Reader;
use syntax::parse::lexer::StringReader;
use syntax::parse::lexer;
use syntax::parse::token::BinOpToken;
use syntax::parse::token::DelimToken;
use syntax::parse::token::Lit;
use syntax::parse::token::Token;
use syntax::parse::token;
use syntax::parse;
use syntax::visit::FnKind::FkItemFn;
use syntax::visit::FnKind::FkMethod;
use syntax::visit::Visitor;
use syntax::visit;

#[repr(C)]
struct CompilationRequest {
    input_file: String,
    output_dir: String
}

#[repr(C)]
struct TokenCallback {
    delegate: extern "C" fn(*const c_char, c_int, c_int)
}

impl TokenCallback {
    fn new(delegate: extern "C" fn(*const c_char, c_int, c_int)) -> TokenCallback {
        TokenCallback { delegate: delegate }
    }

    fn call(&self, token_type: String, token_start: u32, token_end: u32) {
        let callback = self.delegate;
        let token_type = to_ptr(token_type);
        callback(token_type, token_start as c_int, token_end as c_int);
    }
}

#[repr(C)]
pub struct RustLexer<'a> {
    sh: Box<SpanHandler>,
    lexer: Box<StringReader<'a>>
}

#[repr(C)]
pub struct Ast {
    krate: Box<Crate>
}

#[repr(C)]
pub struct SourceIndex {
    functions: Box<[RustFunction]>,
    functions_size: c_int
}

#[repr(C)]
pub struct RustFunction {
    name: *const c_char
}

#[repr(C)]
pub struct RustToken {
    start_line: c_int,
    start_col: c_int,
    end_line: c_int,
    end_col: c_int,
    token_type: TokenKind,
}

impl<'a> RustLexer<'a> {
    fn next_token(&mut self) -> RustToken {
        let token_and_span = self.lexer.next_token();
        let span = token_and_span.sp;
        let lo_loc = self.sh.cm.lookup_char_pos(span.lo);
        let lo_line = lo_loc.line;
        let CharPos(lo_col) = lo_loc.col;
        let hi_loc = self.sh.cm.lookup_char_pos(span.hi);
        let hi_line = hi_loc.line;
        let CharPos(hi_col) = hi_loc.col;
        //TODO: how to return uint to Java
        RustToken {
            start_line: lo_line as c_int,
            start_col: lo_col as c_int,
            end_line: hi_line as c_int,
            end_col: hi_col as c_int,
            token_type: token_type(token_and_span.tok)
        }
    }
}

#[repr(C)]
pub enum TokenKind {
    Eq,
    Lt,
    Le,
    EqEq,
    Ne,
    Ge,
    Gt,
    AndAnd,
    OrOr,
    Not,
    Tilde,
    //BinOp,
    //BinOpEq,
    At,
    Dot,
    DotDot,
    DotDotDot,
    Comma,
    Semi,
    Colon,
    ModSep,
    RArrow,
    LArrow,
    FatArrow,
    Pound,
    Dollar,
    Question,
    //OpenDelim,
    //CloseDelim,
    //Literal,
    Ident,
    Underscore,
    Lifetime,
    Interpolated,
    DocComment,
    MatchNt,
    SubstNt,
    SpecialVarNt,
    Whitespace,
    Comment,
    Shebang,
    Eof,

    //BinOpToken
    Plus,
    Minus,
    Star,
    Slash,
    Percent,
    Caret,
    And,
    Or,
    Shl,
    Shr,

    //DelimToken
    OpenParen,
    OpenBracket,
    OpenBrace,
    CloseParen,
    CloseBracket,
    CloseBrace,

    //Lit
    ByteLiteral,
    CharLiteral,
    IntegerLiteral,
    FloatLiteral,
    StrLiteral,
    StrRawLiteral,
    BinaryLiteral,
    BinaryRawLiteral,
}

fn token_type(token: Token) -> TokenKind {
    match token {
        Token::Eq => TokenKind::Eq,
        Token::Lt => TokenKind::Lt,
        Token::Le => TokenKind::Le,
        Token::EqEq => TokenKind::EqEq,
        Token::Ne => TokenKind::Ne,
        Token::Ge => TokenKind::Ge,
        Token::Gt => TokenKind::Gt,
        Token::AndAnd => TokenKind::AndAnd,
        Token::OrOr => TokenKind::OrOr,
        Token::Not => TokenKind::Not,
        Token::Tilde => TokenKind::Tilde,
        Token::BinOp(kind) | Token::BinOpEq(kind) => match kind {
            BinOpToken::Plus => TokenKind::Plus,
            BinOpToken::Minus => TokenKind::Minus,
            BinOpToken::Star => TokenKind::Star,
            BinOpToken::Slash => TokenKind::Slash,
            BinOpToken::Percent => TokenKind::Percent,
            BinOpToken::Caret => TokenKind::Caret,
            BinOpToken::And => TokenKind::And,
            BinOpToken::Or => TokenKind::Or,
            BinOpToken::Shl => TokenKind::Shl,
            BinOpToken::Shr => TokenKind::Shr,
        },
        Token::At => TokenKind::At,
        Token::Dot => TokenKind::Dot,
        Token::DotDot => TokenKind::DotDot,
        Token::DotDotDot => TokenKind::DotDotDot,
        Token::Comma => TokenKind::Comma,
        Token::Semi => TokenKind::Semi,
        Token::Colon => TokenKind::Colon,
        Token::ModSep => TokenKind::ModSep,
        Token::RArrow => TokenKind::RArrow,
        Token::LArrow => TokenKind::LArrow,
        Token::FatArrow => TokenKind::FatArrow,
        Token::Pound => TokenKind::Pound,
        Token::Dollar => TokenKind::Dollar,
        Token::Question => TokenKind::Question,
        Token::OpenDelim(kind) => match kind {
            DelimToken::Paren => TokenKind::OpenParen,
            DelimToken::Bracket => TokenKind::OpenBracket,
            DelimToken::Brace => TokenKind::OpenBrace
        },
        Token::CloseDelim(kind) => match kind {
            DelimToken::Paren => TokenKind::CloseParen,
            DelimToken::Bracket => TokenKind::CloseBracket,
            DelimToken::Brace => TokenKind::CloseBrace
        },
        Token::Literal(kind, _) => match kind {
            Lit::Byte(_) => TokenKind::ByteLiteral,
            Lit::Char(_) => TokenKind::CharLiteral,
            Lit::Integer(_) => TokenKind::IntegerLiteral,
            Lit::Float(_) => TokenKind::FloatLiteral,
            Lit::Str_(_) => TokenKind::StrLiteral,
            Lit::StrRaw(_, _) => TokenKind::StrRawLiteral,
            Lit::Binary(_) => TokenKind::BinaryLiteral,
            Lit::BinaryRaw(_, _) => TokenKind::BinaryRawLiteral,
        },
        Token::Ident(_, _) => TokenKind::Ident,
        Token::Underscore => TokenKind::Underscore,
        Token::Lifetime(_) => TokenKind::Lifetime,
        Token::Interpolated(_) => TokenKind::Interpolated,
        Token::DocComment(_) => TokenKind::DocComment,
        Token::MatchNt(_, _, _, _) => TokenKind::MatchNt,
        Token::SubstNt(_, _) => TokenKind::SubstNt,
        Token::SpecialVarNt(_) => TokenKind::SpecialVarNt,
        Token::Whitespace => TokenKind::Whitespace,
        Token::Comment => TokenKind::Comment,
        Token::Shebang(_) => TokenKind::Shebang,
        Token::Eof => TokenKind::Eof,
    }
}

#[no_mangle]
#[allow(non_snake_case)]
pub extern fn createLexer<'a>(source: *const c_char) -> Box<RustLexer<'a>> {
    let file_name = "<file in netbeans>".to_string();
    let source = to_string(&source);

    let sh: &SpanHandler = unsafe { mem::transmute(Box::new(diagnostic::mk_span_handler(diagnostic::default_handler(Auto, None), CodeMap::new()))) };
    let fm = sh.cm.new_filemap(file_name, source);

    Box::new(RustLexer {
        sh: unsafe { mem::transmute(sh) },
        lexer: Box::new(StringReader::new(sh, fm))
    })
}

#[no_mangle]
#[allow(non_snake_case)]
pub extern fn getNextToken(lexer: &mut RustLexer) -> RustToken {
    lexer.next_token()
}

#[no_mangle]
#[allow(non_snake_case,unused_variables)]
pub extern fn destroyLexer(lexer: Box<RustLexer>) {
    //Do nothing: lexer will be released
}

#[no_mangle]
pub extern fn parse(source: *const c_char) -> Box<Ast> {
    let sess = parse::new_parse_sess();
    let cfg = vec!();
    let name = "file-in-netbeans.rs".to_string();
    let mut parser = parse::new_parser_from_source_str(
        &sess,
        cfg,
        name,
        to_string(&source),
    );
    //TODO: is this needed?
    //parser.quote_depth += 1u;
    //TODO: let krate = box unwind::try(|| { parser.parse_craate_mod() });
    let krate = Box::new(parser.parse_crate_mod());

    Box::new(Ast {
        krate: krate
    })
}

struct FunctionVisitor {
    functions: Vec<RustFunction>
}

impl<'v> Visitor<'v> for FunctionVisitor {
    #[allow(unused_variables)]
    fn visit_fn(&mut self, a: visit::FnKind<'v>, b: &'v ast::FnDecl, c: &'v ast::Block, d: Span, id: ast::NodeId) {
        let name = match a {
            FkItemFn(ident, _, _, _) | FkMethod(ident, _, _) => to_ptr(ident.name.as_str().to_string()),
            _ => to_ptr("<anonymous>".to_string())
        };
        let function = RustFunction { name: name };
        self.functions.push(function);
        visit::walk_fn(self, a, b, c, d);
    }

    fn visit_mac(&mut self, _macro: &'v ast::Mac) {
        //TODO this has a panic in it by default. How will we index macros?
    }
}

#[no_mangle]
pub extern fn index(ast: &Ast) -> Box<SourceIndex> {
    let mut visitor = FunctionVisitor {
        functions: vec!()
    };
    //TODO: will work for a non-crate module? Or will we need to walk_mod()?
    visit::walk_crate(&mut visitor, &*ast.krate);
    Box::new(SourceIndex {
        functions_size: visitor.functions.len() as c_int,
        functions: visitor.functions.into_boxed_slice()
    })
}

#[no_mangle]
#[allow(non_snake_case,unused_variables)]
pub extern fn destroyAst(ast: Box<Ast>) {
    //Do nothing: ast will be released
}

#[no_mangle]
#[allow(non_snake_case)]
pub extern fn readTokens(input_file: *const c_char, callback: extern "C" fn(*const c_char, c_int, c_int)) {
    use syntax::parse::lexer::Reader;

    let callback = TokenCallback::new(callback);
    let sess = parse::new_parse_sess();
    let fm = parse::file_to_filemap(&sess,
                                    &Path::new(to_string(&input_file)),
                                    None);
    let mut lexer = lexer::StringReader::new(&sess.span_diagnostic, fm);
    loop {
        let next = lexer.next_token();
        let token_type = format!("{:?}", next.tok);
        if next.tok == token::Eof { break }
        let BytePos(start) = next.sp.lo;
        let BytePos(end) = next.sp.hi;
        callback.call(token_type, start, end);
    }
    //// These may be left in an incoherent state after a previous compile.
    //// `clear_tables` and `get_ident_interner().clear()` can be used to free
    //// memory, but they do not restore the initial state.
    //syntax::ext::mtwt::reset_tables();
    //token::reset_ident_interner();

    //let krate = time(sess.time_passes(), "parsing", (), |_| {
    //    match *input {
    //        Input::File(ref file) => {
    //            parse::parse_crate_from_file(&(*file), cfg.clone(), &sess.parse_sess)

}

#[no_mangle]
pub extern fn compile(input_file: *const c_char, output_dir: *const c_char) -> c_int {
    let request = CompilationRequest {
        input_file: to_string(&input_file),
        output_dir: to_string(&output_dir)
    };

    unsafe {
        let result = unwind::try(|| { do_compile(&request) });
        match result {
            Ok(_) => 0,
            Err(_) => 1
        }
    }
}

fn do_compile(request: &CompilationRequest) {
    println!("Compiling, {} to {}...", request.input_file, request.output_dir);
    let descriptions = diagnostics::registry::Registry::new(&DIAGNOSTICS);
    let mut sopts = config::basic_options();
    sopts.search_paths = SearchPaths::new();
    sopts.search_paths.add_path("/usr/local/lib/rustlib/x86_64-apple-darwin/lib");
    let odir = Some(Path::new(&request.output_dir));
    let ofile = None;
    let (input, input_file_path) = (Input::File(Path::new(&request.input_file)), Some(Path::new(&request.input_file)));
    let sess = build_session(sopts, input_file_path, descriptions);
    let cfg = config::build_configuration(&sess);
    driver::compile_input(sess, cfg, &input, &odir, &ofile, None);
}

fn to_string(pointer: &*const c_char) -> String {
    let slice = unsafe { ffi::c_str_to_bytes(pointer) };
    str::from_utf8(slice).unwrap().to_string()
}

fn to_ptr(string: String) -> *const c_char {
    let cs = CString::from_slice(string.as_bytes());
    let ptr = cs.as_ptr();
    // Tell Rust not to clean up the string while we still have a pointer to it.
    // Otherwise, we'll get a segfault.
    unsafe { mem::forget(cs) };
    ptr
}
