use candle_core::Tensor;
use candle_nn::{Linear, Module, VarBuilder, linear};

type ModelError = Box<dyn std::error::Error>;

pub struct Model {
    layer1: Linear,
    layer2: Linear,
}

impl Model {
    pub fn new(hidden_num: usize, vs: &VarBuilder) -> Result<Self, ModelError> {
        let layer1 = linear(4, hidden_num, vs.pp("layer1"))?;
        let layer2 = linear(hidden_num, 3, vs.pp("layer2"))?;

        Ok(Self { layer1, layer2 })
    }

    pub fn forward(&self, xs: &Tensor) -> Result<Tensor, ModelError> {
        let xs1 = self.layer1.forward(xs)?.relu()?;
        let xs2 = self.layer2.forward(&xs1)?;

        Ok(xs2)
    }
}
