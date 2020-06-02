
use wasm_bindgen::prelude::*;

#[wasm_bindgen]
extern {
    #[wasm_bindgen(js_namespace = console)]
    fn log(s: &str);

    fn func1(s: &str);
}

#[wasm_bindgen(module = "/src-js/func.js")]
extern {
    fn func2(s: &str);
}

#[wasm_bindgen]
pub fn run(msg: &str) {
    log(msg);

    func1(msg);
    func2(msg);
}
