use parser::MessageCollector;
use rustc::session::config::Input;
use rustc::session::config;
use rustc::session::search_paths::SearchPaths;
use rustc::session;
use rustc_driver::driver::CompileController;
use rustc_driver::driver;
use syntax::codemap::CodeMap;
use syntax::diagnostic;

pub fn compile(input_file: String, message_collector: MessageCollector) {
    let output_dir = "/dummy".to_string();
    println!("Compiling {}...", input_file);
    let mut sopts = config::basic_options();
    sopts.search_paths = SearchPaths::new();
    sopts.search_paths.add_path("/usr/local/lib/rustlib/x86_64-apple-darwin/lib");
    let odir = Some(Path::new(&output_dir));
    let ofile = None;
    let input = Input::File(Path::new(&input_file));
    let input_file_path = Some(Path::new(&input_file));
    let codemap = CodeMap::new();
    let diagnostic_handler = diagnostic::mk_handler(true, Box::new(message_collector));
    let span_diagnostic_handler = diagnostic::mk_span_handler(diagnostic_handler, codemap);
    let sess = session::build_session_(sopts, input_file_path, span_diagnostic_handler);
    let cfg= config::build_configuration(&sess);
    let plugins = None;
    let mut controller = CompileController::basic();
    controller.after_analysis.stop = true;
    driver::compile_input(sess, cfg, &input, &odir, &ofile, plugins, controller);
}
