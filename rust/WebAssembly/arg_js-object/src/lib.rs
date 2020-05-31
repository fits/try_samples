
use wasm_bindgen::prelude::*;

#[wasm_bindgen]
extern {
    #[wasm_bindgen(js_namespace = console)]
    fn log(s: &str);
}

#[wasm_bindgen]
extern {
    pub type Data;

    #[wasm_bindgen(method)]
    fn show(this: &Data);

    #[wasm_bindgen(method, getter)]
    fn name(this: &Data) -> String;

    #[wasm_bindgen(method, getter)]
    fn value(this: &Data) -> i32;
}

#[wasm_bindgen]
pub fn run(d: &Data) {
    let s = format!("*** name: {}, value: {}", d.name(), d.value());
    log(&s);

    d.show();
}
