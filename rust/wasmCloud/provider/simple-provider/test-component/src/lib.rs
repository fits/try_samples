wit_bindgen::generate!({ generate_all });

use exports::dev::example::types::Guest;
use simple::greeting::types::hello;
use wasi::logging::logging::{Level, log};

struct TestComponent;

impl Guest for TestComponent {
    fn run() -> String {
        let r1 = hello("a1");
        log(Level::Info, "context", format!("a1 => {:?}", r1).as_str());

        let r2 = hello("b2");
        log(Level::Info, "context", format!("b2 => {:?}", r2).as_str());

        format!("{:?}, {:?}", r1, r2)
    }
}

export!(TestComponent);
