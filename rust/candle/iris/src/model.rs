#![allow(dead_code)]

use candle_core::Tensor;
use candle_nn::{Dropout, Linear, Module, VarBuilder, linear};

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
        let xs = self.layer1.forward(xs)?.relu()?;
        let xs = self.layer2.forward(&xs)?;

        Ok(xs)
    }
}

pub struct Model1 {
    layer1: Linear,
    layer2: Linear,
}

impl Model1 {
    pub fn new(vs: &VarBuilder) -> Result<Self, ModelError> {
        let layer1 = linear(4, 8, vs.pp("layer1"))?;
        let layer2 = linear(8, 3, vs.pp("layer2"))?;

        Ok(Self { layer1, layer2 })
    }
}

impl candle_core::Module for Model1 {
    fn forward(&self, xs: &Tensor) -> candle_core::Result<Tensor> {
        let xs = self.layer1.forward(xs)?.relu()?;
        self.layer2.forward(&xs)
    }
}

pub struct Model2 {
    layer1: Linear,
    layer2: Linear,
    layer3: Linear,
    dropout: Dropout,
}

impl Model2 {
    pub fn new(vs: &VarBuilder) -> Result<Self, ModelError> {
        let layer1 = linear(4, 8, vs.pp("layer1"))?;
        let layer2 = linear(8, 12, vs.pp("layer2"))?;
        let layer3 = linear(12, 3, vs.pp("layer3"))?;
        let dropout = Dropout::new(0.2);

        Ok(Self {
            layer1,
            layer2,
            layer3,
            dropout,
        })
    }
}

impl candle_core::ModuleT for Model2 {
    fn forward_t(&self, xs: &Tensor, train: bool) -> candle_core::Result<Tensor> {
        let xs = self.layer1.forward_t(xs, train)?.relu()?;
        let xs = self.dropout.forward_t(&xs, train)?;
        let xs = self.layer2.forward_t(&xs, train)?;
        let xs = self.dropout.forward_t(&xs, train)?;
        let xs = self.layer3.forward_t(&xs, train)?;

        Ok(xs)
    }
}
