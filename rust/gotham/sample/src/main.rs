
use gotham::state::State;

fn sample(state: State) -> (State, String) {
    (state, "sample".to_string())
}

fn main() {
    let addr = "127.0.0.1:8080";

    println!("start, {}", addr);

    gotham::start(addr, || Ok(sample));
}
