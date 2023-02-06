
#[link(wasm_import_module = "wapc")]
extern {
    fn __console_log(ptr: *const u8, len: usize);
    fn __guest_request(op_ptr: *mut u8, ptr: *mut u8);
    fn __guest_response(ptr: *const u8, len: usize);
}

#[no_mangle]
extern fn __guest_call(op_len: i32, req_len: i32) -> i32 {
    let mut op_buf = vec![0; op_len as _];
    let mut req_buf = vec![0; req_len as _];

    unsafe {
        __guest_request(op_buf.as_mut_ptr(), req_buf.as_mut_ptr());
    }

    let op = String::from_utf8(op_buf).unwrap_or(String::default());
    let req = String::from_utf8(req_buf).unwrap_or(String::default());

    let res = format!("result-{}", req);
    let log = format!("called func={}, payload={}", op, req);

    unsafe {
        __guest_response(res.as_ptr(), res.len());
        __console_log(log.as_ptr(), log.len());
    }

    1
}
