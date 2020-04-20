
use actix_cors::Cors;
use actix_web::{http, get, put, web, HttpServer, App, HttpResponse, Responder};
use serde::Deserialize;

#[derive(Debug, Deserialize)]
struct Data {
    value: i32,
}

#[get("/samples")]
async fn get_sample() -> impl Responder {
    "sample"
}

#[put("/samples/{id}")]
async fn put_sample(info: web::Path<(String,)>, 
    params: web::Json<Data>) -> impl Responder {
    
    println!("{:?}, {:?}", info.0, params.0);
    HttpResponse::NoContent()
}

#[actix_rt::main]
async fn main() -> std::io::Result<()> {
    let addr = "127.0.0.1:8080";

    HttpServer::new( move || 
        App::new()
            .wrap(
                Cors::new()
                    .allowed_origin("http://localhost:8081")
                    .allowed_methods(vec!["GET", "PUT"])
                    .allowed_header(http::header::CONTENT_TYPE)
                    .allowed_header("sample-header")
                    .finish()
            )
            .service(get_sample)
            .service(put_sample)
    ).bind(addr)?.run().await
}
