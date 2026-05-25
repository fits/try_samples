use chrono::prelude::*;
use std::env;
use wasmtime::component::{Component, HasSelf, Linker};
use wasmtime::{Engine, Store};

use crate::resource_test::order::imports::{Host, add_to_linker};

wasmtime::component::bindgen!("root" in "../wit");

#[derive(Default)]
struct State;

impl Host for State {
    fn now_datestring(&mut self) -> Datetime {
        format!("{}", Local::now().format("%+"))
    }
}

fn main() -> Result<(), Box<dyn std::error::Error>> {
    let file = env::args().skip(1).next().unwrap_or_default();

    let engine = Engine::default();
    let component = Component::from_file(&engine, file)?;

    let mut linker = Linker::new(&engine);
    add_to_linker::<_, HasSelf<_>>(&mut linker, |s| s)?;

    let mut store = Store::new(&engine, State::default());

    let root = Root::instantiate(&mut store, &component, &linker)?;

    let order = root.resource_test_order_exports().order();

    let r1 = order.call_constructor(&mut store, &"order-1".to_string())??;
    let r2 = order.call_order_item(&mut store, r1, &"item-A".to_string(), 2)??;

    r1.resource_drop(&mut store)?;

    let r3 = order.call_order_item(&mut store, r2, &"item-B".to_string(), 3)??;
    let r4 = order.call_ordered_items(&mut store, r3)?;

    println!("result={:?}", r4);

    Ok(())
}
