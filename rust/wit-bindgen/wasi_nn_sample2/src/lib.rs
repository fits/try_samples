use exports::wasi::http::incoming_handler::{Guest, IncomingRequest, ResponseOutparam};
use wasi::http::types::{ErrorCode, Headers, OutgoingResponse};
use wasi::nn::graph::{ExecutionTarget, GraphEncoding, load};
use wasi::nn::tensor::{Tensor, TensorType};

use std::collections::HashMap;
use std::fs;

wit_bindgen::generate!({
    world: "eval",
    generate_all
});

struct Host;

impl Guest for Host {
    fn handle(request: IncomingRequest, response_out: ResponseOutparam) -> () {
        let res = to_params(&request)
            .and_then(|(a, b)| evaluate(a, b))
            .and_then(|x| to_output(&x))
            .map_err(|e| ErrorCode::InternalError(Some(e.to_string())));

        ResponseOutparam::set(response_out, res);

        ()
    }
}

type Result<T> = std::result::Result<T, Box<dyn std::error::Error>>;

fn to_params(request: &IncomingRequest) -> Result<(f32, f32)> {
    let path_query = request.path_with_query().ok_or("failed path")?;
    let (_, query) = path_query.split_once('?').ok_or("no querystring")?;

    let params = query
        .split('&')
        .filter_map(|x| x.split_once('='))
        .map(|(k, v)| (k.to_string(), v.to_string()))
        .collect::<HashMap<String, String>>();

    let a = extract_to(&params, "a")?;
    let b = extract_to(&params, "b")?;

    Ok((a, b))
}

fn extract_to(params: &HashMap<String, String>, key: &str) -> Result<f32> {
    let v = params.get(key).ok_or(format!("no params: {}", key))?;

    v.parse::<f32>().map_err(|e| e.into())
}

fn evaluate(a: f32, b: f32) -> Result<f32> {
    let model = fs::read("model/sample.onnx")?;

    let graph = load(&[model], GraphEncoding::Onnx, ExecutionTarget::Cpu)
        .map_err(|e| format!("{:?}", e))?;

    let ctx = graph
        .init_execution_context()
        .map_err(|e| format!("{:?}", e))?;

    let data = [a.to_le_bytes(), b.to_le_bytes()].concat();

    let tensor = Tensor::new(&[1, 2], TensorType::Fp32, &data);

    ctx.set_input("input", tensor)
        .map_err(|e| format!("{:?}", e))?;

    ctx.compute().map_err(|e| format!("{:?}", e))?;

    let output = ctx.get_output("output").map_err(|e| format!("{:?}", e))?;
    let output_data: [u8; 4] = output.data().try_into().map_err(|e| format!("{:?}", e))?;

    let res = f32::from_le_bytes(output_data);

    Ok(res)
}

fn to_output(value: &f32) -> Result<OutgoingResponse> {
    let res = format!(r#"{{ "result": {} }}"#, value);

    let h = Headers::new();
    h.append("content-length", res.len().to_string().as_bytes())?;

    let r = OutgoingResponse::new(h);

    let b = r.body().map_err(|_| "failed body")?;
    let w = b.write().map_err(|_| "failed write")?;

    w.write(res.as_bytes())?;
    w.flush()?;

    Ok(r)
}

export!(Host);
