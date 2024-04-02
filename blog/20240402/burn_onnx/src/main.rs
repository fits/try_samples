use burn::tensor::Tensor;
use burn_ndarray::{NdArray, NdArrayDevice};

mod model;
use model::sample::Model;

type Backend = NdArray<f32>;

fn main() {
    let device = NdArrayDevice::default();
    let model: Model<Backend> = Model::default();

    let input = Tensor::<Backend, 2>::from_floats([[1.0, 2.0]], &device);

    println!("{}", input);

    let output = model.forward(input);

    println!("{}", output);
}
