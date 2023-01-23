use image::ImageResult;

use std::env;
use std::time::Instant;

fn to_u32(v: String) -> Option<u32> {
    v.parse().ok()
}

fn main() -> ImageResult<()> {
    let mut args = env::args().skip(1);

    let i_file = args.next().unwrap();
    let w = args.next().and_then(to_u32).unwrap();
    let h = args.next().and_then(to_u32).unwrap();
    let o_file = args.next().unwrap();

    let t = Instant::now();

    let img = image::open(i_file)?;

    let p1 = t.elapsed().as_millis();
    println!("read & decode: {} ms", p1);

    let img_n = img.thumbnail(w, h);

    let p2 = t.elapsed().as_millis();
    println!("thumbnail: {} ms", p2 - p1);

    img_n.save(o_file)?;

    let p3 = t.elapsed().as_millis();
    println!("encode & write: {} ms", p3 - p2);
    println!("total: {} ms", p3);

    Ok(())
}
