use std::env::args;

use tree_sitter::wasmtime::{Engine, Result};
use tree_sitter::{Parser, WasmStore};

const GRAMMAR: &[u8] = include_bytes!("../../state_parser/tree-sitter-state_parser.wasm");

fn main() -> Result<()> {
    let code = args().nth(1).unwrap_or_default();

    let engine = Engine::default();
    let mut store = WasmStore::new(&engine)?;

    let state_parser = store.load_language("state_parser", GRAMMAR)?;

    let mut parser = Parser::new();
    parser.set_wasm_store(store)?;
    parser.set_language(&state_parser)?;

    let tree = parser.parse(&code, None);

    if let Some(t) = tree {
        println!(
            "tree={:?}, language={:?}, root_node.to_sexp={}",
            t,
            t.root_node().language().name(),
            t.root_node().to_sexp()
        );

        let mut cur_node = t.root_node().child(0);

        loop {
            if let Some(n) = cur_node {
                println!("node={}", n);
                cur_node = n.next_sibling();
            } else {
                break;
            }
        }
    }

    Ok(())
}
