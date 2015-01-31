use rustc::session::config::Input;
use rustc::session::config;
use rustc::session::search_paths::SearchPaths;
use rustc::session;
use rustc_driver::driver::CompileController;
use rustc_driver::driver;
use rustc_driver;
use syntax::codemap::CodeMap;
use syntax::diagnostic;

pub fn compile(input_file: String, output_dir: String) {
    println!("Compiling, {} to {}...", input_file, output_dir);
    let registry = rustc_driver::diagnostics_registry();
    let mut sopts = config::basic_options();
    sopts.search_paths = SearchPaths::new();
    sopts.search_paths.add_path("/usr/local/lib/rustlib/x86_64-apple-darwin/lib");
    let odir = Some(Path::new(&output_dir));
    let ofile = None;
    let (input, input_file_path) = (Input::File(Path::new(&input_file)), Some(Path::new(&input_file)));
    let codemap = CodeMap::new();
    let can_print_warnings = true;
    let diagnostic_handler = diagnostic::default_handler(sopts.color, Some(registry), can_print_warnings);
    let span_diagnostic_handler = diagnostic::mk_span_handler(diagnostic_handler, codemap);
    let sess = session::build_session_(sopts, input_file_path, span_diagnostic_handler);
    let cfg= config::build_configuration(&sess);
    let plugins = None;
    let mut controller = CompileController::basic();
    controller.after_analysis.stop = true;
    driver::compile_input(sess, cfg, &input, &odir, &ofile, plugins, controller);
}
