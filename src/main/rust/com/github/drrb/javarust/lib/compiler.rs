use rustc_driver;
use rustc_driver::driver::CompileController;
use rustc::session::search_paths::SearchPaths;
use rustc::session::config::Input;
use rustc::session::config;
use rustc::session;
use rustc_driver::driver;

pub fn compile(input_file: String, output_dir: String) {
    println!("Compiling, {} to {}...", input_file, output_dir);
    let descriptions = rustc_driver::diagnostics_registry();
    let mut sopts = config::basic_options();
    sopts.search_paths = SearchPaths::new();
    sopts.search_paths.add_path("/usr/local/lib/rustlib/x86_64-apple-darwin/lib");
    let odir = Some(Path::new(&output_dir));
    let ofile = None;
    let (input, input_file_path) = (Input::File(Path::new(&input_file)), Some(Path::new(&input_file)));
    let sess = session::build_session(sopts, input_file_path, descriptions);
    let cfg = config::build_configuration(&sess);
    let plugins = None;
    let mut controller = CompileController::basic();
    controller.after_analysis.stop = true;
    driver::compile_input(sess, cfg, &input, &odir, &ofile, plugins, controller);
}
