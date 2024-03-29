use image::{ DynamicImage, ImageResult };
use image::codecs::jpeg::{ JpegDecoder, JpegEncoder };

use std::env;
use std::fs::File;
use std::io::{ BufReader, BufWriter };
use std::time::Instant;

fn to_u32(v: String) -> Option<u32> {
    v.parse().ok()
}

fn main() -> ImageResult<()> {
    let mut args = env::args().skip(1);

    let file = args.next().unwrap();
    let w = args.next().and_then(to_u32).unwrap();
    let h = args.next().and_then(to_u32).unwrap();
    let dest = args.next().unwrap();

    let t = Instant::now();

    let reader = BufReader::new(File::open(file)?);
    let mut dec = JpegDecoder::new(reader)?;

    dec.scale(w as u16, h as u16)?;

    let img_n = DynamicImage::from_decoder(dec)?.thumbnail(w, h);

    let writer = BufWriter::new(File::create(dest)?);
    let mut enc = JpegEncoder::new(writer);

    enc.encode(img_n.as_bytes(), img_n.width(), img_n.height(), img_n.color())?;

    println!("time: {} ms", t.elapsed().as_millis());

    Ok(())
}
