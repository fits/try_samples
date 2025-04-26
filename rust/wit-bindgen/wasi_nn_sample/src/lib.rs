use exports::wasi::cli::run::Guest;
use wasi::cli::stdout::get_stdout;
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
        let out = get_stdout();

        let res = evaluate().map_err(|e| {
            let _ = out.write(e.to_string().as_bytes());
            ()
        })?;

        let _ = out.write(format!("output={}", res).as_bytes());

        out.flush().map_err(|_| ())?;

        Ok(())
    }
}

fn evaluate() -> Result<f32, Box<dyn std::error::Error>> {
    let model = fs::read("model/sample.onnx")?;

    let graph = load(&[model], GraphEncoding::Onnx, ExecutionTarget::Cpu).map_err(|e| format!("{:?}", e))?;

    let ctx = graph.init_execution_context().map_err(|e| format!("{:?}", e))?;

    let data = [1.0f32.to_le_bytes(), 2.0f32.to_le_bytes()].concat();

    let tensor = Tensor::new(&[1, 2], TensorType::Fp32, &data);

    ctx.set_input("input", tensor).map_err(|e| format!("{:?}", e))?;

    ctx.compute().map_err(|e| format!("{:?}", e))?;

    let output = ctx.get_output("output").map_err(|e| format!("{:?}", e))?;
    let output_data: [u8; 4] = output.data().try_into().map_err(|e| format!("{:?}", e))?;

    let res = f32::from_le_bytes(output_data);

    Ok(res)
}

export!(Host);
