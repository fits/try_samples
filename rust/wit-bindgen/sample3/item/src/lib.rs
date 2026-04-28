wit_bindgen::generate!("item");

struct Component;

export!(Component);

impl Guest for Component {
    #[allow(async_fn_in_trait)]
    fn find_price(item: ItemId) -> Option<Amount> {
        let v = (item.len() as i32) * 1000;
        Some(v)
    }
}
