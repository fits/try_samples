
use actix_web::{web, App, HttpServer, Responder};

fn index(info: web::Path<(u32, String)>) -> impl Responder {
    format!("received: {:?}", info)
}

fn main() -> std::io::Result<()> {
    let address = "127.0.0.1:8080";

    HttpServer::new(|| {
        App::new().service(web::resource("/{id}/{name}").to(index))
    }).bind(address)?.run()
}
