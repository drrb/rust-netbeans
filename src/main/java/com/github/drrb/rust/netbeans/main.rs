fn say_hello(name: ~str) -> int {
    for 10.times |time| {
        io::println(fmt!("Hello, %?", name));
    }
}