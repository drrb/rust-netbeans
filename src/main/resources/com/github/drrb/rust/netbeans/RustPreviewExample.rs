/**
 * Say hello
 */
fn greet(recipient: &str) -> ~str {
    // Print out the greetings
    for 2.times |number| {
        io::println(fmt!("%n: Hello, %s", number, recipient));
    }
}
