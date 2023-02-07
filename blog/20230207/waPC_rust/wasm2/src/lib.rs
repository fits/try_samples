use wapc_guest as wapc;

#[no_mangle]
fn wapc_init() {
    wapc::register_function("greet", greet);
}

fn greet(req: &[u8]) -> wapc::CallResult {
    let payload = std::str::from_utf8(req)?;

    wapc::console_log(&format!("called operation=greet, payload={}", payload));

    let res = format!("Hi, {} !", payload);

    Ok(res.into())
}
