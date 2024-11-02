mod bindings {
    wit_bindgen::generate!({
        with: {
            "local:sample/handler": generate,
        }
    });
}

fn main() {
    let a = 2;
    let b = 3;

    let r = bindings::local::sample::handler::add(a, b);

    println!("{a} + {b} = {r}");
}
