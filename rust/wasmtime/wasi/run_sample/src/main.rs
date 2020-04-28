
use std::env;
use anyhow::{anyhow, Result};
use wasmtime::*;
use wasmtime_wasi::{Wasi, WasiCtx};

fn main() -> Result<()> {
    let wasm_file = env::args().nth(1).ok_or(anyhow!("no wasm file"))?;

    let store = Store::default();

    let module = Module::from_file(&store, wasm_file)?;

    let ctx = WasiCtx::new(env::args())?;
    let wasi = Wasi::new(&store, ctx);

    let imports: Vec<_> = module.imports().iter().filter_map(|i| 
        match i.module() {
            "wasi_snapshot_preview1" => 
                wasi.get_export(i.name()).map(|e| Extern::from(e.clone())),
            _ => None,
        }
    ).collect();

    let instance = Instance::new(&module, &imports)?;

    let start = instance.get_export("_start").and_then(Extern::func);

    for f in start {
        f.call(&[])?;
        //f.get0::<()>()?()?;
    }

    Ok(())
}
