use libc::c_int;
use syntax::ast::Crate;
use syntax::ast;
use syntax::codemap::BytePos;
use syntax::codemap::CharPos;
use syntax::codemap::CodeMap;
use syntax::codemap::Span;
use syntax::codemap;
use syntax::diagnostic::Auto;
use syntax::diagnostic;
use syntax::parse::lexer::Reader;
use syntax::parse::lexer::StringReader;
use syntax::visit::FnKind::FkItemFn;
use syntax::visit::FnKind::FkMethod;
use syntax::visit::Visitor;
use syntax::visit;

#[repr(C)]
pub enum HighlightKind {
    EnumConstant,
    EnumType,
    Function,
    Method,
    Struct,
}

#[repr(C)]
pub struct Highlight {
    start_line: c_int,
    start_col: c_int,
    start_byte: c_int,
    start_char: c_int,
    end_line: c_int,
    end_col: c_int,
    end_byte: c_int,
    end_char: c_int,
    kind: HighlightKind,
}

pub struct HighlightVisitor<'a> {
    codemap: &'a CodeMap,
    report_highlight: extern "C" fn (Highlight),
}

pub fn get_highlights(krate: &Crate, visitor: &mut HighlightVisitor) {
    visit::walk_crate(visitor, krate);
}

impl <'a> HighlightVisitor<'a> {
    pub fn new<'v>(codemap: &'v CodeMap, report_highlight: extern "C" fn (Highlight)) -> HighlightVisitor<'v> {
        HighlightVisitor { codemap: codemap, report_highlight: report_highlight }
    }

    fn report_new_highlight(&mut self, kind: HighlightKind, span: Span) {
        let lo_loc = self.codemap.lookup_char_pos(span.lo);
        let lo_line = lo_loc.line;
        let CharPos(lo_col) = lo_loc.col;
        let BytePos(lo_byte) = span.lo;
        let CharPos(lo_char) = self.codemap.bytepos_to_file_charpos(span.lo);
        let hi_loc = self.codemap.lookup_char_pos(span.hi);
        let hi_line = hi_loc.line;
        let CharPos(hi_col) = hi_loc.col;
        let BytePos(hi_byte) = span.hi;
        let CharPos(hi_char) = self.codemap.bytepos_to_file_charpos(span.hi);
        let report_highlight = self.report_highlight;
        report_highlight(Highlight {
            start_line: lo_line as c_int,
            start_col: lo_col as c_int,
            start_byte: lo_byte as c_int,
            start_char: lo_char as c_int,
            end_line: hi_line as c_int,
            end_col: hi_col as c_int,
            end_byte: hi_byte as c_int,
            end_char: hi_char as c_int,
            kind: HighlightKind::Function,
        });
    }
}

impl<'v,'a> Visitor<'v> for HighlightVisitor<'a> {

    #[allow(unused_variables)]
    fn visit_fn(&mut self, a: visit::FnKind<'v>, b: &'v ast::FnDecl, c: &'v ast::Block, d: Span, id: ast::NodeId) {
        match a {
            FkItemFn(ident, _, _, _) => {
                let source = self.codemap.span_to_snippet(d).expect(format!("Couldn't get snippet for {}", self.codemap.span_to_string(d).as_slice()).as_slice());
                let sh = diagnostic::mk_span_handler(diagnostic::default_handler(Auto, None), CodeMap::new());
                let fm = sh.cm.new_filemap("myfunction".to_string(), source);
                let mut lexer = StringReader::new(&sh, fm);
                //TODO: handle functions that are unsafe, extern, etc
                lexer.next_token(); //fn
                lexer.next_token(); //whitespace
                let name_token = lexer.next_token();
                let name_token_lo = d.lo + name_token.sp.lo; //TODO: why - 1?
                let name_token_hi = d.lo + name_token.sp.hi;
                let name_token_abs_span = codemap::mk_sp(name_token_lo, name_token_hi);
                self.report_new_highlight(HighlightKind::Function, name_token_abs_span);
            },
            FkMethod(ident, _, _) =>  {
                self.report_new_highlight(HighlightKind::Method, d);
            },
            _ => {}
        }
        visit::walk_fn(self, a, b, c, d);
    }

    #[allow(unused_variables)]
    fn visit_mac(&mut self, _macro: &'v ast::Mac) {
        // Ignore macros (the default implementation of visit_mac panics)
    }
}
