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

    let i_file = args.next().unwrap();
    let w = args.next().and_then(to_u32).unwrap();
    let h = args.next().and_then(to_u32).unwrap();
    let o_file = args.next().unwrap();

    let t = Instant::now();

    let reader = BufReader::new(File::open(i_file)?);
    let mut dec = JpegDecoder::new(reader)?;

    dec.scale(w as u16, h as u16)?;

    let img = DynamicImage::from_decoder(dec)?;

    let p1 = t.elapsed().as_millis();
    println!("read & decode: {} ms", p1);

    let img_n = img.thumbnail(w, h);

    let p2 = t.elapsed().as_millis();
    println!("thumbnail: {} ms", p2 - p1);

    let writer = BufWriter::new(File::create(o_file)?);
    let mut enc = JpegEncoder::new(writer);

    enc.encode(img_n.as_bytes(), img_n.width(), img_n.height(), img_n.color())?;

    let p3 = t.elapsed().as_millis();
    println!("encode & write: {} ms", p3 - p2);
    println!("total: {} ms", p3);

    Ok(())
}
