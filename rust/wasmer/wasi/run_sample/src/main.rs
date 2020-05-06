
use std::env;
use std::io::prelude::*;
use std::fs::File;
use wasmer_runtime::{ error, compile };
use wasmer_wasi::{ generate_import_object_from_state, get_wasi_version };
use wasmer_wasi::state::WasiState;

fn to_error<E>(e: E) -> error::RuntimeError
where 
    E: Send + 'static
{
    error::RuntimeError(Box::new(e))
}

fn to_bytes(file: &String) -> error::Result<Vec<u8>> {
    let mut f = File::open(file).map_err(to_error)?;
    let mut buffer = Vec::new();

    f.read_to_end(&mut buffer).map_err(to_error)?;

    Ok(buffer)
}

fn main() -> error::Result<()> {
    let wasm_file = env::args().nth(1).ok_or(
        to_error("no wasm file")
    )?;

    let wasm_bytes = to_bytes(&wasm_file)?;

    let module = compile(&wasm_bytes)?;

    let version = get_wasi_version(&module, true)
                    .ok_or(to_error("failed get_wasi_version"))?;

    let state = WasiState::new("sample").build()
                            .map_err(to_error)?;

    let import_object = generate_import_object_from_state(state, version);

    let instance = module.instantiate(&import_object)?;

    instance.call("_start", &[])?;

    //let start: wasmer_runtime::Func<()> = instance.func("_start")?;
    //start.call()?;

    //instance.func::<(), ()>("_start")?.call()?;

    //instance.dyn_func("_start")?.call(&[])?;

    Ok(())
}