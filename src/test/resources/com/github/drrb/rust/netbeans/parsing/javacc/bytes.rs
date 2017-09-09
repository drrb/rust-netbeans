fn bytes() {
    let b = b'x';
    let b = b'\n';
    let b = b'\t';
    let b = b'\0';
    let b = b'\\';
    let b = b'\'';

    let bs = b"abcd";
    let bs = b"ab\tcd";
    let bs = b"ab\ncd";
    let bs = b"ab\0cd";
    let bs = b"ab\\cd";
    let bs = b"ab\"cd";

    let br = br"abcd";
    let br = br"abc\nd";  //No escape expected
    let br = br#"abcd"#;
    let br = br#"ab\ncd"#;  //No escape expected
    let br = br##"ab\ncd"##;  //No escape expected
    let br = br##"ab"#"d"##;
}