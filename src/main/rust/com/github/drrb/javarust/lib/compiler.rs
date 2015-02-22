use parser::MessageCollector;
use rustc::middle::ty::CtxtArenas;
use rustc::session::Session;
use rustc::session::config::Input;
use rustc::session::config;
use rustc::session::search_paths::SearchPaths;
use rustc::session;
use rustc::util::common::time;
use rustc_driver::driver::CompileController;
use rustc_driver::Compilation;
use rustc_driver::driver;
use rustc_trans::back::link;
use syntax::ast::Crate;
use syntax::ast::CrateConfig;
use syntax::ast_map::Forest;
use syntax::codemap::CodeMap;
use syntax::diagnostic;
use syntax::parse::token;
use syntax::parse;
use syntax;

pub struct CompileRequest {
    pub input_path: String,
    pub input_source: String,
    pub search_paths: Vec<String>,
    pub message_collector: MessageCollector
}

pub fn compile(request: CompileRequest) {
    let output_dir = "/dummy".to_string();
    println!("Compiling {}...", request.input_path);
    let mut sopts = config::basic_options();
    sopts.search_paths = SearchPaths::new();
    for search_path in request.search_paths {
        sopts.search_paths.add_path(search_path.as_slice());
    }
    let odir = Some(Path::new(&output_dir));
    let ofile = None;
    let input = Input::Str(request.input_source);
    let input_file_path = Some(Path::new(&request.input_path));
    let codemap = CodeMap::new();
    let diagnostic_handler = diagnostic::mk_handler(true, Box::new(request.message_collector));
    let span_diagnostic_handler = diagnostic::mk_span_handler(diagnostic_handler, codemap);
    let sess = session::build_session_(sopts, input_file_path, span_diagnostic_handler);
    let cfg= config::build_configuration(&sess);
    let plugins = None;
    let mut controller = CompileController::basic();
    controller.after_analysis.stop = Compilation::Stop;
    compile_input(sess, cfg, &input, &odir, &ofile, plugins, controller);
}

// Modified from driver.rs to call our modified phase_1_parse_input function. Also
// removes unused parts.
pub fn compile_input(sess: Session,
                     cfg: CrateConfig,
                     input: &Input,
                     _: &Option<Path>,
                     _: &Option<Path>,
                     addl_plugins: Option<Vec<String>>,
                     control: CompileController) {

    // We need nested scopes here, because the intermediate results can keep
    // large chunks of memory alive and we want to free them as soon as
    // possible to keep the peak memory usage low
    let (expanded_crate, id) = {
        let krate = phase_1_parse_input(&sess, cfg, input);
        let id = link::find_crate_name(Some(&sess), &krate.attrs[..], input);
        let expanded_crate = match driver::phase_2_configure_and_expand(&sess, krate, &id[..], addl_plugins) {
                None => return,
                Some(k) => k
        };

        (expanded_crate, id)
    };

    let mut forest = Forest::new(expanded_crate);
    let arenas = CtxtArenas::new();
    let ast_map = driver::assign_node_ids_and_map(&sess, &mut forest);

    driver::phase_3_run_analysis_passes(sess, ast_map, &arenas, id, control.make_glob_map);
}

// Modified from driver.rs to not use "anon" as the source name because it makes the
// parser fail on module references (can't source the other file relative to this file
// because it doesn't know the path: for source strings it uses the name as the path)
pub fn phase_1_parse_input(sess: &Session, cfg: CrateConfig, input: &Input) -> Crate {
    // These may be left in an incoherent state after a previous compile.
    // `clear_tables` and `get_ident_interner().clear()` can be used to free
    // memory, but they do not restore the initial state.
    syntax::ext::mtwt::reset_tables();
    token::reset_ident_interner();

    let krate = time(sess.time_passes(), "parsing", (), |_| {
        match *input {
            Input::File(ref file) => {
                parse::parse_crate_from_file(&(*file), cfg.clone(), &sess.parse_sess)
            }
            Input::Str(ref src) => {
                let source_path = match sess.local_crate_source_file {
                    Some(ref source_path) => match source_path.as_str() {
                        Some(source_path_str) => source_path_str.to_string(),
                        None => driver::anon_src().to_string()
                    },
                    None => driver::anon_src().to_string()
                };
                parse::parse_crate_from_source_str(source_path,
                                                   src.to_string(),
                                                   cfg.clone(),
                                                   &sess.parse_sess)
            }
        }
    });

    krate
}

