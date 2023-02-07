
#[link(wasm_import_module = "wapc")]
extern {
    fn __console_log(ptr: *const u8, len: usize);
    fn __guest_request(op_ptr: *mut u8, ptr: *mut u8);
    fn __guest_response(ptr: *const u8, len: usize);
}

#[no_mangle]
extern fn __guest_call(op_len: i32, msg_len: i32) -> i32 {
    let mut op_buf = vec![0; op_len as _];
    let mut msg_buf = vec![0; msg_len as _];

    unsafe {
        __guest_request(op_buf.as_mut_ptr(), msg_buf.as_mut_ptr());
    }

    let op = String::from_utf8(op_buf).unwrap_or(String::default());
    let payload = String::from_utf8(msg_buf).unwrap_or(String::default());

    let log_msg = format!("called operation={}, payload={}", op, payload);

    unsafe {
        __console_log(log_msg.as_ptr(), log_msg.len());
    }

    let res = format!("response-{}-{}", op, payload);

    unsafe {
        __guest_response(res.as_ptr(), res.len());
    }

    1
}
