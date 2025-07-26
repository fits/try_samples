use exports::example::testcomp::types::Guest;
use example::pgitem::types::{set_price, get_price};

wit_bindgen::generate!({
    world: "test",
    generate_all,
});

struct Component;

impl Guest for Component {
    fn call() -> String {
        let r1 = set_price("item-12".into(), 4560);
        let r2 = get_price("item-12".into());

        format!("1st={}, 2nd={:?}", r1, r2)
    }
}

export!(Component);
