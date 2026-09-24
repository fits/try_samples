use candle_core::{D, Device, Module, Tensor};
use candle_nn::{VarBuilder, VarMap};

use std::env;
use std::str::FromStr;

mod model;
use model::Model1;

type AppError = Box<dyn std::error::Error>;

fn main() -> Result<(), AppError> {
    let device = Device::metal_if_available(0)?;

    let mut args = env::args().skip(1);

    let sepal_length = to_f32(args.next())?;
    let sepal_width = to_f32(args.next())?;
    let petal_length = to_f32(args.next())?;
    let petal_width = to_f32(args.next())?;

    println!("params: {sepal_length}, {sepal_width}, {petal_length}, {petal_width}");

    let mut varmap = VarMap::new();

    let vs = VarBuilder::from_varmap(&varmap, candle_core::DType::F32, &device);

    let model = Model1::new(&vs)?;

    varmap.load("model1.safetensors")?;

    let input = Tensor::from_vec(
        vec![sepal_length, sepal_width, petal_length, petal_width],
        (1, 4),
        &device,
    )?;

    let output = model.forward(&input)?;

    let res = output.argmax(D::Minus1)?.to_vec1::<u32>()?;

    println!("label = {}", res.first().unwrap());

    Ok(())
}

fn to_f32(v: Option<String>) -> Result<f32, AppError> {
    let res = f32::from_str(&v.unwrap_or_default())?;
    Ok(res)
}
