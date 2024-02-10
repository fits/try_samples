use safetensors::tensor::TensorView;
use safetensors::{serialize, SafeTensors};

fn main() -> Result<(), Box<dyn std::error::Error>> {
    let d: Vec<u8> = vec![3.0f32, 7.0, 11.0]
        .into_iter()
        .flat_map(|d| d.to_le_bytes())
        .collect();

    let w = TensorView::new(safetensors::Dtype::F32, vec![1, 3], &d)?;

    let metadata = vec![("weight", w)];

    let out = serialize(metadata, &None)?;

    println!("serialized = {:?}", out);

    let s = SafeTensors::deserialize(&out)?;

    println!("names = {:?}", s.names());

    let v = s.tensor("weight")?;

    println!("tensor = {:?}", v);

    Ok(())
}
