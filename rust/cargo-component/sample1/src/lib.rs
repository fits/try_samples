cargo_component_bindings::generate!();

use bindings::{CartId, EmptyCart, Guest};

struct Component;

impl Guest for Component {
    fn create(id: CartId) -> EmptyCart {
        EmptyCart { id }
    }
}
