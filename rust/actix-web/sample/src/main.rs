
use actix_web::{get, web, App, HttpServer, Responder};

async fn index(info: web::Path<(u32, String)>) -> impl Responder {
    format!("received: {:?}", info)
}

#[get("/a/b/{id}")]
async fn sample1(info: web::Path<(String,)>) -> impl Responder {
    format!("a: id={}", info.0)
}

#[get("/b/{id}/{value}/{command}")]
async fn sample2(info: web::Path<(String, u32, String)>) -> impl Responder {
    format!("b: id={}, value={}, command={}", info.0, info.1, info.2)
}

#[actix_rt::main]
async fn main() -> std::io::Result<()> {
    let address = "127.0.0.1:8080";

    HttpServer::new(|| {
        App::new()
            .service(web::resource("/{id}/{name}").to(index))
            .service(sample1)
            .service(sample2)
    }).bind(address)?.run().await
}
