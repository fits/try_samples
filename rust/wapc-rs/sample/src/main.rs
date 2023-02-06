
use wapc::{WapcHost, errors::Error};
use wasmtime_provider::WasmtimeEngineProviderBuilder;

use std::env;
use std::fs;

fn error(msg: &str) -> Error {
    Error::General(msg.to_string())
}

fn main() -> Result<(), Error> {
    env_logger::init();

    let mut args = env::args().skip(1);

    let wasm_file = args.next().ok_or(error("wasm file"))?;
    let func_name = args.next().ok_or(error("function name"))?;
    let payload = args.next().ok_or(error("payload"))?;

    let buf = fs::read(wasm_file)?;

    let engine = WasmtimeEngineProviderBuilder::new()
        .module_bytes(&buf)
        .build()?;

    let host = WapcHost::new(Box::new(engine), None)?;

    let res = host.call(&func_name, payload.as_bytes())?;

    if let Ok(r) = String::from_utf8(res) {
        println!("response: {}", r);
    }

    Ok(())
}
