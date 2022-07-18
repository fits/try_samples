
use photon_rs::native::{open_image, save_image};
use photon_rs::transform::{resize, SamplingFilter};

use std::env; 
use std::time::Instant;

fn to_u32(v: String) -> Option<u32> {
    v.parse().ok()
}

fn main() -> Result<(), Box<dyn std::error::Error>> {
    let mut args = env::args().skip(1);

    let file = args.next().unwrap();
    let width = args.next().and_then(to_u32).unwrap();
    let height = args.next().and_then(to_u32).unwrap();

    let start = Instant::now();

    let mut img = open_image(file.as_str())?;

    img = resize(&img, width, height, SamplingFilter::Nearest);

    save_image(img, "output.jpg");

    let end = start.elapsed();

    println!("time: {} ms", end.as_millis());

    Ok(())
}
