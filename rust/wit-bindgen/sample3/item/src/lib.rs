
wit_bindgen::generate!("item");

struct Component;

export_item!(Component);

impl Item for Component {
    fn find_price(item:ItemId,) -> Option<Amount> {
        let v = (item.len() as i32) * 1000;
        Some(v)
    }
}
