
use aws_sdk_s3::{Client, Endpoint};
use aws_sdk_s3::config::Builder;
use bytes::Buf;
use http::Uri;
use image::DynamicImage;
use image::codecs::jpeg::JpegDecoder;

use std::env;
use std::time::Instant;

type Error = Box<dyn std::error::Error>;

fn to_u32(v: String) -> Option<u32> {
    v.parse().ok()
}

#[tokio::main]
async fn main() -> Result<(), Error> {
    let bucket = env::var("BUCKET_NAME")?;

    let endpoint = env::var("S3_ENDPOINT")
        .ok()
        .and_then(|v| v.parse::<Uri>().ok())
        .map(Endpoint::immutable);

    let mut args = env::args().skip(1);

    let path = args.next().unwrap();
    let width = args.next().and_then(to_u32).unwrap();
    let height = args.next().and_then(to_u32).unwrap();
    let dest = args.next().unwrap();

    let conf = aws_config::load_from_env().await;

    let s3 = match endpoint {
        Some(ep) => Client::from_conf(
            Builder::from(&conf).endpoint_resolver(ep).build()
        ),
        _ => Client::new(&conf),
    };

    let start = Instant::now();

    let res = s3.get_object()
        .bucket(bucket)
        .key(path)
        .send()
        .await?;

    let buf = res.body.collect().await?;

    let t1 = start.elapsed().as_millis();

    let mut dec = JpegDecoder::new(buf.reader())?;

    dec.scale(width as u16, height as u16)?;

    let img = DynamicImage::from_decoder(dec)?
        .thumbnail(width, height);

    let t2 = start.elapsed().as_millis() - t1;

    img.save(dest)?;

    let t3 = start.elapsed().as_millis() - t2;

    println!("s3 download: {} ms", t1);
    println!("resize: {} ms", t2);
    println!("save: {} ms", t3);

    Ok(())
}
