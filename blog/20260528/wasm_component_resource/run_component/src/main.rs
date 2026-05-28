use std::env;
use wasmtime::component::{Component, HasSelf, Linker};
use wasmtime::{Engine, Store};

use sample1::cart::imports::{Host, add_to_linker};
use sample1::cart::types::{Amount, ItemId};

wasmtime::component::bindgen!("root" in "../sample1/wit");

#[derive(Default)]
struct State;

impl Host for State {
    fn find_price(&mut self, item: ItemId) -> Option<Amount> {
        Some(100. * item.len() as f64)
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

    let cart = root.sample1_cart_exports().cart();

    let c1 = cart.call_constructor(&mut store, &"cart-1".to_string())??;
    let c2 = cart.call_add_item(&mut store, c1, &"A1".to_string(), 1)??;
    let c3 = cart.call_add_item(&mut store, c2, &"B-234".to_string(), 2)??;

    println!("c1_lines={:?}", cart.call_get_lines(&mut store, c1));
    println!("c2_lines={:?}", cart.call_get_lines(&mut store, c2));
    println!("c3_lines={:?}", cart.call_get_lines(&mut store, c3));
    
    println!("c1={:?}", c1);

    println!("add_item 0 qty={:?}", cart.call_add_item(&mut store, c1, &"C45".to_string(), 0));

    c2.resource_drop(&mut store)?;

    println!("after drop c1_lines={:?}", cart.call_get_lines(&mut store, c1));
    println!("after drop c2_lines={:?}", cart.call_get_lines(&mut store, c2));
    println!("after drop c3_lines={:?}", cart.call_get_lines(&mut store, c3));

    Ok(())
}
