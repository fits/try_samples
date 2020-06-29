
use wasm_bindgen::prelude::*;
use wasm_bindgen_futures::JsFuture;
use js_sys::Promise;

use serde::{Deserialize, Serialize};

#[derive(Debug, Deserialize, Serialize)]
struct Data<T>{
    value: T,
}

type StrData = Data<String>;

#[wasm_bindgen]
extern {
    #[wasm_bindgen(js_namespace = console)]
    fn log(s: String);

    #[wasm_bindgen(js_namespace = sample)]
    fn initial_value() -> Promise;
}

#[wasm_bindgen]
pub async fn sample(v: Promise) -> Result<JsValue, JsValue> {
    let data = JsFuture::from(v)
        .await
        .and_then(|v| from_jsvalue::<StrData>(&v))?;

    let init_data = JsFuture::from(initial_value())
        .await
        .and_then(|v| from_jsvalue::<StrData>(&v))?;

    log(format!("*** init={:?}, arg={:?}", init_data, data));

    let res = StrData { value: format!("{}/{}", init_data.value, data.value) };

    to_jsvalue(&res)
}

fn to_jsvalue<T>(v: &T) -> Result<JsValue, JsValue> 
where
    T: serde::Serialize,
{
    JsValue::from_serde(v)
        .map_err(|e| JsValue::from(e.to_string()))
}

fn from_jsvalue<T>(v: &JsValue) -> Result<T, JsValue> 
where
    T: for<'a> serde::Deserialize<'a>
{
    JsValue::into_serde::<T>(v)
        .map_err(|e| JsValue::from(e.to_string()))
}
