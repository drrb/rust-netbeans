fn strings() {
    let s = "normal string";
    let s = "ab\n\t\r\0\"cd";
    let rs = r"ab\n\t\r\0cd"; //No escapes
    let rs = r#"ab\n\t\r\0cd"#; //No escapes
    let rs = r##"ab\n\t\r\0cd"##; //No escapes
    let rs = r##"ab"#"cd"##; //No escapes
    let rs = r"ab♞cd";
}