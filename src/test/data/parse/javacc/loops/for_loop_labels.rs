fn main() {
    'outer: for x in 0..10 {
        'inner: for y in 0..10 {
            if x % 2 == 0 { continue 'outer; };
            if y % 2 == 0 { continue 'inner; };
        }
    }
}