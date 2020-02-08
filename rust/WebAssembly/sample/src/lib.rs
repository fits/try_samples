extern crate wasm_bindgen;

use wasm_bindgen::prelude::*;

#[wasm_bindgen]
pub fn sample(msg: &str) -> JsValue {
    let s = format!("message-{}!!!", msg);
    JsValue::from_str(&s)
}
