fn blocks() {
    let x = 1;
    {
        let y = 2;
    }
    {
        let y = 3;
    }
}

fn block_expressions() {
    let z = {
        let a = 1;
        2
    };
}