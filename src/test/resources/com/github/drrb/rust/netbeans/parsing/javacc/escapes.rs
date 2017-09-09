fn escapes() {
    let slash = "a\\b";
    let quote = "a\"b";
    let newline = "a\nb";
    let carriage_return = "a\rb";
    let tab = "a\tb";
    let null = "a\0b";
    let hex = "a\x00b";
    let uni = "a\u{0000ab}";

    let slash_char = '\\';
    let quote_char = '\'';
    let newline_char = '\n';
    let carriage_return_char = '\r';
    let tab_char = '\t';
    let null_char = '\0';
    let hex_char = '\x10';
    let uni_char = '\u{0000ab}';
}