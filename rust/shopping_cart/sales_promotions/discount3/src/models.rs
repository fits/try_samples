#![allow(dead_code)]

use std::cmp::max;
use std::collections::HashMap;

use num::BigRational;
use num_traits::Zero;

pub type OrderItemId = String;
pub type ItemId = String;
pub type Amount = BigRational;
pub type Quantity = usize;

pub type AttrKey = String;
pub type AttrValue = String;
pub type Attrs = HashMap<AttrKey, AttrValue>;

#[derive(Debug, Clone, PartialEq)]
pub struct OrderItem {
    id: OrderItemId,
    item_id: ItemId,
    price: Amount,
    attrs: Attrs,
}

#[derive(Debug, Clone)]
pub enum ItemCondition {
    Item(Vec<ItemId>),
    Attribute(AttrKey, Vec<AttrValue>),
    PriceRange(Amount, Option<Amount>),
    Not(Box<ItemCondition>),
    And(Box<ItemCondition>, Box<ItemCondition>),
    Or(Box<ItemCondition>, Box<ItemCondition>),
}

impl ItemCondition {
    fn not(&self) -> Self {
        Self::Not(Box::new(self.to_owned()))
    }

    fn and(&self, c: Self) -> Self {
        Self::And(Box::new(self.to_owned()), Box::new(c))
    }

    fn or(&self, c: Self) -> Self {
        Self::Or(Box::new(self.to_owned()), Box::new(c))
    }

