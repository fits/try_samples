#![feature(decl_macro)]
#[macro_use] extern crate rocket;

use rocket::http::RawStr;

#[get("/samples?<q>&<s>")]
fn samples(q: &RawStr, s: Option<String>) -> String {
    format!("q={}, s={:?}", q, s)
}

fn main() {
    rocket::ignite()
        .mount("/", routes![samples])
        .launch();
}
