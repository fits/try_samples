use candle_core::{Device, Tensor};

type Result<T> = std::result::Result<T, Box<dyn std::error::Error>>;

fn main() -> Result<()> {
    let device = Device::new_metal(0)?;
    // Metal contiguous affine F64 not implemented
    let a = Tensor::from_slice(&[1.0f32, 2., 3., 4., 5., 6.], (3, 2), &device)?;

    println!("{a}");

    let b = (a * 3.)?;

    println!("{b}");

    let c = Tensor::from_slice(&[3.0f32, 2.], (2, 1), &device)?;

    println!("{c}");

    let d = b.matmul(&c)?;

    println!("{d}");

    Ok(())
}
