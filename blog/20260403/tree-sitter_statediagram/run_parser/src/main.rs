use std::env::args;

use tree_sitter::wasmtime::{Engine, Result};
use tree_sitter::{Parser, WasmStore};

const GRAMMAR: &[u8] =
    include_bytes!("../../tree-sitter-state_diagram/tree-sitter-state_diagram.wasm");

fn main() -> Result<()> {
    let text = args().nth(1).unwrap_or_default();

    let engine = Engine::default();
    let mut store = WasmStore::new(&engine)?;

    let diagram = store.load_language("state_diagram", GRAMMAR)?;

    let mut parser = Parser::new();
    parser.set_wasm_store(store)?;
    parser.set_language(&diagram)?;

    let tree = parser.parse(&text, None);

    if let Some(t) = tree {
        println!(
            "tree={:?}, language={:?}, to_sexp={}",
            t,
            t.root_node().language().name(),
            t.root_node().to_sexp()
        );
    }

    Ok(())
}
