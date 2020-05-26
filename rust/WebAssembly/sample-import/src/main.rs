
#[link(wasm_import_module = "sample")]
extern {
    fn count_up() -> i32;
    fn log(ptr: *const u8, len: usize);
}

fn main() {
    let n = 5;

    for i in 1..=n {
        unsafe {
            let s = format!("カウント {} : {}", i, count_up());
            log(s.as_ptr(), s.len());
        }
    }
}
