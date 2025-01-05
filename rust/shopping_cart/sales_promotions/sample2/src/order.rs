use std::collections::HashMap;

use super::*;

pub type OrderLineId = String;
pub type ItemId = String;

pub type AttrKey = String;
pub type AttrValue = String;
pub type Attributes = HashMap<AttrKey, AttrValue>;

#[derive(Debug, Clone)]
pub struct Order {
    pub order_at: Date,
    pub lines: Vec<OrderLine>,
}

#[derive(Debug, Clone, PartialEq)]
pub struct OrderLine {
    pub line_id: OrderLineId,
    pub item_id: ItemId,
    pub price: Amount,
    pub attrs: Attributes,
}

impl Order {
    pub fn subtotal(&self) -> Amount {
        self.subtotal_by(&ItemRestriction::Anything)
    }

    pub fn subtotal_by(&self, restrict: &ItemRestriction) -> Amount {
        let mut total = Amount::zero();

        for line in &self.lines {
            if restrict.check(line) {
                total += line.price.clone();
            }
        }

        total
    }
}

pub fn subtotal_lines(lines: &Vec<&OrderLine>) -> Amount {
    let mut total = Amount::zero();

    for line in lines {
        total += line.price.clone();
    }

    total
}
