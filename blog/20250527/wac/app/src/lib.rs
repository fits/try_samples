use exports::wasi::cli::run::Guest;
use testapp::cart::types::{add_item, create};

wit_bindgen::generate!({
    world: "app",
    with: {
        "testapp:cart/types": generate,
        "testapp:item/types": generate,
        "wasi:cli/run@0.2.3": generate,
    }
});

struct Component;

impl Guest for Component {
    fn run() -> Result<(), ()> {
        let s1 = create("cart-1");
        println!("1. {:?}", s1);

        let s2 = add_item(&s1, "item1", 1);
        println!("2. {:?}", s2);

        let s3 = add_item(&s2.unwrap(), "item-22", 2);
        println!("3. {:?}", s3);

        let s4 = add_item(&s3.unwrap(), "item1", 3);
        println!("4. {:?}", s4);

        Ok(())
    }
}

export!(Component);
