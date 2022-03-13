use hyper::service::{make_service_fn, service_fn};
use hyper::{Body, Error, Method, Request, Response, Result, Server, StatusCode, header};
use hyper::body::Buf;

use serde_json::Value as JsValue;

use std::sync::{Arc, RwLock};

#[derive(Default, Clone, Debug)]
struct Store(Arc<RwLock<Vec<JsValue>>>);

fn json_response(s: String) -> Result<Response<Body>> {
    let res = Response::builder()
        .status(StatusCode::OK)
        .header(header::CONTENT_TYPE, "application/json")
        .body(Body::from(s))
        .unwrap();

    Ok(res)
}

fn response(code: StatusCode, s: String) -> Result<Response<Body>> {
    let res = Response::builder()
        .status(code)
        .body(Body::from(s))
        .unwrap();

    Ok(res)
}

fn not_found() -> Result<Response<Body>> {
    response(StatusCode::NOT_FOUND, String::default())
}

fn server_error(s: String) -> Result<Response<Body>> {
    response(StatusCode::INTERNAL_SERVER_ERROR, s)
}

fn bad_request(s: String) -> Result<Response<Body>> {
    response(StatusCode::BAD_REQUEST, s)
}

async fn post_items(req: Request<Body>, store: Store) -> Result<Response<Body>> {
    let body = hyper::body::aggregate(req).await?;

    match serde_json::from_reader(body.reader()) {
        Ok(v) => match store.0.write() {
            Ok(mut store) => {
                store.push(v);
                let size = store.len();
            
                json_response(size.to_string())
            },
            Err(e) => server_error(e.to_string())
        },
        Err(e) => bad_request(e.to_string())
    }
}

fn get_items(_req: Request<Body>, store: Store) -> Result<Response<Body>> {
    match store.0.read() {
        Ok(store) => {
            let json = serde_json::to_string(&store.clone())
                .unwrap_or("".to_string());

            json_response(json)
        },
        Err(e) => server_error(e.to_string())
    }
}

async fn handle(req: Request<Body>, store: Store) -> Result<Response<Body>> {
    match (req.method(), req.uri().path()) {
        (&Method::POST, "/items") => post_items(req, store).await,
        (&Method::GET, "/items") => get_items(req, store),
        _ => not_found(),
    }
}

#[tokio::main]
async fn main() -> Result<()> {
    let store = Store::default();
    let addr = ([127, 0, 0, 1], 8080).into();

    let server = Server::bind(&addr)
        .serve(make_service_fn(move |_| {
            let store = store.clone();

            async move {
                Ok::<_, Error>(service_fn(move |req| handle(req, store.to_owned())))
            }
        }));

    println!("server started: {}", addr);

    server.await?;

    Ok(())
}
