
use wasm_bindgen::prelude::*;

#[wasm_bindgen]
pub struct Data(String, i32);

#[wasm_bindgen]
impl Data {
    #[wasm_bindgen(constructor)]
    pub fn new(name: String, value: i32) -> Data {
        Data(name, value)
    }

    #[wasm_bindgen(getter)]
    pub fn name(&self) -> String {
        self.0.clone()
    }

    #[wasm_bindgen(getter)]
    pub fn value(&self) -> i32 {
        self.1
    }

    pub fn show(&self) -> String {
        format!("Data ({}, {})", self.0, self.1)
    }
}

#[wasm_bindgen(inspectable)]
pub struct Data2(String, i32);

#[wasm_bindgen]
impl Data2 {
    #[wasm_bindgen(constructor)]
    pub fn new(name: String, value: i32) -> Data2 {
        Data2(name, value)
    }

    #[wasm_bindgen(getter)]
    pub fn name(&self) -> String {
        self.0.clone()
    }

    #[wasm_bindgen(getter)]
    pub fn value(&self) -> i32 {
        self.1
    }

    pub fn show(&self) -> String {
        format!("Data2 ({}, {})", self.0, self.1)
    }
}
