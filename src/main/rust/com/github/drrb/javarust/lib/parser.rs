use libc::c_char;
use libc::c_int;
use std::ffi::CString;
use std::mem;
use syntax::ast::Crate;
use syntax::codemap::CharPos;
use syntax::codemap::CodeMap;
use syntax::codemap::Span;
use syntax::diagnostic::Emitter;
use syntax::diagnostic::Handler;
use syntax::diagnostic::Level;
use syntax::diagnostic::RenderSpan;
use syntax::diagnostic;
use syntax::parse::ParseSess;
use syntax::parse;

#[repr(C)]
pub struct Ast {
    pub parse_session: Box<ParseSess>,
    pub krate: Box<Crate>
}

#[repr(C)]
pub struct ParseMessage {
    level: ParseMessageLevel,
    start_line: c_int,
    start_col: c_int,
    end_line: c_int,
    end_col: c_int,
    message: *const c_char,
}

#[repr(C)]
pub enum ParseMessageLevel {
    Bug,
    Fatal,
    Error,
    Warning,
    Note,
    Help,
}

pub struct MessageCollector {
    collect: extern "C" fn (ParseMessage)
}

pub fn parse(
    file_name: String,
    source: String,
    message_collector: MessageCollector,
) -> Ast {
    let handler = diagnostic::mk_handler(Box::new(message_collector));
    let span_handler = diagnostic::mk_span_handler(handler, CodeMap::new());
    let sess = parse::new_parse_sess_special_handler(span_handler);
    let krate = {
        let cfg = vec!();
        let mut parser = parse::new_parser_from_source_str(
            &sess,
            cfg,
            file_name,
            source,
            );
        //TODO: is this needed?
        //parser.quote_depth += 1u;
        parser.parse_crate_mod()
    };
    Ast { parse_session: Box::new(sess), krate: Box::new(krate) }
}

impl MessageCollector {
    pub fn new(collect: extern "C" fn (ParseMessage)) -> MessageCollector {
        MessageCollector {
            collect: collect
        }
    }
}

impl Emitter for MessageCollector {
    fn emit(&mut self, cmsp: Option<(&CodeMap, Span)>, msg: &str, code: Option<&str>, lvl: Level) {
        match cmsp {
            Some((codemap, span)) => {
                let lo_loc = codemap.lookup_char_pos(span.lo);
                let lo_line = lo_loc.line;
                let CharPos(lo_col) = lo_loc.col;
                let hi_loc = codemap.lookup_char_pos(span.hi);
                let hi_line = hi_loc.line;
                let CharPos(hi_col) = hi_loc.col;
                let collect = self.collect;
                collect(ParseMessage {
                    level: ParseMessageLevel::from_emitted(lvl),
                    start_line: lo_line as c_int,
                    start_col: lo_col as c_int,
                    end_line: hi_line as c_int,
                    end_col: hi_col as c_int,
                    message: to_ptr(msg.to_string()),
                });
            },
            None => {
                // TODO: collect even if there's no location?
            }
        }
    }

    fn custom_emit(&mut self, cm: &CodeMap, sp: RenderSpan, msg: &str, lvl: Level) {
        let collect = self.collect;
        collect(ParseMessage {
            level: ParseMessageLevel::from_emitted(lvl),
            start_line: 0 as c_int,
            start_col: 0 as c_int,
            end_line: 0 as c_int,
            end_col: 0 as c_int,
            message: to_ptr(msg.to_string()),
        });
    }
}

impl ParseMessageLevel {
    pub fn from_emitted(level: Level) -> ParseMessageLevel {
        match level {
            Level::Bug => ParseMessageLevel::Bug,
            Level::Fatal => ParseMessageLevel::Fatal,
            Level::Error => ParseMessageLevel::Error,
            Level::Warning => ParseMessageLevel::Warning,
            Level::Note => ParseMessageLevel::Note,
            Level::Help => ParseMessageLevel::Help,
        }
    }
}

fn to_ptr(string: String) -> *const c_char {
    let cs = CString::from_slice(string.as_bytes());
    let ptr = cs.as_ptr();
    // Tell Rust not to clean up the string while we still have a pointer to it.
    // Otherwise, we'll get a segfault.
    unsafe { mem::forget(cs) };
    ptr
}

