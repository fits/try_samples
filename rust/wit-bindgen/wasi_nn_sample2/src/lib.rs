use exports::wasi::http::incoming_handler::{Guest, IncomingRequest, ResponseOutparam};
use wasi::http::types::{ErrorCode, Headers, OutgoingResponse};
use wasi::nn::graph::{ExecutionTarget, GraphEncoding, load};
use wasi::nn::tensor::{Tensor, TensorType};

use std::fs;

wit_bindgen::generate!({
    world: "eval",
    generate_all
});

struct Host;

impl Guest for Host {
    fn handle(_request: IncomingRequest, response_out: ResponseOutparam) -> () {
        let res = evaluate()
            .and_then(|x| to_output(&x))
            .map_err(|e| ErrorCode::InternalError(Some(e.to_string())));

        ResponseOutparam::set(response_out, res);

        ()
    }
}

type Result<T> = std::result::Result<T, Box<dyn std::error::Error>>;

fn evaluate() -> Result<f32> {
    let model = fs::read("model/sample.onnx")?;

    let graph = load(&[model], GraphEncoding::Onnx, ExecutionTarget::Cpu)
        .map_err(|e| format!("{:?}", e))?;

    let ctx = graph
        .init_execution_context()
        .map_err(|e| format!("{:?}", e))?;

    let data = [1.0f32.to_le_bytes(), 2.0f32.to_le_bytes()].concat();

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
