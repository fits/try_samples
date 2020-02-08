extern crate wasm_bindgen;

use wasm_bindgen::prelude::*;
use js_sys::Promise;

#[wasm_bindgen]
pub fn sample(msg: &str) -> Promise {
    let s = format!("message-{}!!!", msg);
    let v = JsValue::from_str(&s);

    Promise::resolve(&v)
}
