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

fn return_call() {
    return hello().blah();
}

fn return_expression() {
    return 1 + 1;
}

fn weird_return_expression() {
    1 + return 1;
}

fn return_expresssion_maybe() {
    return ! true;
}

fn weird_returnx() {
    return - 1;
}

// TODO: this is supported by the Rust LL(1) grammar. But should it be?
//fn return_dot() {
//    return .hello().blah();
//}