use wasmtime::component::{Component, Linker};
use wasmtime::{Config, Engine, Store};

use std::env;

type Result<T> = std::result::Result<T, Box<dyn std::error::Error>>;

fn main() -> Result<()> {
    let mut args = env::args().skip(1);

    let file = args.next().unwrap_or_default();
    let p1: i32 = args.next().unwrap_or("1".to_string()).parse()?;
    let p2: i32 = args.next().unwrap_or("2".to_string()).parse()?;

    let mut config = Config::new();
    config.wasm_component_model(true);

    let engine = Engine::new(&config)?;

    let component = Component::from_file(&engine, file)?;

    let mut store = Store::new(&engine, ());

    let linker = Linker::new(&engine);

    let instance = linker.instantiate(&mut store, &component)?;

    let f = instance.get_typed_func::<(i32, i32), (i32,)>(&mut store, "f")?;

    let r = f.call(&mut store, (p1, p2))?;

    println!("result = {:?}", r);

    Ok(())
}
