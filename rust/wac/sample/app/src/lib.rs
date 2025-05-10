use exports::wasi::cli::run::Guest;
use sample::cart::types::{add_item, create};

wit_bindgen::generate!({
    world: "app",
    with: {
        "sample:cart/types": generate,
        "sample:item/types": generate,
        "wasi:cli/run@0.2.3": generate,
    }
});

struct Component;

impl Guest for Component {
    fn run() -> Result<(), ()> {
        let s1 = create("cart1");
        println!("create : {:?}", s1);

        let s2 = add_item(&s1, "item-1", 1);
        println!("add_item 1st : {:?}", s2);

        let s3 = add_item(&s2.unwrap(), "item-2", 2);
        println!("add_item 2nd : {:?}", s3);

        let s4 = add_item(&s3.unwrap(), "item-1", 3);
        println!("add_item 3rd : {:?}", s4);

        Ok(())
    }
}

export!(Component);
