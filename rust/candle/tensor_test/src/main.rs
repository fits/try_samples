use candle_core::{Tensor, Device, IndexOp};

type Result<T> = std::result::Result<T, Box<dyn std::error::Error>>;

fn main() -> Result<()> {
    let device = Device::Cpu;

    let t1 = Tensor::from_slice(&[1, 2, 3, 4, 5, 6], (2, 3), &device)?;
    println!("t1 shape={:?}", t1.shape());

    let t2 = Tensor::from_slice(&[7, 8, 9, 10, 11, 12], (2, 3), &device)?;

    let t3 = Tensor::stack(&[&t1, &t2], 0)?;

    println!("t3 shape={:?}", t3.shape());

    let t3a = t3.get(0)?;
    let t3a_v = t3a.flatten_all()?.to_vec1::<i32>()?;

    println!("t3a={:?}, value={:?}", t3a, t3a_v);
    
    let t3b = t3.i((1, ..))?;
    let t3b_v = t3b.flatten_all()?.to_vec1::<i32>()?;

    println!("t3b={:?}, value={:?}", t3b, t3b_v);

    let t4 = Tensor::cat(&[&t1, &t2], 0);

    println!("t4={:?}", t4);

    Ok(())
}
