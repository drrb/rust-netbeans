use lexer::TokenKind;
use libc::c_int;
use syntax::ast::Crate;
use syntax::ast::Item_;
use syntax::ast::TraitItem;
use syntax::ast;
use syntax::codemap::BytePos;
use syntax::codemap::CharPos;
use syntax::codemap::CodeMap;
use syntax::codemap::Span;
use syntax::codemap;
use syntax::diagnostic::Auto;
use syntax::diagnostic;
use syntax::parse::token::Token;
use syntax::parse::lexer::Reader;
use syntax::parse::lexer::StringReader;
use syntax::visit::FnKind;
use syntax::visit::Visitor;
use syntax::visit;

#[repr(C)]
pub enum HighlightKind {
    EnumType,
    EnumVariant,
    Field,
    Function,
    Method,
    Struct,
    Trait,
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
            kind: kind,
        });
    }

    fn find_embedded_token(&self, kind: TokenKind, span: Span) -> Span {
        let source = self.codemap.span_to_snippet(span).expect(format!("Couldn't get snippet for {}", self.codemap.span_to_string(span).as_slice()).as_slice());
        let sh = diagnostic::mk_span_handler(diagnostic::default_handler(Auto, None), CodeMap::new());
        let fm = sh.cm.new_filemap("myfunction".to_string(), source);
        let mut lexer = StringReader::new(&sh, fm);
        ////TODO: handle functions that are unsafe, extern, etc
        //lexer.next_token(); //fn
        //lexer.next_token(); //whitespace
        //let target_token = lexer.next_token();
        ////TODO: how to do this??
        let mut target_token = lexer.next_token();
        while TokenKind::for_token(target_token.tok) != kind {
            target_token = lexer.next_token();
        }
        let target_token_lo = span.lo + target_token.sp.lo;
        let target_token_hi = span.lo + target_token.sp.hi;
        codemap::mk_sp(target_token_lo, target_token_hi)
    }
}

impl<'v,'a> Visitor<'v> for HighlightVisitor<'a> {
    fn visit_item(&mut self, item: &'v ast::Item) {
        match item.node {
            Item_::ItemEnum(_, _) => {
                let name_span = self.find_embedded_token(TokenKind::Ident, item.span);
                self.report_new_highlight(HighlightKind::EnumType, name_span);
            }
            Item_::ItemImpl(_, _, _, _, _, _) | Item_::ItemStruct(_, _) => {
                let name_span = self.find_embedded_token(TokenKind::Ident, item.span);
                self.report_new_highlight(HighlightKind::Struct, name_span);
            }
            Item_::ItemTrait(_, _, _, _) => {
                let name_span = self.find_embedded_token(TokenKind::Ident, item.span);
                self.report_new_highlight(HighlightKind::Trait, name_span);
            }
            _ => {}
        }
        visit::walk_item(self, item)
    }

    fn visit_trait_item(&mut self, trait_item: &'v ast::TraitItem) {
        match *trait_item {
            TraitItem::RequiredMethod(ref method) => {
                let name_span = self.find_embedded_token(TokenKind::Ident, method.span);
                self.report_new_highlight(HighlightKind::Method, name_span);
            }
            TraitItem::ProvidedMethod(ref method) => {
                let name_span = self.find_embedded_token(TokenKind::Ident, method.span);
                self.report_new_highlight(HighlightKind::Method, name_span);
            }
            _ => {}
        }
        visit::walk_trait_item(self, trait_item)
    }

    fn visit_struct_field(&mut self, struct_field: &'v ast::StructField) {
        let name_span = self.find_embedded_token(TokenKind::Ident, struct_field.span);
        self.report_new_highlight(HighlightKind::Field, name_span);
        visit::walk_struct_field(self, struct_field)
    }

    fn visit_variant(&mut self, variant: &'v ast::Variant, generics: &'v ast::Generics) {
        let name_span = self.find_embedded_token(TokenKind::Ident, variant.span);
        self.report_new_highlight(HighlightKind::EnumVariant, name_span);
        visit::walk_variant(self, variant, generics)
    }

    fn visit_fn(&mut self, fn_kind: visit::FnKind<'v>, fn_decl: &'v ast::FnDecl, fn_block: &'v ast::Block, fn_span: Span, _: ast::NodeId) {
        match fn_kind {
            FnKind::FkItemFn(_, _, _, _) => {
                let name_span = self.find_embedded_token(TokenKind::Ident, fn_span);
                self.report_new_highlight(HighlightKind::Function, name_span);
            },
            FnKind::FkMethod(_, _, _) =>  {
                let name_span = self.find_embedded_token(TokenKind::Ident, fn_span);
                self.report_new_highlight(HighlightKind::Method, name_span);
            },
            _ => {}
        }
        visit::walk_fn(self, fn_kind, fn_decl, fn_block, fn_span);
    }

    #[allow(unused_variables)]
    fn visit_mac(&mut self, _macro: &'v ast::Mac) {
        // Ignore macros (the default implementation of visit_mac panics)
    }
}
