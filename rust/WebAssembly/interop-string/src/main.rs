
#[link(wasm_import_module = "sample")]
extern {
    fn log(ptr: *const u8, len: usize);
    fn message();
}

#[no_mangle]
extern fn _new_string(size: usize) -> *mut u8 {
    let v = vec![0; size];

    Box::into_raw(v.into_boxed_slice()) as *mut u8
}

#[no_mangle]
extern fn _return_string(ptr: *const u8, len: usize) {
    unsafe {
        let slice = std::slice::from_raw_parts(ptr, len);
        let s = std::str::from_utf8_unchecked(slice);

        let msg = format!("returned string: {}", s);
        log(msg.as_ptr(), msg.len());
    }
}

fn main() {
    unsafe {
        message();
    }
}
