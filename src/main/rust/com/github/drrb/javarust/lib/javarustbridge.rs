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
extern crate syntax;

use libc::c_char;
use libc::c_int;
use std::ffi::CString;
use std::ffi;
use std::mem;
use std::str;
use syntax::codemap::BytePos;
use syntax::codemap::CharPos;
use syntax::codemap::CodeMap;
use syntax::diagnostic::Auto;
use syntax::diagnostic::SpanHandler;
use syntax::diagnostic;
use syntax::parse::lexer::Reader;
use syntax::parse::lexer::StringReader;
use syntax::parse::token::BinOpToken;
use syntax::parse::token::DelimToken;
use syntax::parse::token::Lit;
use syntax::parse::token::Token;


#[repr(C)]
pub struct RustLexer<'a> {
    sh: Box<SpanHandler>,
    lexer: Box<StringReader<'a>>
}

#[repr(C)]
pub struct RustToken {
    start_line: c_int,
    start_col: c_int,
    start_byte: c_int,
    start_char: c_int,
    end_line: c_int,
    end_col: c_int,
    end_byte: c_int,
    end_char: c_int,
    token_type: TokenKind,
}

impl<'a> RustLexer<'a> {
    fn next_token(&mut self) -> RustToken {
        let token_and_span = self.lexer.next_token();
        let span = token_and_span.sp;
        let lo_loc = self.sh.cm.lookup_char_pos(span.lo);
        let lo_line = lo_loc.line;
        let CharPos(lo_col) = lo_loc.col;
        let BytePos(lo_byte) = span.lo;
        let CharPos(lo_char) = self.sh.cm.bytepos_to_file_charpos(span.lo);
        let hi_loc = self.sh.cm.lookup_char_pos(span.hi);
        let hi_line = hi_loc.line;
        let CharPos(hi_col) = hi_loc.col;
        let BytePos(hi_byte) = span.hi;
        let CharPos(hi_char) = self.sh.cm.bytepos_to_file_charpos(span.hi);
        //TODO: how to return uint to Java
        RustToken {
            start_byte: lo_byte as c_int,
            start_char: lo_char as c_int,
            start_line: lo_line as c_int,
            start_col: lo_col as c_int,
            end_byte: hi_byte as c_int,
            end_char: hi_char as c_int,
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
