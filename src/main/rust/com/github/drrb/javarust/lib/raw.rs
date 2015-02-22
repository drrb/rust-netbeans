use libc::c_char;
use std::ffi::CString;
use std::ffi::CStr;
use std::mem;
use std::str;

pub fn to_ptr(string: String) -> *const c_char {
    let cs = CString::new(string.as_bytes()).unwrap();
    let ptr = cs.as_ptr();
    // Tell Rust not to clean up the string while we still have a pointer to it.
    // Otherwise, we'll get a segfault.
    unsafe { mem::forget(cs) };
    ptr
}

pub fn to_string(pointer: *const c_char) -> String {
    let bytes = unsafe { CStr::from_ptr(pointer).to_bytes() };
    str::from_utf8(bytes).unwrap().to_string()
}
