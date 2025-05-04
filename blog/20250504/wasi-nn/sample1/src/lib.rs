use exports::wasi::cli::run::Guest;
use wasi::nn::graph::{ExecutionTarget, GraphEncoding, load};
use wasi::nn::tensor::{Tensor, TensorType};

use std::fs;

wit_bindgen::generate!({
    world: "eval",
    generate_all
});

struct Host;

impl Guest for Host {
    fn run() -> Result<(), ()> {
        let res = evaluate().map_err(|e| {
            println!("ERROR: {}", e);
            ()
        })?;

        println!("output={}", res);

        Ok(())
    }
}

fn evaluate() -> Result<f32, Box<dyn std::error::Error>> {
    let model = fs::read("model/sample.onnx")?;

    let err_handler = |e: wasi::nn::errors::Error| format!("{:?}", e);

    let graph = load(&[model], GraphEncoding::Onnx, ExecutionTarget::Cpu).map_err(err_handler)?;

    let ctx = graph.init_execution_context().map_err(err_handler)?;

    let input = [1.1f32.to_le_bytes(), 2.2f32.to_le_bytes()].concat();
    let tensor = Tensor::new(&[1, 2], TensorType::Fp32, &input);
    ctx.set_input("input", tensor).map_err(err_handler)?;

    ctx.compute().map_err(err_handler)?;

    let output = ctx.get_output("output").map_err(err_handler)?;
    let output_data: [u8; 4] = output.data().try_into().map_err(|e| format!("{:?}", e))?;

    Ok(f32::from_le_bytes(output_data))
}

export!(Host);
