use gotham::state::State;
use gotham::prelude::FromState;
use gotham::hyper::Uri;

use std::env;

fn sample(state: State) -> (State, &'static str) {
    let uri = Uri::borrow_from(&state);
    println!("path={}", uri);

    (state, "ok")
}

fn main() {
    let port = env::var("APP_PORT").unwrap_or("3000".to_owned());
    let addr = format!("0.0.0.0:{}", port);

    println!("listening: {}", addr);

    gotham::start(addr, || Ok(sample)).unwrap();
}
