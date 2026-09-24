use candle_core::{D, Device};
use candle_nn::{AdamW, Optimizer, ParamsAdamW, VarBuilder, VarMap, loss};

mod dataset;
use dataset::Dataset;

mod model;
use model::Model;

const EPOCH: usize = 100;

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
        // let output = ops::log_softmax(&output, D::Minus1)?;
        // let loss = loss::nll(&output, &train_data.labels)?;

        let loss = loss::cross_entropy(&output, &train_data.labels)?;

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

    varmap.save("model.safetensors")?;

    Ok(())
}
