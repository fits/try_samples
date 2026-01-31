use candle_core::{Device, Tensor};

type Result<T> = std::result::Result<T, Box<dyn std::error::Error>>;

fn main() -> Result<()> {
    let device = Device::new_cuda(0)?;

    let a = Tensor::from_slice(&[1., 2., 3., 7., 8., 9.], &[3, 2], &device)?;

    println!("{a}");

    let b = (a * 5.)?;

    println!("{b}");

    Ok(())
}
