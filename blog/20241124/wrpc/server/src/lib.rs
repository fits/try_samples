mod bindings {
    use crate::Handler;

    wit_bindgen::generate!({
        with: {
            "local:sample/handler": generate,
        }
    });

    export!(Handler);
}

struct Handler;

impl bindings::exports::local::sample::handler::Guest for Handler {
    fn add(a: i32, b: i32) -> i32 {
        a + b
    }
}
