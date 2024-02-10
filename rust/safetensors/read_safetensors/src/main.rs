use memmap2::MmapOptions;
use safetensors::SafeTensors;

use std::env;
use std::fs::File;

fn main() -> Result<(), Box<dyn std::error::Error>> {
    let file_name = env::args().skip(1).next().ok_or("safetensors file")?;

    let file = File::open(file_name)?;
    let buf = unsafe { MmapOptions::new().map(&file)? };

    let t = SafeTensors::deserialize(&buf)?;

    for n in t.names() {
        println!("{}", n);
    }

    Ok(())
}
