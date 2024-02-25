use tch::{
    nn::{self, Module},
    Device, Tensor,
};

use std::env;

#[derive(Debug)]
struct Net {
    fc1: nn::Linear,
    fc2: nn::Linear,
}

impl Net {
    fn new(vs: &nn::Path) -> Self {
        let fc1 = nn::linear(vs / "fc1", 2, 3, Default::default());
        let fc2 = nn::linear(vs / "fc2", 3, 1, Default::default());

        Self { fc1, fc2 }
    }
}

impl nn::Module for Net {
    fn forward(&self, xs: &Tensor) -> Tensor {
        xs.apply(&self.fc1).relu().apply(&self.fc2)
    }
}

fn main() -> Result<(), Box<dyn std::error::Error>> {
    let device = if let Some(_) = env::args().skip(1).next() {
        Device::Mps
    } else {
        Device::Cpu
    };

    let mut vs = nn::VarStore::new(device);
    let net = Net::new(&vs.root());

    vs.load("sample.safetensors")?;

    let x = Tensor::from_slice(&[1.0f32, 2.0]).to(device).view((1, 2));

    let y = net.forward(&x);

    y.print();

    Ok(())
}
