use wasmtime::component::{Component, Linker};
use wasmtime::{Config, Engine, Store};

use std::env;

wasmtime::component::bindgen!("sample" in "../sample/wit");

#[derive(Default)]
struct State {}

fn main() -> Result<(), Box<dyn std::error::Error>> {
    let file = env::args().skip(1).next().unwrap_or_default();

    let mut config = Config::new();
    config.wasm_component_model(true);

    let engine = Engine::new(&config)?;

    let component = Component::from_file(&engine, file)?;

    let linker = Linker::new(&engine);

    let mut store = Store::new(&engine, State::default());

    let (bindings, _) = Sample::instantiate(&mut store, &component, &linker)?;

    let s1 = bindings.call_create(&mut store, &"cart1".to_string())?;
    println!("{:?}", s1);

    let s2 = bindings.call_change_qty(&mut store, &s1, &"item-1".to_string(), 2)?;
    println!("{:?}", s2);

    let s3 = bindings.call_change_qty(&mut store, &s2, &"item-2".to_string(), 1)?;
    println!("{:?}", s3);

    let s4 = bindings.call_change_qty(&mut store, &s3, &"item-2".to_string(), 5)?;
    println!("{:?}", s4);

    let s5 = bindings.call_change_qty(&mut store, &s4, &"item-1".to_string(), 0)?;
    println!("{:?}", s5);

    let s6 = bindings.call_change_qty(&mut store, &s5, &"item-2".to_string(), 0)?;
    println!("{:?}", s6);

    Ok(())
}
