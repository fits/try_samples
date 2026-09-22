use candle_core::{D, Device, Tensor};
use candle_nn::{
    AdamW, Linear, Module, Optimizer, ParamsAdamW, VarBuilder, VarMap, linear, loss, ops,
};

mod dataset;
use dataset::Dataset;

const EPOCH: usize = 100;

struct Model {
    layer1: Linear,
    layer2: Linear,
}

impl Model {
    fn new(hidden_num: usize, vs: &VarBuilder) -> Result<Self, AppError> {
        let layer1 = linear(4, hidden_num, vs.pp("layer1"))?;
        let layer2 = linear(hidden_num, 3, vs.pp("layer2"))?;

        Ok(Self { layer1, layer2 })
    }

    fn forward(&self, xs: &Tensor) -> Result<Tensor, AppError> {
        let xs1 = self.layer1.forward(xs)?.relu()?;
        let xs2 = self.layer2.forward(&xs1)?;

        Ok(xs2)
    }
}

type AppError = Box<dyn std::error::Error>;

fn main() -> Result<(), AppError> {
    let device = Device::metal_if_available(0)?;

    let ds = Dataset::load("./iris.csv", &device)?;

    let (train_data, test_data) = ds.split_train_test(0.2, &device)?;

    let varmap = VarMap::new();
    let vs = VarBuilder::from_varmap(&varmap, candle_core::DType::F32, &device);

    let model = Model::new(8, &vs)?;

    let adam_config = ParamsAdamW {
        lr: 0.01,
        ..Default::default()
    };

    let mut adam = AdamW::new(varmap.all_vars(), adam_config)?;

    for epoch in 0..EPOCH {
        let output = model.forward(&train_data.data)?;
        let output = ops::log_softmax(&output, D::Minus1)?;

        let loss = loss::nll(&output, &train_data.labels)?;

        adam.backward_step(&loss)?;

        let test_output = model.forward(&test_data.data)?;

        let acc_count = test_output
            .argmax(D::Minus1)?
            .eq(&test_data.labels)?
            .sum_all()?
            .to_scalar::<u8>()?;

        let acc = acc_count as f32 / test_data.size as f32;

        println!(
            "epoch={:3}, loss={:.3}, accuracy={acc:.3}",
            epoch + 1,
            loss.to_scalar::<f32>()?,
        );
    }

    Ok(())
}
