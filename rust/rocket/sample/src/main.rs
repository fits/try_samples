#![feature(decl_macro)]
#[macro_use] extern crate rocket;

#[get("/")]
fn index() -> String {
    "sample".to_string()
}

fn main() {
    rocket::ignite().mount("/", routes![index]).launch();
}
