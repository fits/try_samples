use std::env::args;

use tree_sitter::{Parser, WasmStore};
use tree_sitter::wasmtime::{Engine, Result};

const GRAMMAR: &[u8] = include_bytes!("../../tree-sitter-simple/tree-sitter-simple.wasm");

fn main() -> Result<()> {
    let code = args().nth(1).unwrap_or_default();

    let engine = Engine::default();
    let mut store = WasmStore::new(&engine)?;

    let simple = store.load_language("simple", GRAMMAR)?;

    let mut parser = Parser::new();
    parser.set_wasm_store(store)?;
    parser.set_language(&simple)?;
    
    let tree = parser.parse(&code, None);

    if let Some(t) = tree {
        println!("source={}, tree={:?}, root_node.to_sexp={}", code, t, t.root_node().to_sexp());
    }

    Ok(())
}
