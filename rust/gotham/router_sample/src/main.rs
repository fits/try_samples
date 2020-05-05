
use gotham::state::State;
use gotham::router::Router;
use gotham::router::builder::*;

fn sample(state: State) -> (State, String) {
    (state, "sample".to_string())
}

fn router() -> Router {
    build_simple_router(|route| {
        route.get("/sample").to(sample);
    })
}

fn main() {
    let addr = "127.0.0.1:8080";

    println!("start, {}", addr);

    gotham::start(addr, router());
}