    fn predict(&self, target: &OrderItem) -> bool {
        match self {
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

#[derive(Debug, Clone)]
pub enum GroupCondition {
    Items(ItemCondition),
    QtyLimit(Box<GroupCondition>, Quantity, Option<Quantity>),
    PickOne(Vec<ItemCondition>),
}

impl GroupCondition {
    fn qty_limit(&self, from: Quantity, to: Option<Quantity>) -> Self {
        Self::QtyLimit(Box::new(self.to_owned()), from, to)
    }

    fn select<'a>(&self, items: &'a Vec<OrderItem>) -> Option<Vec<&'a OrderItem>> {
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
                    let mut rs: Vec<&'a OrderItem> = vec![];

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

#[derive(Debug, Clone)]
pub enum Reward<T> {
    GroupDiscount(Amount, Vec<T>, Option<Amount>),
    ItemDiscount(Vec<(Amount, T)>, Option<Amount>),
    GroupPrice(Amount, Vec<T>),
    ItemPrice(Vec<(Amount, T)>),
}

#[derive(Debug, Clone, PartialEq)]
pub enum DiscountMethod {
    ValueDiscount(Amount),
    RateDiscount(Amount),
    ChangePrice(Amount),
}

fn amount_100() -> Amount {
    Amount::from_integer(100.into())
}

impl DiscountMethod {
    fn value(v: Amount) -> Self {
        Self::ValueDiscount(v.max(Amount::zero()))
    }

    fn rate(v: Amount) -> Self {
        let r = v.max(Amount::zero()).min(amount_100()) / amount_100();
        Self::RateDiscount(r)
    }

    fn price(v: Amount) -> Self {
        Self::ChangePrice(v.max(Amount::zero()))
    }
}

fn subtotal(items: &Vec<&OrderItem>) -> Amount {
    let mut total = Amount::zero();

    for t in items {
        total += t.price.clone().max(Amount::zero());
    }

    total
}

#[derive(Debug, Clone, PartialEq)]
pub enum DiscountAction {
    Whole(DiscountMethod),
    Each(DiscountMethod, Option<Quantity>),
}

fn is_all_zero(rs: &Vec<(Amount, &OrderItem)>) -> bool {
    let zero = Amount::zero();

    for (r, _) in rs {
        if *r > zero {
            return false;
        }
    }

    true
}

fn is_all_same_price(rs: &Vec<(Amount, &OrderItem)>) -> bool {
    for (r, o) in rs {
        if *r != o.price {
            return false;
        }
    }

    true
}

impl DiscountAction {
    fn action<'a>(&self, items: Vec<&'a OrderItem>) -> Option<Reward<&'a OrderItem>> {
        match self {
            Self::Whole(m) => match m {
                DiscountMethod::ValueDiscount(v) => {
                    let v = subtotal(&items).min(v.clone());

                    if v > Amount::zero() {
                        Some(Reward::GroupDiscount(v, items, None))
                    } else {
                        None
                    }
                }
                DiscountMethod::RateDiscount(r) => {
                    let total = subtotal(&items);
                    let d = total * r;

                    if d > Amount::zero() {
                        Some(Reward::GroupDiscount(d, items, Some(r.clone())))
                    } else {
                        None
                    }
                }
                DiscountMethod::ChangePrice(p) => {
                    let total = subtotal(&items);
                    let price = p.clone().max(Amount::zero());

                    if total > price {
                        Some(Reward::GroupPrice(price, items))
                    } else {
                        None
                    }
                }
            },
            Self::Each(m, skip) => {
                let skip = skip.unwrap_or(0);

                if items.len() > skip {
                    match m {
                        DiscountMethod::ValueDiscount(v) => {
                            if *v > Amount::zero() {
                                let rs = items
                                    .into_iter()
                                    .enumerate()
                                    .map(|(i, x)| {
                                        if i < skip {
                                            (Amount::zero(), x)
                                        } else {
                                            (v.clone().min(x.price.clone()), x)
                                        }
                                    })
                                    .collect::<Vec<_>>();

                                if is_all_zero(&rs) {
                                    None
                                } else {
                                    Some(Reward::ItemDiscount(rs, None))
                                }
                            } else {
                                None
                            }
                        }
                        DiscountMethod::RateDiscount(r) => {
                            if *r > Amount::zero() {
                                let rs = items
                                    .into_iter()
                                    .enumerate()
                                    .map(|(i, x)| {
                                        if i < skip {
                                            (Amount::zero(), x)
                                        } else {
                                            (r.clone() * x.price.clone(), x)
                                        }
                                    })
                                    .collect::<Vec<_>>();

                                if is_all_zero(&rs) {
                                    None
                                } else {
                                    Some(Reward::ItemDiscount(rs, Some(r.clone())))
                                }
                            } else {
                                None
                            }
                        }
                        DiscountMethod::ChangePrice(p) => {
                            let rs = items
                                .into_iter()
                                .enumerate()
                                .map(|(i, x)| {
                                    if i < skip {
                                        (x.price.clone(), x)
                                    } else {
                                        (p.clone().min(x.price.clone()), x)
                                    }
                                })
                                .collect::<Vec<_>>();

                            if is_all_same_price(&rs) {
                                None
                            } else {
                                Some(Reward::ItemPrice(rs))
                            }
                        }
                    }
                } else {
                    None
                }
            }
        }
    }
}

#[derive(Debug, Clone)]
pub struct DiscountRule {
    condition: GroupCondition,
    action: DiscountAction,
}

impl DiscountRule {
    fn apply<'a>(&self, items: &'a Vec<OrderItem>) -> Option<Reward<&'a OrderItem>> {
        self.condition
            .select(items)
            .and_then(|x| self.action.action(x))
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    fn from_u(v: usize) -> Amount {
        Amount::from_integer(v.into())
    }

    fn from_i(v: i32) -> Amount {
        Amount::from_integer(v.into())
    }

    fn from_f(v: f32) -> Amount {
        Amount::from_float(v).unwrap()
    }

    fn item_order(id: OrderItemId, item_id: ItemId) -> OrderItem {
        OrderItem {
            id,
            item_id,
            price: from_u(100),
            attrs: HashMap::new(),
        }
    }

    fn attr_order(id: OrderItemId, key: AttrKey, value: AttrValue) -> OrderItem {
        OrderItem {
            id,
            item_id: "item-1".into(),
            price: from_u(100),
            attrs: HashMap::from([(key, value)]),
        }
    }

    fn item_attr_order(
        id: OrderItemId,
        item_id: ItemId,
        key: AttrKey,
        value: AttrValue,
    ) -> OrderItem {
        OrderItem {
            id,
            item_id,
            price: from_u(100),
            attrs: HashMap::from([(key, value)]),
        }
    }

