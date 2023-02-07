
use wapc_guest as wapc;

#[no_mangle]
fn wapc_init() {
    wapc::register_function("hello", hello);
}

fn hello(req: &[u8]) -> wapc::CallResult {
    let msg = std::str::from_utf8(req)?;

    wapc::console_log(&format!("called hello: {}", msg));

    let res = format!("return-hello-{}", msg);

    Ok(res.into())
}
