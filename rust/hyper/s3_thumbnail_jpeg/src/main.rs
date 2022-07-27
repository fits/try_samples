
use hyper::service::{make_service_fn, service_fn};
use hyper::{Body, Request, Response, Server, StatusCode, header};

use aws_sdk_s3::{Client, Endpoint};
use aws_sdk_s3::config::Builder;
use aws_sdk_s3::types::AggregatedBytes;
use bytes::Buf;
use http::Uri;
use image::DynamicImage;
use image::codecs::jpeg::JpegDecoder;

use std::env;
use std::cmp;
use std::io::Cursor;
use std::collections::HashMap;
use std::net::SocketAddr;
use std::time::Instant;

type GenericError = Box<dyn std::error::Error + Send + Sync>;
type Result<T> = std::result::Result<T, GenericError>;
type ResponseResult = Result<Response<Body>>;

fn not_found() -> ResponseResult {
    let res = Response::builder()
        .status(StatusCode::NOT_FOUND)
        .body(Body::empty())
        .unwrap();

    Ok(res)
}

fn jpeg_response(body: Body) -> ResponseResult {
    let res = Response::builder()
        .header(header::CONTENT_TYPE, "image/jpeg")
        .body(body)
        .unwrap();

    Ok(res)
}

fn to_num<T>(param: Option<&String>, default_value: T, max_value: T) -> T
where
    T: Ord + std::str::FromStr,
{
    cmp::min(
        param.and_then(|v| v.parse().ok()).unwrap_or(default_value), 
        max_value
    )
}

async fn thumbnail(body: AggregatedBytes, params: HashMap<String, String>) -> ResponseResult {
    let width = to_num(params.get("w"), 0, 800);
    let height = to_num(params.get("h"), 0, 800);
    let quality = to_num(params.get("q"), 80, 100);

    if width == 0 || height == 0 {
        return jpeg_response(body.into_bytes().into());
    }

    let t = Instant::now();

    let mut dec = JpegDecoder::new(body.reader())?;
    dec.scale(width, height)?;

    let img = DynamicImage::from_decoder(dec)?
        .thumbnail(width as u32, height as u32);

    let t1 = t.elapsed().as_millis();

    let mut buf = Cursor::new(Vec::new());
    img.write_to(&mut buf, image::ImageOutputFormat::Jpeg(quality))?;

    let t2 = t.elapsed().as_millis() - t1;

    println!("image resize: {} ms", t1);
    println!("image encode: {} ms", t2);

    jpeg_response(buf.into_inner().into())
}

async fn handle_jpeg(s3client: Client, bucket: String, 
    path: String, params: HashMap<String, String>) -> ResponseResult {

    let t = Instant::now();

    let obj = s3client.get_object()
        .bucket(bucket)
        .key(path)
        .send()
        .await;

    match obj {
        Ok(r) => {
            let buf = r.body.collect().await?;

            println!("s3 download: {} ms", t.elapsed().as_millis());

            thumbnail(buf, params).await
        },
        Err(_) => not_found(),
    }
}

fn query_to_map(uri: &Uri) -> HashMap<String, String> {
    let q = uri.query().unwrap_or("");

    form_urlencoded::parse(q.as_bytes())
        .into_owned()
        .collect()
}

async fn handle(req: Request<Body>, s3client: Client, bucket: String) -> ResponseResult {
    let path = req.uri().path();

    if path.to_lowercase().ends_with(".jpg") {
        let params = query_to_map(req.uri());

        let mut path = path.to_string();
        path.remove(0);

        handle_jpeg(s3client, bucket, path, params).await
    }
    else {
        not_found()
    }
}

#[tokio::main]
async fn main() -> Result<()> {
    let port = env::args()
        .skip(1)
        .next()
        .and_then(|s| s.parse().ok())
        .unwrap_or(8080);

    let addr = SocketAddr::from(([127, 0, 0, 1], port));

    let bucket = env::var("BUCKET_NAME").unwrap();

    let endpoint = env::var("S3_ENDPOINT")
        .ok()
        .and_then(|v| v.parse::<Uri>().ok())
        .map(Endpoint::immutable);

    let conf = aws_config::load_from_env().await;

    let s3client = match endpoint {
        Some(ep) => Client::from_conf(
            Builder::from(&conf).endpoint_resolver(ep).build()
        ),
        _ => Client::new(&conf),
    };

    let make_service = make_service_fn(move |_| {
        let s3client = s3client.clone();
        let bucket = bucket.clone();

        async move { 
            Ok::<_, GenericError>(service_fn(move |req| 
                handle(req, s3client.to_owned(), bucket.to_owned()))
            )
        }
    });

    let server = Server::bind(&addr)
        .serve(make_service);

    println!("server start: {}", addr);

    server.await?;

    Ok(())
}
