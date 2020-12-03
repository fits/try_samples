
use actix_web::{post, web, App, HttpRequest, HttpServer};
use cloudevents::Data;
use cloudevents_sdk_actix_web::{HttpRequestExt};

#[post("/")]
async fn post_event(req: HttpRequest, payload: web::Payload) -> Result<String, actix_web::Error> {
    let event = req.to_event(payload).await?;

    println!("* received: {:?}", event);

    if let Some(d) = event.data() {
        match d {
            Data::Binary(b) => 
                println!("** data binary: {:?}", String::from_utf8(b.clone())),
            Data::String(s) => println!("** data string: {}", s),
            Data::Json(s) => println!("** data json: {}", s), 
        };
    }

    Ok("done".to_string())
}

#[actix_web::main]
async fn main() -> std::io::Result<()> {
    let address = "localhost:3000";

    HttpServer::new(|| {
        App::new()
            .service(post_event)
    })
    .bind(address)?
    .run()
    .await
}