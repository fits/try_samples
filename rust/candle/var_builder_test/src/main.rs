use candle_core::Device;
use candle_nn::{Linear, VarBuilder, VarMap, linear};

type AppError = Box<dyn std::error::Error>;

#[allow(dead_code)]
pub struct Model {
    layer1: Linear,
    layer2: Linear,
}

impl Model {
    pub fn new(vs: &VarBuilder) -> Result<Self, AppError> {
        let l1 = vs.pp("layer1");

        let layer1 = linear(4, 8, l1.clone())?;
        let layer2 = linear(8, 3, vs.pp("layer2"))?;

        println!(
            "* layer1 weight={}, bias={}",
            l1.contains_tensor("weight"),
            l1.contains_tensor("bias")
        ); // true, true

        Ok(Self { layer1, layer2 })
    }
}

fn main() -> Result<(), AppError> {
    let device = Device::Cpu;

    let varmap = VarMap::new();
    let vs = VarBuilder::from_varmap(&varmap, candle_core::DType::F32, &device);

    println!("before model new: {:?}", varmap.data());

    let _ = Model::new(&vs)?;

    println!("after model new: {:?}", varmap.data());

    println!(
        "* weight={}, bias={}",
        vs.contains_tensor("weight"),
        vs.contains_tensor("bias")
    ); // false, false

    println!("* a1={}", vs.contains_tensor("a1")); // false

    let _ = vs.get((2, 1), "a1")?;

    println!("after vs.get: {:?}", varmap.data());

    println!("* a1={}", vs.contains_tensor("a1")); // true

    Ok(())
}