    fn price_order(id: OrderItemId, price: Amount) -> OrderItem {
        OrderItem {
            id,
            item_id: "item-1".into(),
            price,
            attrs: HashMap::new(),
        }
    }

    fn item_price_order(id: OrderItemId, item_id: ItemId, price: Amount) -> OrderItem {
        OrderItem {
            id,
            item_id,
            price,
            attrs: HashMap::new(),
        }
    }

    #[test]
    fn subtotal_positive() {
        let o1 = price_order("o1".into(), from_u(100));
        let o2 = price_order("o2".into(), from_u(150));

        let r = subtotal(&vec![&o1, &o2]);
        assert_eq!(from_u(250), r);
    }

    #[test]
    fn subtotal_negative() {
        let o1 = price_order("o1".into(), from_u(100));
        let o2 = price_order("o2".into(), from_i(-150));
        let o3 = price_order("o2".into(), from_i(-250));

        let r = subtotal(&vec![&o1, &o2, &o3]);
        assert_eq!(from_u(100), r);
    }

    mod condition {
        use super::GroupCondition::*;
        use super::ItemCondition::*;
        use super::*;

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
            assert_eq!("o1", r.get(0).unwrap().id);
            assert_eq!("o3", r.get(1).unwrap().id);
            assert_eq!("o4", r.get(2).unwrap().id);
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
            assert_eq!("o1", r.get(0).unwrap().id);
            assert_eq!("o4", r.get(1).unwrap().id);
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
            assert_eq!("o1", r.get(0).unwrap().id);
            assert_eq!("o4", r.get(1).unwrap().id);
            assert_eq!("o3", r.get(2).unwrap().id);
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

    mod method {
        use super::DiscountAction::*;
        use super::DiscountMethod::*;
        use super::Reward::*;
        use super::*;

