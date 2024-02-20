use tch::{
    nn::{self, Module, OptimizerConfig},
    Device, Kind, Tensor,
};

use std::f64;

mod dataset;
use dataset::Dataset;

fn main() -> Result<(), Box<dyn std::error::Error>> {
    let device = Device::Cpu;

    let ds = Dataset::load("iris.csv")?;

    let idx =
        Tensor::randperm(ds.size, (Kind::Int64, device)).hsplit_array([ds.size - (ds.size / 10)]);

    let train_idx = idx.first().unwrap();
    let test_idx = idx.last().unwrap();

    let train_data = ds.data.index_select(0, &train_idx);
    let train_labels = ds.labels.index_select(0, &train_idx);

    let test_data = ds.data.index_select(0, &test_idx);
    let test_labels = ds.labels.index_select(0, &test_idx);

    let vs = nn::VarStore::new(device);
    let net = net(&vs.root());

    let mut opt = nn::Adam::default().build(&vs, 1e-3)?;

    for epoch in 1..=100 {
        let loss = net
            .forward(&train_data)
            .cross_entropy_for_logits(&train_labels);

        opt.backward_step(&loss);

        let acc = net.forward(&test_data).accuracy_for_logits(&test_labels);

        println!(
            "epoch: {}, train loss: {:.5}, test acc: {:.3}",
            epoch,
            f64::try_from(&loss)?,
            f64::try_from(&acc)?
        );
    }

    Ok(())
}

fn net(vs: &nn::Path) -> impl Module {
    nn::seq()
        .add(nn::linear(vs / "layer1", 4, 8, Default::default()))
        .add_fn(|xs| xs.relu())
        .add(nn::linear(vs / "layer2", 8, 16, Default::default()))
        .add_fn(|xs| xs.relu())
        .add(nn::linear(vs / "layer3", 16, 3, Default::default()))
        .add_fn(|xs| xs.softmax(-1, Kind::Float))
}
