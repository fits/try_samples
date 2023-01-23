use image::ImageResult;
use std::env;

fn to_u32(v: String) -> Option<u32> {
    v.parse().ok()
}

fn main() -> ImageResult<()> {
    let mut args = env::args().skip(1);

    let file = args.next().unwrap();
    let w = args.next().and_then(to_u32).unwrap();
    let h = args.next().and_then(to_u32).unwrap();
    let dest = args.next().unwrap();

    let img = image::open(file)?;

    let img_n = img.thumbnail(w, h);

    img_n.save(dest)?;

    Ok(())
}
