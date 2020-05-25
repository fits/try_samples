
use std::ffi::CString;
use std::os::raw::c_char;

#[link(wasm_import_module = "sample")]
extern {
    fn count_up() -> i32;
    fn log(ptr: *const c_char, len: usize);
}

fn main() {
    let n = 5;

    for i in 1..=n {
        unsafe {
            let s = format!("count-{} : {}", i, count_up());

            let msg = CString::new(s.clone()).unwrap();

            log(msg.as_ptr(), s.len());
        }
    }
}