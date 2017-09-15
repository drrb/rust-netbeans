fn main() {
    let mut x = 5; // mut x: i32
    let mut done = false; // mut done: bool

    while !done {
//        x += x - 3;

        println!("{}", x);

        if x % 5 == 0 {
            done = true;
        }
    }
}