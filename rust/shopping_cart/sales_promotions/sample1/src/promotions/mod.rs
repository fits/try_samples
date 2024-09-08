#![allow(dead_code)]

pub mod discount;
use discount::{DiscountReward, DiscountRule};

use std::cmp::max;
use std::collections::HashMap;

use num::BigRational;
use num_traits::Zero;

pub type OrderId = String;
pub type OrderLineId = String;
pub type ItemId = String;
pub type Amount = BigRational;
pub type Quantity = usize;

pub type AttrKey = String;
pub type AttrValue = String;
pub type Attrs = HashMap<AttrKey, AttrValue>;

#[derive(Debug, Clone)]
pub struct PromotionRule {
    condition: OrderCondition,
    action: PromotionAction,
}

#[derive(Debug, Clone)]
pub enum OrderCondition {
    Anything,
    Attribute(AttrKey, Vec<AttrValue>),
    SubtotalRange(Amount, Option<Amount>),
    IncludeItem(ItemCondition),
    Not(Box<Self>),
    And(Box<Self>, Box<Self>),
    Or(Box<Self>, Box<Self>),
}

#[derive(Debug, Clone)]
pub enum GroupCondition {
    Items(ItemCondition),
    QtyLimit(Box<Self>, Quantity, Option<Quantity>),
    PickOne(Vec<ItemCondition>),
}

#[derive(Debug, Clone)]
pub enum ItemCondition {
    Anything,
    Attribute(AttrKey, Vec<AttrValue>),
    Item(Vec<ItemId>),
    PriceRange(Amount, Option<Amount>),
    Not(Box<Self>),
    And(Box<Self>, Box<Self>),
    Or(Box<Self>, Box<Self>),
}

#[derive(Debug, Clone)]
pub enum PromotionAction {
    All(RewardAction),
    Any(Quantity, RewardAction),
}

#[derive(Debug, Clone)]
pub enum RewardAction {
    Discount(DiscountRule),
}

#[derive(Debug, Clone)]
pub struct Order {
    pub id: OrderId,
    pub attrs: Attrs,
    pub lines: Vec<OrderLine>,
}

#[derive(Debug, Clone, PartialEq)]
pub struct OrderLine {
    pub line_id: OrderLineId,
    pub attrs: Attrs,
    pub item_id: ItemId,
    pub price: Amount,
}

#[derive(Debug, Clone)]
pub enum RewardTarget<T> {
    None,
    Group(Vec<T>),
    Single(T),
}

#[derive(Debug, Clone)]
pub enum Reward<T> {
    Discount(DiscountReward<T>),
}

impl GroupCondition {
    pub fn qty_limit(&self, from: Quantity, to: Option<Quantity>) -> Self {
        Self::QtyLimit(Box::new(self.to_owned()), from, to)
    }

    fn select<'a>(&self, items: &'a Vec<OrderLine>) -> Option<Vec<&'a OrderLine>> {
        match self {
            Self::Items(c) => {
                let rs = items
                    .iter()
                    .filter(move |&x| c.predict(x))
                    .collect::<Vec<_>>();

                if rs.len() > 0 {
                    Some(rs)
                } else {
                    None
                }
            }
            Self::QtyLimit(c, from, to) => c
                .select(items)
                .map(|x| {
                    if let Some(to) = to {
                        x.into_iter().take(*to).collect::<Vec<_>>()
                    } else {
                        x
                    }
                })
                .and_then(|x| {
                    if x.len() >= max(1, *from) {
                        Some(x)
                    } else {
                        None
                    }
                }),
            Self::PickOne(cs) => {
                if cs.len() > 0 {
                    let mut rs: Vec<&'a OrderLine> = vec![];

                    for c in cs {
                        for i in items {
                            if rs.contains(&i) {
                                continue;
                            }

                            if c.predict(i) {
                                rs.push(i);
                                break;
                            }
                        }
                    }

                    if rs.len() == cs.len() {
                        Some(rs)
                    } else {
                        None
                    }
                } else {
                    None
                }
            }
        }
    }
}

impl ItemCondition {
    pub fn not(&self) -> Self {
        Self::Not(Box::new(self.to_owned()))
    }

    pub fn and(&self, c: Self) -> Self {
        Self::And(Box::new(self.to_owned()), Box::new(c))
    }

    pub fn or(&self, c: Self) -> Self {
        Self::Or(Box::new(self.to_owned()), Box::new(c))
    }

