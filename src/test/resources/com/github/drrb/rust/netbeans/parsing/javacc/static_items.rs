static SOME_VARIABLE: bool = 0x1000 == 1;
pub static OTHER_VARIABLE: bool = false;
static mut TOP_OF_HEAP: *const u8 = ptr::null();