        #[test]
        fn value_100() {
            let m = DiscountMethod::value(from_u(100));

            if let ValueDiscount(v) = m {
                assert_eq!(from_u(100), v);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn value_negative() {
            let m = DiscountMethod::value(from_i(-100));

            if let ValueDiscount(v) = m {
                assert_eq!(from_u(0), v);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn price_100() {
            let m = DiscountMethod::price(from_u(100));

            if let ChangePrice(v) = m {
                assert_eq!(from_u(100), v);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn price_negative() {
            let m = DiscountMethod::price(from_i(-100));

            if let ChangePrice(v) = m {
                assert_eq!(from_i(0), v);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn rate_10() {
            let m = DiscountMethod::rate(from_u(50));

            if let RateDiscount(v) = m {
                assert_eq!(from_f(0.5), v);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn rate_over_100() {
            let m = DiscountMethod::rate(from_u(200));

            if let RateDiscount(v) = m {
                assert_eq!(from_u(1), v);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn rate_negative() {
            let m = DiscountMethod::rate(from_i(-50));

            if let RateDiscount(v) = m {
                assert_eq!(from_u(0), v);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn whole_value_discount() {
            let a = Whole(DiscountMethod::value(from_u(100)));

            let o1 = price_order("o1".into(), from_u(100));
            let o2 = price_order("o2".into(), from_u(150));
            let o3 = price_order("o3".into(), from_u(200));

            let r = a.action(vec![&o1, &o2, &o3]);

            if let Some(GroupDiscount(d, ts, None)) = r {
                assert_eq!(from_u(100), d);
                assert_eq!(3, ts.len());
                assert_eq!(&&o1, ts.get(0).unwrap());
                assert_eq!(&&o3, ts.get(2).unwrap());
            } else {
                assert!(false);
            }
        }

        #[test]
        fn whole_value_zero_discount() {
            let a = Whole(DiscountMethod::value(from_u(0)));

            let o1 = price_order("o1".into(), from_u(100));
            let o2 = price_order("o2".into(), from_u(150));
            let o3 = price_order("o3".into(), from_u(200));

            let r = a.action(vec![&o1, &o2, &o3]);

            assert!(r.is_none());
        }

        #[test]
        fn whole_value_over_discount() {
            let a = Whole(DiscountMethod::value(from_u(500)));

            let o1 = price_order("o1".into(), from_u(100));
            let o2 = price_order("o2".into(), from_u(150));
            let o3 = price_order("o3".into(), from_u(200));

            let r = a.action(vec![&o1, &o2, &o3]);

            if let Some(GroupDiscount(d, ts, None)) = r {
                assert_eq!(from_u(450), d);
                assert_eq!(3, ts.len());
                assert_eq!(&&o1, ts.get(0).unwrap());
                assert_eq!(&&o3, ts.get(2).unwrap());
            } else {
                assert!(false);
            }
        }

        #[test]
        fn whole_value_discount_zero_subtotal() {
            let a = Whole(DiscountMethod::value(from_u(500)));

            let o1 = price_order("o1".into(), from_u(0));

            let r = a.action(vec![&o1]);

            assert!(r.is_none());
        }

        #[test]
        fn whole_rate_discount() {
            let a = Whole(DiscountMethod::rate(from_u(10)));

            let o1 = price_order("o1".into(), from_u(100));
            let o2 = price_order("o2".into(), from_u(150));
            let o3 = price_order("o3".into(), from_u(200));

            let r = a.action(vec![&o1, &o2, &o3]);

            if let Some(GroupDiscount(d, ts, rate)) = r {
                assert_eq!(from_u(45), d);
                assert_eq!(from_u(1) / from_u(10), rate.unwrap());
                assert_eq!(3, ts.len());
                assert_eq!(&&o1, ts.get(0).unwrap());
                assert_eq!(&&o3, ts.get(2).unwrap());
            } else {
                assert!(false);
            }
        }

        #[test]
        fn whole_rate_discount_zero() {
            let a = Whole(DiscountMethod::rate(from_u(0)));

            let o1 = price_order("o1".into(), from_u(100));
            let o2 = price_order("o2".into(), from_u(150));
            let o3 = price_order("o3".into(), from_u(200));

            let r = a.action(vec![&o1, &o2, &o3]);

            assert!(r.is_none());
        }

        #[test]
        fn whole_rate_discount_zero_subtotal() {
            let a = Whole(DiscountMethod::rate(from_u(10)));

            let o1 = price_order("o1".into(), from_u(0));

            let r = a.action(vec![&o1]);

            assert!(r.is_none());
        }

        #[test]
        fn whole_price() {
            let a = Whole(DiscountMethod::price(from_u(400)));

            let o1 = price_order("o1".into(), from_u(100));
            let o2 = price_order("o2".into(), from_u(150));
            let o3 = price_order("o3".into(), from_u(200));

            let r = a.action(vec![&o1, &o2, &o3]);

            if let Some(GroupPrice(d, ts)) = r {
                assert_eq!(from_u(400), d);
                assert_eq!(3, ts.len());
                assert_eq!(&&o1, ts.get(0).unwrap());
                assert_eq!(&&o3, ts.get(2).unwrap());
            } else {
                assert!(false);
            }
        }

        #[test]
        fn whole_price_over() {
            let a = Whole(DiscountMethod::price(from_u(500)));

            let o1 = price_order("o1".into(), from_u(100));
            let o2 = price_order("o2".into(), from_u(150));
            let o3 = price_order("o3".into(), from_u(200));

            let r = a.action(vec![&o1, &o2, &o3]);

            assert!(r.is_none());
        }

        #[test]
        fn whole_price_same_subtotal() {
            let a = Whole(DiscountMethod::price(from_u(450)));

            let o1 = price_order("o1".into(), from_u(100));
            let o2 = price_order("o2".into(), from_u(150));
            let o3 = price_order("o3".into(), from_u(200));

            let r = a.action(vec![&o1, &o2, &o3]);

            assert!(r.is_none());
        }

        #[test]
        fn whole_price_zero_subtotal() {
            let a = Whole(DiscountMethod::price(from_u(450)));

            let o1 = price_order("o1".into(), from_u(0));

            let r = a.action(vec![&o1]);

            assert!(r.is_none());
        }

        #[test]
        fn each_value_discount() {
            let a = Each(DiscountMethod::value(from_u(100)), None);

            let o1 = price_order("o1".into(), from_u(100));
            let o2 = price_order("o2".into(), from_u(150));
            let o3 = price_order("o3".into(), from_u(200));

            let r = a.action(vec![&o1, &o2, &o3]);

            if let Some(ItemDiscount(d, None)) = r {
                assert_eq!(3, d.len());

                let (d1, i1) = d.get(0).unwrap();
                assert_eq!(from_u(100), *d1);
                assert_eq!(&&o1, i1);

                let (d2, i2) = d.get(1).unwrap();
                assert_eq!(from_u(100), *d2);
                assert_eq!(&&o2, i2);

                let (d3, i3) = d.get(2).unwrap();
                assert_eq!(from_u(100), *d3);
                assert_eq!(&&o3, i3);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn each_value_zero_discount() {
            let a = Each(DiscountMethod::value(from_u(0)), None);

            let o1 = price_order("o1".into(), from_u(100));
            let o2 = price_order("o2".into(), from_u(50));

            let r = a.action(vec![&o1, &o2]);

            assert!(r.is_none());
        }

        #[test]
        fn each_value_over_discount() {
            let a = Each(DiscountMethod::value(from_u(100)), None);

            let o1 = price_order("o1".into(), from_u(100));
            let o2 = price_order("o2".into(), from_u(50));

            let r = a.action(vec![&o1, &o2]);

            if let Some(ItemDiscount(d, None)) = r {
                assert_eq!(2, d.len());

                let (d2, i2) = d.get(1).unwrap();
                assert_eq!(from_u(50), *d2);
                assert_eq!(&&o2, i2);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn each_value_discount_skip1() {
            let a = Each(DiscountMethod::value(from_u(100)), Some(1));

            let o1 = price_order("o1".into(), from_u(100));
            let o2 = price_order("o2".into(), from_u(50));
            let o3 = price_order("o3".into(), from_u(200));

            let r = a.action(vec![&o1, &o2, &o3]);

            if let Some(ItemDiscount(d, None)) = r {
                assert_eq!(3, d.len());

                // no discount by skip
                let (d1, i1) = d.get(0).unwrap();
                assert_eq!(from_u(0), *d1);
                assert_eq!(&&o1, i1);

                let (d2, i2) = d.get(1).unwrap();
                assert_eq!(from_u(50), *d2);
                assert_eq!(&&o2, i2);

                let (d3, i3) = d.get(2).unwrap();
                assert_eq!(from_u(100), *d3);
                assert_eq!(&&o3, i3);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn each_value_discount_skip1_only_zero_discount() {
            let a = Each(DiscountMethod::value(from_u(100)), Some(1));

            let o1 = price_order("o1".into(), from_u(100));
            let o2 = price_order("o2".into(), from_u(0));

            let r = a.action(vec![&o1, &o2]);

            assert!(r.is_none());
        }

        #[test]
        fn each_value_discount_over_skip() {
            let a = Each(DiscountMethod::value(from_u(100)), Some(3));

            let o1 = price_order("o1".into(), from_u(100));
            let o2 = price_order("o2".into(), from_u(50));
            let o3 = price_order("o3".into(), from_u(200));

            let r = a.action(vec![&o1, &o2, &o3]);

            assert!(r.is_none());
        }

        #[test]
        fn each_rate_discount() {
            let a = Each(DiscountMethod::rate(from_u(20)), None);

            let o1 = price_order("o1".into(), from_u(100));
            let o2 = price_order("o2".into(), from_u(150));
            let o3 = price_order("o3".into(), from_u(200));

            let r = a.action(vec![&o1, &o2, &o3]);

            if let Some(ItemDiscount(d, Some(r))) = r {
                assert_eq!(3, d.len());
                assert_eq!(from_u(1) / from_u(5), r);

                let (d1, i1) = d.get(0).unwrap();
                assert_eq!(from_u(20), *d1);
                assert_eq!(&&o1, i1);

                let (d2, i2) = d.get(1).unwrap();
                assert_eq!(from_u(30), *d2);
                assert_eq!(&&o2, i2);

                let (d3, i3) = d.get(2).unwrap();
                assert_eq!(from_u(40), *d3);
                assert_eq!(&&o3, i3);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn each_rate_discount_zero() {
            let a = Each(DiscountMethod::rate(from_u(0)), None);

            let o1 = price_order("o1".into(), from_u(100));
            let o2 = price_order("o2".into(), from_u(150));

            let r = a.action(vec![&o1, &o2]);

            assert!(r.is_none());
        }

        #[test]
        fn each_rate_discount_skip1() {
            let a = Each(DiscountMethod::rate(from_u(20)), Some(1));

            let o1 = price_order("o1".into(), from_u(100));
            let o2 = price_order("o2".into(), from_u(150));
            let o3 = price_order("o3".into(), from_u(200));

            let r = a.action(vec![&o1, &o2, &o3]);

            if let Some(ItemDiscount(d, Some(r))) = r {
                assert_eq!(3, d.len());
                assert_eq!(from_u(1) / from_u(5), r);

                // no discount by skip
                let (d1, i1) = d.get(0).unwrap();
                assert_eq!(from_u(0), *d1);
                assert_eq!(&&o1, i1);

                let (d2, i2) = d.get(1).unwrap();
                assert_eq!(from_u(30), *d2);
                assert_eq!(&&o2, i2);

                let (d3, i3) = d.get(2).unwrap();
                assert_eq!(from_u(40), *d3);
                assert_eq!(&&o3, i3);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn each_rate_discount_skip1_zero_discount() {
            let a = Each(DiscountMethod::rate(from_u(20)), Some(1));

            let o1 = price_order("o1".into(), from_u(100));
            let o2 = price_order("o2".into(), from_u(0));

            let r = a.action(vec![&o1, &o2]);

            assert!(r.is_none());
        }

        #[test]
        fn each_price() {
            let a = Each(DiscountMethod::price(from_u(100)), None);

            let o1 = price_order("o1".into(), from_u(110));
            let o2 = price_order("o2".into(), from_u(150));
            let o3 = price_order("o3".into(), from_u(200));

            let r = a.action(vec![&o1, &o2, &o3]);

            if let Some(ItemPrice(d)) = r {
                assert_eq!(3, d.len());

                let (d1, i1) = d.get(0).unwrap();
                assert_eq!(from_u(100), *d1);
                assert_eq!(&&o1, i1);

                let (d2, i2) = d.get(1).unwrap();
                assert_eq!(from_u(100), *d2);
                assert_eq!(&&o2, i2);

                let (d3, i3) = d.get(2).unwrap();
                assert_eq!(from_u(100), *d3);
                assert_eq!(&&o3, i3);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn each_price_over() {
            let a = Each(DiscountMethod::price(from_u(100)), None);

            let o1 = price_order("o1".into(), from_u(110));
            let o2 = price_order("o2".into(), from_u(80));

            let r = a.action(vec![&o1, &o2]);

            if let Some(ItemPrice(d)) = r {
                assert_eq!(2, d.len());

                let (d1, i1) = d.get(0).unwrap();
                assert_eq!(from_u(100), *d1);
                assert_eq!(&&o1, i1);

                let (d2, i2) = d.get(1).unwrap();
                assert_eq!(from_u(80), *d2);
                assert_eq!(&&o2, i2);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn each_price_all_same_or_over() {
            let a = Each(DiscountMethod::price(from_u(100)), None);

            let o1 = price_order("o1".into(), from_u(100));
            let o2 = price_order("o2".into(), from_u(80));

            let r = a.action(vec![&o1, &o2]);

            assert!(r.is_none());
        }

        #[test]
        fn each_price_skip1() {
            let a = Each(DiscountMethod::price(from_u(100)), Some(1));

            let o1 = price_order("o1".into(), from_u(110));
            let o2 = price_order("o2".into(), from_u(150));
            let o3 = price_order("o3".into(), from_u(200));

            let r = a.action(vec![&o1, &o2, &o3]);

            if let Some(ItemPrice(d)) = r {
                assert_eq!(3, d.len());

                // no change by skip
                let (d1, i1) = d.get(0).unwrap();
                assert_eq!(from_u(110), *d1);
                assert_eq!(&&o1, i1);

                let (d2, i2) = d.get(1).unwrap();
                assert_eq!(from_u(100), *d2);
                assert_eq!(&&o2, i2);

                let (d3, i3) = d.get(2).unwrap();
                assert_eq!(from_u(100), *d3);
                assert_eq!(&&o3, i3);
            } else {
                assert!(false);
            }
        }
    }

    mod rule {
        use super::DiscountAction::*;
        use super::GroupCondition::*;
        use super::ItemCondition::*;
        use super::*;

        #[test]
        fn bogo_free() {
            let rule = DiscountRule {
                condition: Items(Item(vec!["item-1".into()])).qty_limit(2, Some(2)),
                action: Each(DiscountMethod::rate(from_u(100)), Some(1)),
            };

            let items = vec![
                item_price_order("o1".into(), "item-1".into(), from_u(100)),
                item_price_order("o2".into(), "item-2".into(), from_u(500)),
                item_price_order("o3".into(), "item-1".into(), from_u(100)),
                item_price_order("o4".into(), "item-1".into(), from_u(100)),
                item_price_order("o5".into(), "item-2".into(), from_u(500)),
            ];

            let r = rule.apply(&items);

            if let Some(Reward::ItemDiscount(d, Some(rate))) = r {
                assert_eq!(2, d.len());
                assert_eq!(from_u(1), rate);

                let (d1, i1) = d.get(0).unwrap();
                assert_eq!(from_u(0), *d1);
                assert_eq!("o1", i1.id);

                let (d2, i2) = d.get(1).unwrap();
                assert_eq!(from_u(100), *d2);
                assert_eq!("o3", i2.id);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn bogo_half() {
            let rule = DiscountRule {
                condition: Items(Item(vec!["item-1".into()])).qty_limit(2, Some(2)),
                action: Each(DiscountMethod::rate(from_u(50)), Some(1)),
            };

            let items = vec![
                item_price_order("o1".into(), "item-1".into(), from_u(100)),
                item_price_order("o2".into(), "item-2".into(), from_u(500)),
                item_price_order("o3".into(), "item-1".into(), from_u(100)),
                item_price_order("o4".into(), "item-1".into(), from_u(100)),
                item_price_order("o5".into(), "item-2".into(), from_u(500)),
            ];

            let r = rule.apply(&items);

            if let Some(Reward::ItemDiscount(d, Some(rate))) = r {
                assert_eq!(2, d.len());
                assert_eq!(from_u(1) / from_u(2), rate);

                let (d1, i1) = d.get(0).unwrap();
                assert_eq!(from_u(0), *d1);
                assert_eq!("o1", i1.id);

                let (d2, i2) = d.get(1).unwrap();
                assert_eq!(from_u(50), *d2);
                assert_eq!("o3", i2.id);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn set_price() {
            let rule = DiscountRule {
                condition: PickOne(vec![
                    Item(vec!["item-1".into()]),
                    Item(vec!["item-2".into()]),
                    Item(vec!["item-2".into()]),
                ]),
                action: Whole(DiscountMethod::price(from_u(700))),
            };

            let items = vec![
                item_price_order("o1".into(), "item-1".into(), from_u(100)),
                item_price_order("o2".into(), "item-2".into(), from_u(500)),
                item_price_order("o3".into(), "item-1".into(), from_u(100)),
                item_price_order("o4".into(), "item-1".into(), from_u(100)),
                item_price_order("o5".into(), "item-2".into(), from_u(500)),
            ];

            let r = rule.apply(&items);

            if let Some(Reward::GroupPrice(p, is)) = r {
                assert_eq!(from_u(700), p);

                assert_eq!(3, is.len());
                assert_eq!("o1", is.get(0).unwrap().id);
                assert_eq!("o2", is.get(1).unwrap().id);
                assert_eq!("o5", is.get(2).unwrap().id);
            } else {
                assert!(false);
            }
        }
    }
}