    fn predict(&self, target: &OrderLine) -> bool {
        match self {
            Self::Anything => true,
            Self::Item(items) => items.contains(&target.item_id),
            Self::Attribute(k, v) => target.attrs.get(k).map(|x| v.contains(x)).unwrap_or(false),
            Self::PriceRange(from, to) => {
                target.price >= *from && to.clone().map(|x| target.price <= x).unwrap_or(true)
            }
            Self::Not(c) => !c.predict(target),
            Self::And(c1, c2) => c1.predict(target) && c2.predict(target),
            Self::Or(c1, c2) => c1.predict(target) || c2.predict(target),
        }
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    fn from_u(v: usize) -> Amount {
        Amount::from_integer(v.into())
    }

    fn item_order(line_id: OrderLineId, item_id: ItemId) -> OrderLine {
        OrderLine {
            line_id,
            attrs: HashMap::new(),
            item_id,
            price: from_u(100),
        }
    }

    fn attr_order(line_id: OrderLineId, key: AttrKey, value: AttrValue) -> OrderLine {
        OrderLine {
            line_id,
            attrs: HashMap::from([(key, value)]),
            item_id: "item-1".into(),
            price: from_u(100),
        }
    }

    fn item_attr_order(
        line_id: OrderLineId,
        item_id: ItemId,
        key: AttrKey,
        value: AttrValue,
    ) -> OrderLine {
        OrderLine {
            line_id,
            attrs: HashMap::from([(key, value)]),
            item_id,
            price: from_u(100),
        }
    }

    fn price_order(line_id: OrderLineId, price: Amount) -> OrderLine {
        OrderLine {
            line_id,
            attrs: HashMap::new(),
            item_id: "item-1".into(),
            price,
        }
    }

    mod condition {
        use super::GroupCondition::*;
        use super::ItemCondition::*;
        use super::*;

        #[test]
        fn anything() {
            let c = Anything;

            assert!(c.predict(&item_order("o1".into(), "item-1".into())));
            assert!(c.predict(&item_order("o2".into(), "item-2".into())));
        }

        #[test]
        fn item_include() {
            let c = Item(vec!["item-1".into(), "item-2".into()]);

            assert!(c.predict(&item_order("o1".into(), "item-1".into())));
            assert!(c.predict(&item_order("o2".into(), "item-2".into())));
        }

        #[test]
        fn item_exclude() {
            let c = Item(vec!["item-1".into(), "item-2".into()]);

            assert_eq!(false, c.predict(&item_order("o1".into(), "item-3".into())));
        }

        #[test]
        fn attr_match() {
            let c: ItemCondition = Attribute("category".into(), vec!["c1".into(), "c2".into()]);

            assert!(c.predict(&attr_order("o1".into(), "category".into(), "c1".into())));
            assert!(c.predict(&attr_order("o2".into(), "category".into(), "c2".into())));
        }

        #[test]
        fn attr_unmatch() {
            let c: ItemCondition = Attribute("category".into(), vec!["c1".into(), "c2".into()]);

            assert_eq!(
                false,
                c.predict(&attr_order("o1".into(), "category".into(), "c3".into()))
            );
        }

        #[test]
        fn no_attr() {
            let c: ItemCondition = Attribute("category".into(), vec!["c1".into(), "c2".into()]);

            assert_eq!(
                false,
                c.predict(&attr_order("o1".into(), "keyword".into(), "k1".into()))
            );
        }

        #[test]
        fn price_lower_match() {
            let c = PriceRange(from_u(100), None);

            assert!(c.predict(&price_order("o1".into(), from_u(100))));
            assert!(c.predict(&price_order("o1".into(), from_u(200))));
        }

        #[test]
        fn price_lower_unmatch() {
            let c = PriceRange(from_u(100), None);

            assert_eq!(false, c.predict(&price_order("o1".into(), from_u(99))));
            assert_eq!(false, c.predict(&price_order("o2".into(), from_u(0))));
        }

        #[test]
        fn price_upper_match() {
            let c = PriceRange(from_u(100), Some(from_u(200)));

            assert!(c.predict(&price_order("o1".into(), from_u(100))));
            assert!(c.predict(&price_order("o2".into(), from_u(150))));
            assert!(c.predict(&price_order("o3".into(), from_u(200))));
        }

        #[test]
        fn price_upper_unmatch() {
            let c = PriceRange(from_u(100), Some(from_u(200)));

            assert_eq!(false, c.predict(&price_order("o1".into(), from_u(201))));
            assert_eq!(false, c.predict(&price_order("o2".into(), from_u(300))));
        }

        #[test]
        fn not_item() {
            let c = Item(vec!["item-1".into()]).not();

            assert_eq!(false, c.predict(&item_order("o1".into(), "item-1".into())));
            assert!(c.predict(&item_order("o2".into(), "item-2".into())));
        }

        #[test]
        fn and_match() {
            let c = Item(vec!["item-1".into(), "item-2".into()])
                .and(Attribute("category".into(), vec!["c1".into(), "c2".into()]));

            assert!(c.predict(&item_attr_order(
                "o1".into(),
                "item-1".into(),
                "category".into(),
                "c2".into()
            )));
            assert!(c.predict(&item_attr_order(
                "o2".into(),
                "item-2".into(),
                "category".into(),
                "c1".into()
            )));
        }

        #[test]
        fn and_unmatch() {
            let c = Item(vec!["item-1".into(), "item-2".into()])
                .and(Attribute("category".into(), vec!["c1".into(), "c2".into()]));

            assert_eq!(
                false,
                c.predict(&item_attr_order(
                    "o1".into(),
                    "item-3".into(),
                    "category".into(),
                    "c2".into()
                ))
            );
            assert_eq!(
                false,
                c.predict(&item_attr_order(
                    "o2".into(),
                    "item-2".into(),
                    "category".into(),
                    "c3".into()
                ))
            );
            assert_eq!(
                false,
                c.predict(&item_attr_order(
                    "o3".into(),
                    "item-1".into(),
                    "keyword".into(),
                    "k1".into()
                ))
            );
        }

        #[test]
        fn or_match() {
            let c = Item(vec!["item-1".into(), "item-2".into()])
                .or(Attribute("category".into(), vec!["c1".into(), "c2".into()]));

            assert!(c.predict(&item_attr_order(
                "o1".into(),
                "item-1".into(),
                "category".into(),
                "c2".into()
            )));
            assert!(c.predict(&item_attr_order(
                "o2".into(),
                "item-3".into(),
                "category".into(),
                "c1".into()
            )));

            assert!(c.predict(&item_attr_order(
                "o2".into(),
                "item-1".into(),
                "category".into(),
                "c3".into()
            )));
        }

        #[test]
        fn or_unmatch() {
            let c = Item(vec!["item-1".into(), "item-2".into()])
                .or(Attribute("category".into(), vec!["c1".into(), "c2".into()]));

            assert_eq!(
                false,
                c.predict(&item_attr_order(
                    "o1".into(),
                    "item-3".into(),
                    "category".into(),
                    "c3".into()
                ))
            );
        }

        #[test]
        fn select_items_match() {
            let c = Items(Item(vec!["item-1".into(), "item-2".into()]));

            let it = vec![
                item_order("o1".into(), "item-1".into()),
                item_order("o2".into(), "item-3".into()),
                item_order("o3".into(), "item-1".into()),
                item_order("o4".into(), "item-2".into()),
                item_order("o5".into(), "item-4".into()),
            ];

            let r = c.select(&it).unwrap();

            assert_eq!(3, r.len());
            assert_eq!("o1", r.get(0).unwrap().line_id);
            assert_eq!("o3", r.get(1).unwrap().line_id);
            assert_eq!("o4", r.get(2).unwrap().line_id);
        }

        #[test]
        fn select_items_unmatch() {
            let c = Items(Item(vec!["item-10".into(), "item-11".into()]));

            let it = vec![
                item_order("o1".into(), "item-1".into()),
                item_order("o2".into(), "item-3".into()),
                item_order("o3".into(), "item-1".into()),
                item_order("o4".into(), "item-2".into()),
                item_order("o5".into(), "item-4".into()),
            ];

            assert!(c.select(&it).is_none());
        }

        #[test]
        fn select_qty_lower_match() {
            let c = Items(Item(vec!["item-1".into(), "item-2".into()])).qty_limit(3, None);

            let it = vec![
                item_order("o1".into(), "item-1".into()),
                item_order("o2".into(), "item-3".into()),
                item_order("o3".into(), "item-1".into()),
                item_order("o4".into(), "item-2".into()),
                item_order("o5".into(), "item-4".into()),
            ];

            assert!(c.select(&it).is_some());
        }

        #[test]
        fn select_qty_lower_unmatch() {
            let c = Items(Item(vec!["item-1".into(), "item-2".into()])).qty_limit(4, None);

            let it = vec![
                item_order("o1".into(), "item-1".into()),
                item_order("o2".into(), "item-3".into()),
                item_order("o3".into(), "item-1".into()),
                item_order("o4".into(), "item-2".into()),
                item_order("o5".into(), "item-4".into()),
            ];

            assert!(c.select(&it).is_none());
        }

        #[test]
        fn select_qty_upper_over() {
            let c = Items(Item(vec!["item-1".into(), "item-2".into()])).qty_limit(1, Some(2));

            let it = vec![
                item_order("o1".into(), "item-1".into()),
                item_order("o2".into(), "item-3".into()),
                item_order("o3".into(), "item-1".into()),
                item_order("o4".into(), "item-2".into()),
                item_order("o5".into(), "item-4".into()),
            ];

            let r = c.select(&it).unwrap();

            assert_eq!(2, r.len());
        }

        #[test]
        fn select_qty_upper_under() {
            let c = Items(Item(vec!["item-1".into(), "item-2".into()])).qty_limit(2, Some(5));

            let it = vec![
                item_order("o1".into(), "item-1".into()),
                item_order("o2".into(), "item-3".into()),
                item_order("o3".into(), "item-1".into()),
                item_order("o4".into(), "item-2".into()),
                item_order("o5".into(), "item-4".into()),
            ];

            let r = c.select(&it).unwrap();

            assert_eq!(3, r.len());
        }

        #[test]
        fn select_qty_upper_zero() {
            let c = Items(Item(vec!["item-1".into(), "item-2".into()])).qty_limit(0, Some(0));

            let it = vec![
                item_order("o1".into(), "item-1".into()),
                item_order("o2".into(), "item-3".into()),
                item_order("o3".into(), "item-1".into()),
                item_order("o4".into(), "item-2".into()),
                item_order("o5".into(), "item-4".into()),
            ];

            assert!(c.select(&it).is_none());
        }

        #[test]
        fn select_qty_upper_under_lower() {
            let c = Items(Item(vec!["item-1".into(), "item-2".into()])).qty_limit(2, Some(1));

            let it = vec![
                item_order("o1".into(), "item-1".into()),
                item_order("o2".into(), "item-3".into()),
                item_order("o3".into(), "item-1".into()),
                item_order("o4".into(), "item-2".into()),
                item_order("o5".into(), "item-4".into()),
            ];

            assert!(c.select(&it).is_none());
        }

        #[test]
        fn select_pickone() {
            let c = PickOne(vec![
                Item(vec!["item-1".into()]),
                Item(vec!["item-2".into()]),
            ]);

            let it = vec![
                item_order("o1".into(), "item-1".into()),
                item_order("o2".into(), "item-3".into()),
                item_order("o3".into(), "item-1".into()),
                item_order("o4".into(), "item-2".into()),
                item_order("o5".into(), "item-4".into()),
            ];

            let r = c.select(&it).unwrap();

            assert_eq!(2, r.len());
            assert_eq!("o1", r.get(0).unwrap().line_id);
            assert_eq!("o4", r.get(1).unwrap().line_id);
        }

        #[test]
        fn select_pickone_same() {
            let c = PickOne(vec![
                Item(vec!["item-1".into()]),
                Item(vec!["item-2".into()]),
                Item(vec!["item-1".into()]),
            ]);

            let it = vec![
                item_order("o1".into(), "item-1".into()),
                item_order("o2".into(), "item-3".into()),
                item_order("o3".into(), "item-1".into()),
                item_order("o4".into(), "item-2".into()),
                item_order("o5".into(), "item-4".into()),
            ];

            let r = c.select(&it).unwrap();

            assert_eq!(3, r.len());
            assert_eq!("o1", r.get(0).unwrap().line_id);
            assert_eq!("o4", r.get(1).unwrap().line_id);
            assert_eq!("o3", r.get(2).unwrap().line_id);
        }

        #[test]
        fn select_pickone_empty() {
            let c = PickOne(vec![]);

            let it = vec![
                item_order("o1".into(), "item-1".into()),
                item_order("o2".into(), "item-3".into()),
                item_order("o3".into(), "item-1".into()),
                item_order("o4".into(), "item-2".into()),
                item_order("o5".into(), "item-4".into()),
            ];

            let r = c.select(&it);

            assert!(r.is_none());
        }

        #[test]
        fn select_pickone_any_none() {
            let c = PickOne(vec![
                Item(vec!["item-1".into()]),
                Item(vec!["item-7".into()]),
            ]);

            let it = vec![
                item_order("o1".into(), "item-1".into()),
                item_order("o2".into(), "item-3".into()),
                item_order("o3".into(), "item-1".into()),
                item_order("o4".into(), "item-2".into()),
                item_order("o5".into(), "item-4".into()),
            ];

            let r = c.select(&it);

            assert!(r.is_none());
        }

        #[test]
        fn select_pickone_last_missing() {
            let c = PickOne(vec![
                Item(vec!["item-3".into()]),
                Item(vec!["item-3".into()]),
            ]);

            let it = vec![
                item_order("o1".into(), "item-1".into()),
                item_order("o2".into(), "item-3".into()),
                item_order("o3".into(), "item-1".into()),
                item_order("o4".into(), "item-2".into()),
                item_order("o5".into(), "item-4".into()),
            ];

            let r = c.select(&it);

            assert!(r.is_none());
        }
    }
}
