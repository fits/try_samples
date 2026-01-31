use burn::Tensor;

type Backend = burn::backend::Cuda;

fn main() {
    let device = Default::default();

    let tensor = Tensor::<Backend, 2>::from_floats(
        [[0.1, 0.2, 0.3], [0.4, 0.5, 0.6], [0.7, 0.8, 0.9]],
        &device,
    );

    let r = tensor * 0.17;

    println!("{}", r);
}
