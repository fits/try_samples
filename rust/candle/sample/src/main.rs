use candle_core::{Device, Tensor};

fn main() -> Result<(), Box<dyn std::error::Error>> {
    let device = Device::Cpu;

    let a = Tensor::from_slice(&[1., 2., 3., 4., 5., 6.], (3, 2), &device)?;

    println!("{a}");

    let b = (a * 3.)?;

    println!("{b}");

    let c = Tensor::from_slice(&[3., 2.], (2, 1), &device)?;

    println!("{c}");

    let d = b.matmul(&c)?;

    println!("{d}");

    Ok(())
}
