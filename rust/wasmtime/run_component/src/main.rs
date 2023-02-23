
use anyhow::Result;
use wasmtime::component::{Component, Linker};
use wasmtime::{Config, Engine, Store};

use std::env;

wasmtime::component::bindgen!("sample");

#[derive(Default)]
struct State {}

impl SampleImports for State {
    fn log(&mut self, level: LogLevel, msg: String) -> anyhow::Result<()> {
        println!("level={:?}, message={}", level, msg);
        Ok(())
    }
}

fn main() -> Result<()> {
    let file = env::args().skip(1).next().unwrap_or_default();

    let mut config = Config::new();
    config.wasm_component_model(true);

    let engine = Engine::new(&config)?;

    let component = Component::from_file(&engine, file)?;

    let mut linker = Linker::new(&engine);

    Sample::add_to_linker(&mut linker, |s: &mut State| s)?;

    let mut store = Store::new(&engine, State::default());

    let (bindings, _) = Sample::instantiate(&mut store, &component, &linker)?;

    bindings.call_run(&mut store)?;

    Ok(())
}
