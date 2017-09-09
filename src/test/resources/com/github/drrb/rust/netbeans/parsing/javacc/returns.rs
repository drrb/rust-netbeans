fn simple_return() {
    let x = 1;
    return
}

fn return_statement() {
    let x = 1;
    return;
}

fn implicit_return() -> uint {
    1
}

fn explicit_return() -> uint {
    return 1;
}

fn weird_return() -> uint {
    let x = return 1;
}

fn explicit_double_return() -> uint {
    if true {
        return 1;
    } else {
        return 2;
    }
}