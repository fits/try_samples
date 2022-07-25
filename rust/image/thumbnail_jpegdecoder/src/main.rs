
use image::{ DynamicImage, ImageResult };
use image::io::Reader as ImageReader;
use image::codecs::jpeg::JpegDecoder;

use std::env;
use std::time::Instant;

fn to_u32(v: String) -> Option<u32> {
    v.parse().ok()
}

fn main() -> ImageResult<()> {
    let mut args = env::args().skip(1);

    let file = args.next().unwrap();
    let width = args.next().and_then(to_u32).unwrap();
    let height = args.next().and_then(to_u32).unwrap();
    let dest = args.next().unwrap();

    let start = Instant::now();

    let reader = ImageReader::open(file)?;
    let mut dec = JpegDecoder::new(reader.into_inner())?;

    dec.scale(width as u16, height as u16)?;

    let img_n = DynamicImage::from_decoder(dec)?.thumbnail(width, height);

    img_n.save(dest)?;

    println!("time: {} ms", start.elapsed().as_millis());

    Ok(())
}
