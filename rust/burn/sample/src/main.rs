use burn::tensor::Tensor;
use burn::backend::Wgpu;

type Backend = Wgpu;

fn main() {
    let device = Default::default();

    let tensor = Tensor::<Backend, 1>::from_floats([1.0, 2.0, 3.0, 4.0, 5.0], &device);

    println!("{}", tensor * 3);
}
