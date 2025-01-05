use chrono::prelude::*;
use num::BigRational;
use num_traits::{One, Zero};

mod order;
use order::*;

mod discount;
use discount::*;

mod discount_price;
use discount_price::*;

pub type PromotionId = String;
pub type Amount = BigRational;
pub type Quantity = usize;
pub type Date = DateTime<Utc>;

pub type CouponCode = String;

pub trait Restrictional<T> {
    fn check(&self, target: T) -> bool;
}

pub trait Selector<S, T> {
    fn select_by(&self, target: S) -> Option<Vec<T>>;
}

pub trait Target<T> {
    fn targets(&self) -> Vec<T>;
}

#[derive(Debug, Clone, PartialEq)]
pub struct Promotion {
    pub id: PromotionId,
    pub name: String,
    pub description: Option<String>,
    restriction: Restriction,
    rule: Rule,
}

#[derive(Debug, Clone, PartialEq)]
pub enum Rule {
    Discount(DiscountRule),
    DiscountPrice(DiscountPriceRule),
    FreeShipping,
    FreeItem(ItemId),
    Coupon(CouponCode),
    And(Box<Self>, Box<Self>),
    Or(Box<Self>, Box<Self>),
}

#[derive(Debug, Clone, PartialEq)]
pub enum RewardTarget<T> {
    None,
    Group(Vec<T>),
    Single(T),
}

#[derive(Debug, Clone, PartialEq)]
pub enum Reward<T> {
    Discount(DiscountReward<T>),
    DiscountPrice(DiscountPriceReward<T>),
    FreeShipping,
    FreeItem(ItemId),
    Coupon(CouponCode),
}

#[derive(Debug, Clone, PartialEq)]
pub enum Restriction {
    Anything,
    Date(DateRestriction),
    HasItem(ItemRestriction),
    SubtotalRange(ItemRestriction, Amount, Option<Amount>),
    Not(Box<Self>),
    And(Box<Self>, Box<Self>),
    Or(Box<Self>, Box<Self>),
}

#[derive(Debug, Clone, PartialEq)]
pub enum DateRestriction {
    From(Date),
    To(Date),
    DayOfWeek(Weekday),
}

#[derive(Debug, Clone, PartialEq)]
pub enum ItemRestriction {
    Anything,
    Item(Vec<ItemId>),
    PriceRange(Amount, Option<Amount>),
    Attribute(AttrKey, Vec<AttrValue>),
    Not(Box<Self>),
    And(Box<Self>, Box<Self>),
    Or(Box<Self>, Box<Self>),
}

#[derive(Debug, Clone, PartialEq)]
pub enum GroupSelection {
    Items(ItemRestriction),
    EachPickOne(Vec<ItemRestriction>),
    MinQty(Box<Self>, Quantity),
    Take(Box<Self>, Quantity),
}

#[derive(Debug, Clone, PartialEq)]
pub enum PromotionTarget {
    Current,
    Id(PromotionId),
}

pub type PromotionReward<T> = (PromotionTarget, Vec<Reward<T>>);
pub type PromotionInput<'a> = (&'a Order, Vec<PromotionReward<&'a OrderLine>>);

fn in_range_amount(target: &Amount, from: &Amount, to: &Option<Amount>) -> bool {
    *from <= *target && to.clone().map(|x| x >= *target).unwrap_or(true)
}

impl Promotion {
    pub fn new(
        id: PromotionId,
        name: String,
        restriction: Restriction,
        rule: Rule,
        description: Option<String>,
    ) -> Self {
        Self {
            id,
            name,
            description,
            restriction,
            rule,
        }
    }

    pub fn apply<'a>(&self, target: PromotionInput<'a>) -> Option<Vec<Reward<&'a OrderLine>>> {
        if self.restriction.check(target.0) {
            self.rule.apply(target)
        } else {
            None
        }
    }
}

impl Rule {
    pub fn and(&self, other: Self) -> Self {
        Self::And(Box::new(self.to_owned()), Box::new(other))
    }

    pub fn or(&self, other: Self) -> Self {
        Self::Or(Box::new(self.to_owned()), Box::new(other))
    }

    fn apply<'a>(&self, target: PromotionInput<'a>) -> Option<Vec<Reward<&'a OrderLine>>> {
        match self {
            Self::Discount(r) => r.apply(target).map(|x| vec![Reward::Discount(x)]),
            Self::DiscountPrice(r) => r.apply(target).map(|x| vec![Reward::DiscountPrice(x)]),
            Self::FreeShipping => Some(vec![Reward::FreeShipping]),
            Self::FreeItem(v) => Some(vec![Reward::FreeItem(v.clone())]),
            Self::Coupon(v) => Some(vec![Reward::Coupon(v.clone())]),
            Self::And(a, b) => a.apply(target.clone()).and_then(|a| {
                let rs = [target.1, vec![(PromotionTarget::Current, a.clone())]].concat();
                b.apply((target.0, rs)).map(|b| [a, b].concat())
            }),
            Self::Or(a, b) => {
                let a = a.apply(target.clone());

                if a.is_some() {
                    return a;
                }

                b.apply(target)
            }
        }
    }
}

impl Restriction {
    pub fn not(&self) -> Self {
        Self::Not(Box::new(self.to_owned()))
    }

    pub fn and(&self, c: Self) -> Self {
        Self::And(Box::new(self.to_owned()), Box::new(c))
    }

    pub fn or(&self, c: Self) -> Self {
        Self::Or(Box::new(self.to_owned()), Box::new(c))
    }
}

impl Restrictional<&Order> for Restriction {
    fn check(&self, target: &Order) -> bool {
        match self {
            Self::Anything => true,
            Self::Date(a) => a.check(&target.order_at),
            Self::HasItem(a) => target.lines.iter().any(|x| a.check(x)),
            Self::SubtotalRange(a, f, t) => in_range_amount(&target.subtotal_by(a), f, t),
            Self::Not(a) => !a.check(target),
            Self::And(a, b) => a.check(target) && b.check(target),
            Self::Or(a, b) => a.check(target) || b.check(target),
        }
    }
}

impl Restrictional<&Date> for DateRestriction {
    fn check(&self, target: &Date) -> bool {
        match self {
            Self::From(d) => d <= target,
            Self::To(d) => d >= target,
            Self::DayOfWeek(d) => *d == target.weekday(),
        }
    }
}

impl ItemRestriction {
    pub fn not(&self) -> Self {
        Self::Not(Box::new(self.to_owned()))
    }

    pub fn and(&self, c: Self) -> Self {
        Self::And(Box::new(self.to_owned()), Box::new(c))
    }

    pub fn or(&self, c: Self) -> Self {
        Self::Or(Box::new(self.to_owned()), Box::new(c))
    }
}

impl Restrictional<&OrderLine> for ItemRestriction {
    fn check(&self, target: &OrderLine) -> bool {
        match self {
            Self::Anything => true,
            Self::Item(a) => a.contains(&target.item_id),
            Self::PriceRange(a, b) => in_range_amount(&target.price, a, b),
            Self::Attribute(k, vs) => target.attrs.get(k).map(|x| vs.contains(x)).unwrap_or(false),
            Self::Not(a) => !a.check(target),
            Self::And(a, b) => a.check(target) && b.check(target),
            Self::Or(a, b) => a.check(target) || b.check(target),
        }
    }
}

impl GroupSelection {
    pub fn min_qty(&self, qty: Quantity) -> Self {
        Self::MinQty(Box::new(self.to_owned()), qty)
    }

    pub fn take(&self, qty: Quantity) -> Self {
        Self::Take(Box::new(self.to_owned()), qty)
    }
}

impl<'a> Selector<PromotionInput<'a>, &'a OrderLine> for GroupSelection {
    fn select_by(&self, target: PromotionInput<'a>) -> Option<Vec<&'a OrderLine>> {
        let excludes = target.targets();

        let lines = target
            .0
            .lines
            .iter()
            .filter(|x| !excludes.contains(x))
            .collect::<Vec<_>>();

        match self {
            Self::Items(r) => {
                let vs = lines
                    .into_iter()
                    .filter(|&x| r.check(x))
                    .collect::<Vec<_>>();

                Some(vs).filter(|x| x.len() > 0)
            }
            Self::EachPickOne(rs) => {
                let mut vs: Vec<&'a OrderLine> = vec![];

                for r in rs {
                    for line in &lines {
                        if !vs.contains(&line) && r.check(line) {
                            vs.push(line);
                            break;
                        }
                    }
                }

                Some(vs).filter(|x| x.len() > 0 && x.len() == rs.len())
            }
            Self::MinQty(s, q) => s.select_by(target).filter(|x| x.len() >= *q),
            Self::Take(s, q) => s
                .select_by(target)
                .map(|x| x.into_iter().take(*q).collect::<Vec<_>>())
                .filter(|x| x.len() > 0),
        }
    }
}

impl<T: Clone> Target<T> for RewardTarget<T> {
    fn targets(&self) -> Vec<T> {
        match self {
            Self::None => vec![],
            Self::Group(x) => x.clone(),
            Self::Single(x) => vec![x.clone()],
        }
    }
}

impl<T: Clone> Target<T> for Reward<T> {
    fn targets(&self) -> Vec<T> {
        match self {
            Self::Discount(x) => x.targets(),
            Self::DiscountPrice(x) => x.1.targets(),
            Self::FreeShipping | Self::FreeItem(_) | Self::Coupon(_) => vec![],
        }
    }
}

impl<T: Clone> Target<T> for PromotionReward<T> {
    fn targets(&self) -> Vec<T> {
        self.1
            .iter()
            .fold(vec![], |acc, x| [acc, x.targets()].concat())
    }
}

impl<'a> Target<&'a OrderLine> for PromotionInput<'a> {
    fn targets(&self) -> Vec<&'a OrderLine> {
        self.1
            .iter()
            .fold(vec![], |acc, x| [acc, x.targets()].concat())
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use std::collections::HashMap;

    fn empty_order() -> Order {
        Order {
            order_at: Utc::now(),
            lines: vec![],
        }
    }

    fn test_order() -> Order {
        Order {
            order_at: Utc::now(),
            lines: vec![
                OrderLine {
                    line_id: "line1".into(),
                    item_id: "item-1".into(),
                    price: to_amount(100),
                    attrs: HashMap::new(),
                },
                OrderLine {
                    line_id: "line2".into(),
                    item_id: "item-2".into(),
                    price: to_amount(200),
                    attrs: HashMap::new(),
                },
            ],
        }
    }

    fn order_line(line_id: &str, item_id: &str, price: usize) -> OrderLine {
        OrderLine {
            line_id: line_id.into(),
            item_id: item_id.into(),
            price: to_amount(price),
            attrs: HashMap::new(),
        }
    }

    fn to_amount(v: usize) -> Amount {
        Amount::from_integer(v.into())
    }

    mod restriction {
        use super::Restriction::*;
        use super::*;

        #[test]
        fn anything() {
            let r = Anything;
            assert!(r.check(&empty_order()));
        }

        #[test]
        fn not() {
            let r = Anything.not();
            assert_eq!(false, r.check(&empty_order()));
        }

        #[test]
        fn and_both_true() {
            let r = Anything.and(Anything);
            assert!(r.check(&empty_order()));
        }

        #[test]
        fn and_right_false() {
            let r = Anything.and(Anything.not());
            assert_eq!(false, r.check(&empty_order()));
        }

        #[test]
        fn or_left_false() {
            let r = Anything.not().or(Anything);
            assert!(r.check(&empty_order()));
        }

        #[test]
        fn or_both_false() {
            let r = Anything.not().or(Anything.not());
            assert_eq!(false, r.check(&empty_order()));
        }

        #[test]
        fn date() {
            let r = Date(DateRestriction::From(Utc::now()));
            assert!(r.check(&empty_order()));
        }

        #[test]
        fn has_item() {
            let r = HasItem(ItemRestriction::Item(vec!["item-1".into()]));
            assert!(r.check(&test_order()));
        }

        #[test]
        fn not_has_item() {
            let r = HasItem(ItemRestriction::Item(vec!["item-3".into()]));
            assert_eq!(false, r.check(&test_order()));
        }

        #[test]
        fn subtotal_in_range() {
            let r = SubtotalRange(
                ItemRestriction::Item(vec!["item-1".into(), "item-2".into()]),
                to_amount(300),
                None,
            );
            assert!(r.check(&test_order()));
        }

        #[test]
        fn subtotal_in_range_with_filter() {
            let r = SubtotalRange(
                ItemRestriction::Item(vec!["item-1".into()]),
                to_amount(50),
                Some(to_amount(100)),
            );
            assert!(r.check(&test_order()));
        }

        #[test]
        fn subtotal_out_range() {
            let r = SubtotalRange(
                ItemRestriction::Item(vec!["item-1".into(), "item-2".into()]),
                to_amount(100),
                Some(to_amount(200)),
            );
            assert_eq!(false, r.check(&test_order()));
        }
    }

    mod date_restriction {
        use chrono::TimeDelta;

        use super::DateRestriction::*;
        use super::*;

        #[test]
        fn from_eq() {
            let now = Utc::now();

            let r = From(now.clone());
            assert!(r.check(&now));
        }

        #[test]
        fn from_before() {
            let now = Utc::now();

            let r = From(now.clone());

            let d = now.checked_sub_signed(TimeDelta::seconds(1)).unwrap();

            assert_eq!(false, r.check(&d));
        }

        #[test]
        fn from_after() {
            let now = Utc::now();

            let r = From(now.clone());

            let d = now.checked_add_signed(TimeDelta::seconds(1)).unwrap();

            assert!(r.check(&d));
        }

        #[test]
        fn to_eq() {
            let now = Utc::now();

            let r = To(now.clone());
            assert!(r.check(&now));
        }

        #[test]
        fn to_before() {
            let now = Utc::now();

            let r = To(now.clone());

            let d = now.checked_sub_signed(TimeDelta::seconds(1)).unwrap();

            assert!(r.check(&d));
        }

        #[test]
        fn to_after() {
            let now = Utc::now();

            let r = To(now.clone());

            let d = now.checked_add_signed(TimeDelta::seconds(1)).unwrap();

            assert_eq!(false, r.check(&d));
        }

        #[test]
        fn dayofweek_eq() {
            let now = Utc::now();

            let r = DayOfWeek(now.weekday());
            assert!(r.check(&now));
        }

        #[test]
        fn dayofweek_diff() {
            let now = Utc::now();

            let r = DayOfWeek(now.weekday());

            let d1 = now.checked_add_signed(TimeDelta::days(1)).unwrap();
            assert_eq!(false, r.check(&d1));

            let d2 = now.checked_sub_signed(TimeDelta::days(1)).unwrap();
            assert_eq!(false, r.check(&d2));
        }
    }

    mod item_restriction {
        use super::ItemRestriction::*;
        use super::*;

        fn order_line(item_id: ItemId, price: usize) -> OrderLine {
            OrderLine {
                line_id: "line1".into(),
                item_id,
                price: to_amount(price),
                attrs: HashMap::new(),
            }
        }

        fn order_line_with_attrs(item_id: &str, price: usize, key: &str, value: &str) -> OrderLine {
            OrderLine {
                line_id: "line1".into(),
                item_id: item_id.into(),
                price: to_amount(price),
                attrs: HashMap::from([(key.into(), value.into())]),
            }
        }

        #[test]
        fn anything() {
            let r = Anything;
            assert!(r.check(&order_line("item-1".into(), 100)));
        }

        #[test]
        fn item_id_include() {
            let r = Item(vec!["item-1".into(), "item-2".into()]);
            assert!(r.check(&order_line("item-2".into(), 100)));
        }

        #[test]
        fn item_id_not_include() {
            let r = Item(vec!["item-1".into(), "item-2".into()]);
            assert_eq!(false, r.check(&order_line("item-3".into(), 100)));
        }

        #[test]
        fn price_range_from_over_and_eq() {
            let r = PriceRange(to_amount(100), None);

            assert!(r.check(&order_line("item-1".into(), 100)));
            assert!(r.check(&order_line("item-1".into(), 200)));
        }

        #[test]
        fn price_range_from_under() {
            let r = PriceRange(to_amount(100), None);

            assert_eq!(false, r.check(&order_line("item-1".into(), 99)));
            assert_eq!(false, r.check(&order_line("item-1".into(), 50)));
        }

        #[test]
        fn price_range_to_over() {
            let r = PriceRange(to_amount(100), Some(to_amount(200)));

            assert_eq!(false, r.check(&order_line("item-1".into(), 201)));
            assert_eq!(false, r.check(&order_line("item-1".into(), 300)));
        }

        #[test]
        fn price_range_to_under_and_eq() {
            let r = PriceRange(to_amount(100), Some(to_amount(200)));

            assert!(r.check(&order_line("item-1".into(), 200)));
            assert!(r.check(&order_line("item-1".into(), 150)));
        }

        #[test]
        fn attrs_include() {
            let r = Attribute("category".into(), vec!["B2".into(), "A1".into()]);

            assert!(r.check(&order_line_with_attrs("item-1", 100, "category", "A1")));
            assert!(r.check(&order_line_with_attrs("item-1", 100, "category", "B2")));
        }

        #[test]
        fn attrs_no_value() {
            let r = Attribute("category".into(), vec!["B2".into(), "A1".into()]);

            assert_eq!(
                false,
                r.check(&order_line_with_attrs("item-1", 100, "category", "C3"))
            );
        }

        #[test]
        fn attrs_no_key() {
            let r = Attribute("category".into(), vec!["B2".into(), "A1".into()]);

            assert_eq!(
                false,
                r.check(&order_line_with_attrs("item-1", 100, "type", "A1"))
            );
        }

        #[test]
        fn attrs_empty_values() {
            let r = Attribute("category".into(), vec![]);

            assert_eq!(
                false,
                r.check(&order_line_with_attrs("item-1", 100, "category", "A1"))
            );
        }
    }

    mod selection {
        use super::order_line;
        use super::GroupSelection::*;
        use super::*;

        fn create_order(lines: Vec<OrderLine>) -> Order {
            Order {
                order_at: Utc::now(),
                lines,
            }
        }

        #[test]
        fn items_no_reward() {
            let lines = vec![
                order_line("line-1", "item-A", 100),
                order_line("line-2", "item-B", 200),
                order_line("line-3", "item-C", 300),
            ];

            let order = create_order(lines);

            let s = Items(
                ItemRestriction::Item(vec!["item-A".into()])
                    .or(ItemRestriction::Item(vec!["item-C".into()])),
            );

            if let Some(xs) = s.select_by((&order, vec![])) {
                assert_eq!(2, xs.len());
                assert_eq!("line-1", xs[0].line_id);
                assert_eq!("line-3", xs[1].line_id);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn items_reward() {
            let l1 = order_line("line-1", "item-A", 100);
            let l2 = order_line("line-2", "item-B", 200);
            let l3 = order_line("line-3", "item-C", 300);
            let l4 = order_line("line-4", "item-A", 100);
            let l5 = order_line("line-5", "item-C", 300);
            let l6 = order_line("line-6", "item-C", 300);
            let l7 = order_line("line-7", "item-C", 300);

            let order = create_order(vec![
                l1.clone(),
                l2.clone(),
                l3.clone(),
                l4.clone(),
                l5.clone(),
                l6.clone(),
                l7.clone(),
            ]);

            let s = Items(
                ItemRestriction::Item(vec!["item-A".into()])
                    .or(ItemRestriction::Item(vec!["item-C".into()])),
            );

            let rewards = vec![
                (PromotionTarget::Id("p1".into()), vec![Reward::FreeShipping]),
                (
                    PromotionTarget::Id("p2".into()),
                    vec![
                        Reward::DiscountPrice((
                            to_amount(160),
                            RewardTarget::Group(vec![&l1, &l2]),
                        )),
                        Reward::Discount(DiscountReward::One(
                            to_amount(20),
                            RewardTarget::Single(&l3),
                        )),
                    ],
                ),
                (
                    PromotionTarget::Id("p3".into()),
                    vec![Reward::Discount(DiscountReward::Bundle(vec![
                        (None, RewardTarget::Single(&l5)),
                        (Some(to_amount(50)), RewardTarget::Single(&l6)),
                    ]))],
                ),
            ];

            if let Some(xs) = s.select_by((&order, rewards)) {
                assert_eq!(2, xs.len());
                assert_eq!("line-4", xs[0].line_id);
                assert_eq!("line-7", xs[1].line_id);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn items_none_no_reward() {
            let lines = vec![
                order_line("line-1", "item-A", 100),
                order_line("line-2", "item-B", 200),
                order_line("line-3", "item-C", 300),
            ];

            let order = create_order(lines);

            let s = Items(ItemRestriction::Item(vec!["item-D".into()]));

            let res = s.select_by((&order, vec![]));

            assert!(res.is_none());
        }

        #[test]
        fn each_pickone_no_reward() {
            let lines = vec![
                order_line("line-1", "item-A", 100),
                order_line("line-2", "item-B", 200),
                order_line("line-3", "item-C", 300),
                order_line("line-4", "item-D", 400),
            ];

            let order = create_order(lines);

            let s = EachPickOne(vec![
                ItemRestriction::Item(vec!["item-B".into(), "item-A".into()]),
                ItemRestriction::Item(vec!["item-B".into()]).not(),
            ]);

            if let Some(xs) = s.select_by((&order, vec![])) {
                assert_eq!(2, xs.len());
                assert_eq!("line-1", xs[0].line_id);
                assert_eq!("line-3", xs[1].line_id);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn each_pickone_no_duplicate_no_reward() {
            let lines = vec![
                order_line("line-1", "item-A", 100),
                order_line("line-2", "item-B", 200),
                order_line("line-3", "item-A", 100),
                order_line("line-4", "item-D", 400),
            ];

            let order = create_order(lines);

            let s = EachPickOne(vec![
                ItemRestriction::Item(vec!["item-A".into(), "item-B".into()]),
                ItemRestriction::Item(vec!["item-B".into()]).not(),
            ]);

            if let Some(xs) = s.select_by((&order, vec![])) {
                assert_eq!(2, xs.len());
                assert_eq!("line-1", xs[0].line_id);
                assert_eq!("line-3", xs[1].line_id);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn each_pickone_with_empty_no_reward() {
            let lines = vec![
                order_line("line-1", "item-A", 100),
                order_line("line-2", "item-B", 200),
                order_line("line-3", "item-C", 300),
                order_line("line-4", "item-D", 400),
            ];

            let order = create_order(lines);

            let s = EachPickOne(vec![]);

            let res = s.select_by((&order, vec![]));

            assert!(res.is_none());
        }

        #[test]
        fn each_pickone_part_not_found_no_reward() {
            let lines = vec![
                order_line("line-1", "item-A", 100),
                order_line("line-2", "item-B", 200),
                order_line("line-3", "item-C", 300),
                order_line("line-4", "item-D", 400),
            ];

            let order = create_order(lines);

            let s = EachPickOne(vec![
                ItemRestriction::Item(vec!["item-B".into(), "item-A".into()]),
                ItemRestriction::Item(vec!["item-E".into()]),
            ]);

            let res = s.select_by((&order, vec![]));

            assert!(res.is_none());
        }

        #[test]
        fn min_qty_under_no_reward() {
            let lines = vec![
                order_line("line-1", "item-A", 100),
                order_line("line-2", "item-B", 200),
                order_line("line-3", "item-C", 300),
            ];

            let order = create_order(lines);

            let s = Items(
                ItemRestriction::Item(vec!["item-A".into()])
                    .or(ItemRestriction::Item(vec!["item-C".into()])),
            )
            .min_qty(1);

            if let Some(xs) = s.select_by((&order, vec![])) {
                assert_eq!(2, xs.len());
                assert_eq!("line-1", xs[0].line_id);
                assert_eq!("line-3", xs[1].line_id);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn min_qty_eq_no_reward() {
            let lines = vec![
                order_line("line-1", "item-A", 100),
                order_line("line-2", "item-B", 200),
                order_line("line-3", "item-C", 300),
            ];

            let order = create_order(lines);

            let s = Items(
                ItemRestriction::Item(vec!["item-A".into()])
                    .or(ItemRestriction::Item(vec!["item-C".into()])),
            )
            .min_qty(2);

            if let Some(xs) = s.select_by((&order, vec![])) {
                assert_eq!(2, xs.len());
                assert_eq!("line-1", xs[0].line_id);
                assert_eq!("line-3", xs[1].line_id);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn min_qty_zero_no_reward() {
            let lines = vec![
                order_line("line-1", "item-A", 100),
                order_line("line-2", "item-B", 200),
                order_line("line-3", "item-C", 300),
            ];

            let order = create_order(lines);

            let s = Items(
                ItemRestriction::Item(vec!["item-A".into()])
                    .or(ItemRestriction::Item(vec!["item-C".into()])),
            )
            .min_qty(0);

            if let Some(xs) = s.select_by((&order, vec![])) {
                assert_eq!(2, xs.len());
                assert_eq!("line-1", xs[0].line_id);
                assert_eq!("line-3", xs[1].line_id);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn min_qty_over_no_reward() {
            let lines = vec![
                order_line("line-1", "item-A", 100),
                order_line("line-2", "item-B", 200),
                order_line("line-3", "item-C", 300),
            ];

            let order = create_order(lines);

            let s = Items(
                ItemRestriction::Item(vec!["item-A".into()])
                    .or(ItemRestriction::Item(vec!["item-C".into()])),
            )
            .min_qty(3);

            let res = s.select_by((&order, vec![]));

            assert!(res.is_none());
        }

        #[test]
        fn min_qty_none_zero_no_reward() {
            let lines = vec![
                order_line("line-1", "item-A", 100),
                order_line("line-2", "item-B", 200),
                order_line("line-3", "item-C", 300),
            ];

            let order = create_order(lines);

            let s = Items(ItemRestriction::Item(vec!["item-D".into()])).min_qty(0);

            let res = s.select_by((&order, vec![]));

            assert!(res.is_none());
        }

        #[test]
        fn take_under_no_reward() {
            let lines = vec![
                order_line("line-1", "item-A", 100),
                order_line("line-2", "item-B", 200),
                order_line("line-3", "item-C", 300),
            ];

            let order = create_order(lines);

            let s = Items(
                ItemRestriction::Item(vec!["item-A".into()])
                    .or(ItemRestriction::Item(vec!["item-C".into()])),
            )
            .take(1);

            if let Some(xs) = s.select_by((&order, vec![])) {
                assert_eq!(1, xs.len());
                assert_eq!("line-1", xs[0].line_id);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn take_eq_no_reward() {
            let lines = vec![
                order_line("line-1", "item-A", 100),
                order_line("line-2", "item-B", 200),
                order_line("line-3", "item-C", 300),
            ];

            let order = create_order(lines);

            let s = Items(
                ItemRestriction::Item(vec!["item-A".into()])
                    .or(ItemRestriction::Item(vec!["item-C".into()])),
            )
            .take(2);

            if let Some(xs) = s.select_by((&order, vec![])) {
                assert_eq!(2, xs.len());
                assert_eq!("line-1", xs[0].line_id);
                assert_eq!("line-3", xs[1].line_id);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn take_over_no_reward() {
            let lines = vec![
                order_line("line-1", "item-A", 100),
                order_line("line-2", "item-B", 200),
                order_line("line-3", "item-C", 300),
            ];

            let order = create_order(lines);

            let s = Items(
                ItemRestriction::Item(vec!["item-A".into()])
                    .or(ItemRestriction::Item(vec!["item-C".into()])),
            )
            .take(3);

            if let Some(xs) = s.select_by((&order, vec![])) {
                assert_eq!(2, xs.len());
                assert_eq!("line-1", xs[0].line_id);
                assert_eq!("line-3", xs[1].line_id);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn take_zero_no_reward() {
            let lines = vec![
                order_line("line-1", "item-A", 100),
                order_line("line-2", "item-B", 200),
                order_line("line-3", "item-C", 300),
            ];

            let order = create_order(lines);

            let s = Items(
                ItemRestriction::Item(vec!["item-A".into()])
                    .or(ItemRestriction::Item(vec!["item-C".into()])),
            )
            .take(0);

            let res = s.select_by((&order, vec![]));

            assert!(res.is_none());
        }
    }

    mod rule {
        use super::Rule::*;
        use super::*;

        #[test]
        fn free_shipping() {
            let r = FreeShipping;

            let order = test_order();

            let res = r.apply((&order, vec![]));

            if let Some(vs) = res {
                assert_eq!(1, vs.len());
                assert_eq!(Reward::FreeShipping, vs[0]);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn free_item() {
            let r = FreeItem("free-item-1".into());

            let order = test_order();

            let res = r.apply((&order, vec![]));

            if let Some(vs) = res {
                assert_eq!(1, vs.len());
                assert_eq!(Reward::FreeItem("free-item-1".into()), vs[0]);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn coupon() {
            let r = Coupon("coupon-1".into());

            let order = test_order();

            let res = r.apply((&order, vec![]));

            if let Some(vs) = res {
                assert_eq!(1, vs.len());
                assert_eq!(Reward::Coupon("coupon-1".into()), vs[0]);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn discount() {
            let r = Discount(DiscountRule::new(
                GroupSelection::Items(ItemRestriction::Anything),
                DiscountAction::percentage(to_amount(10)),
            ));

            let order = test_order();

            let res = r.apply((&order, vec![]));

            if let Some(vs) = res {
                assert_eq!(1, vs.len());

                if let Reward::Discount(DiscountReward::One(v, _)) = vs[0].to_owned() {
                    assert_eq!(to_amount(30), v);
                } else {
                    assert!(false);
                }
            } else {
                assert!(false);
            }
        }

        #[test]
        fn discount_price() {
            let r = DiscountPrice(DiscountPriceRule::new(
                GroupSelection::Items(ItemRestriction::Anything),
                to_amount(250),
            ));

            let order = test_order();

            let res = r.apply((&order, vec![]));

            if let Some(vs) = res {
                assert_eq!(1, vs.len());

                if let Reward::DiscountPrice((p, t)) = vs[0].to_owned() {
                    assert_eq!(to_amount(250), p);

                    if let RewardTarget::Group(ts) = t {
                        assert_eq!(2, ts.len());
                        assert_eq!("line1", ts[0].line_id);
                    } else {
                        assert!(false);
                    }
                } else {
                    assert!(false);
                }
            } else {
                assert!(false);
            }
        }

        #[test]
        fn and_both_ok() {
            let r = FreeShipping.and(Discount(DiscountRule::new(
                GroupSelection::Items(ItemRestriction::Anything),
                DiscountAction::percentage(to_amount(10)),
            )));

            let order = test_order();

            let res = r.apply((&order, vec![]));

            if let Some(vs) = res {
                assert_eq!(2, vs.len());

                assert_eq!(Reward::FreeShipping, vs[0]);

                if let Reward::Discount(DiscountReward::One(v, _)) = vs[1].to_owned() {
                    assert_eq!(to_amount(30), v);
                } else {
                    assert!(false);
                }
            } else {
                assert!(false);
            }
        }

        #[test]
        fn and_left_ng() {
            let r = Discount(DiscountRule::new(
                GroupSelection::Items(ItemRestriction::Anything.not()),
                DiscountAction::percentage(to_amount(10)),
            ))
            .and(Discount(DiscountRule::new(
                GroupSelection::Items(ItemRestriction::Anything),
                DiscountAction::percentage(to_amount(10)),
            )));

            let order = test_order();

            let res = r.apply((&order, vec![]));

            assert!(res.is_none());
        }

        #[test]
        fn and_right_ng() {
            let r = Discount(DiscountRule::new(
                GroupSelection::Items(ItemRestriction::Anything),
                DiscountAction::percentage(to_amount(10)),
            ))
            .and(Discount(DiscountRule::new(
                GroupSelection::Items(ItemRestriction::Anything.not()),
                DiscountAction::percentage(to_amount(10)),
            )));

            let order = test_order();

            let res = r.apply((&order, vec![]));

            assert!(res.is_none());
        }

        #[test]
        fn and_both_discount() {
            let r = Discount(DiscountRule::new(
                GroupSelection::Items(ItemRestriction::Anything).take(1),
                DiscountAction::percentage(to_amount(10)),
            ))
            .and(Discount(DiscountRule::new(
                GroupSelection::Items(ItemRestriction::Anything),
                DiscountAction::percentage(to_amount(20)),
            )));

            let order = Order {
                order_at: Utc::now(),
                lines: vec![
                    OrderLine {
                        line_id: "line1".into(),
                        item_id: "item-1".into(),
                        price: to_amount(100),
                        attrs: HashMap::new(),
                    },
                    OrderLine {
                        line_id: "line2".into(),
                        item_id: "item-2".into(),
                        price: to_amount(200),
                        attrs: HashMap::new(),
                    },
                    OrderLine {
                        line_id: "line3".into(),
                        item_id: "item-3".into(),
                        price: to_amount(300),
                        attrs: HashMap::new(),
                    },
                ],
            };

            let res = r.apply((&order, vec![]));

            if let Some(vs) = res {
                assert_eq!(2, vs.len());

                if let Reward::Discount(DiscountReward::One(v, t)) = vs[0].to_owned() {
                    assert_eq!(to_amount(10), v);
                    assert_eq!(RewardTarget::Single(&order.lines[0]), t);
                } else {
                    assert!(false);
                }

                if let Reward::Discount(DiscountReward::One(v, t)) = vs[1].to_owned() {
                    assert_eq!(to_amount(100), v);
                    assert_eq!(
                        RewardTarget::Group(vec![&order.lines[1], &order.lines[2]]),
                        t
                    );
                } else {
                    assert!(false);
                }
            } else {
                assert!(false);
            }
        }

        #[test]
        fn or_both_ok() {
            let r = FreeShipping.or(Discount(DiscountRule::new(
                GroupSelection::Items(ItemRestriction::Anything),
                DiscountAction::percentage(to_amount(10)),
            )));

            let order = test_order();

            let res = r.apply((&order, vec![]));

            if let Some(vs) = res {
                assert_eq!(1, vs.len());
                assert_eq!(Reward::FreeShipping, vs[0]);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn or_left_ng() {
            let r = Discount(DiscountRule::new(
                GroupSelection::Items(ItemRestriction::Anything.not()),
                DiscountAction::percentage(to_amount(10)),
            ))
            .or(Discount(DiscountRule::new(
                GroupSelection::Items(ItemRestriction::Anything),
                DiscountAction::percentage(to_amount(10)),
            )));

            let order = test_order();

            let res = r.apply((&order, vec![]));

            if let Some(vs) = res {
                assert_eq!(1, vs.len());

                if let Reward::Discount(DiscountReward::One(v, _)) = vs[0].to_owned() {
                    assert_eq!(to_amount(30), v);
                } else {
                    assert!(false);
                }
            } else {
                assert!(false);
            }
        }

        #[test]
        fn or_both_ng() {
            let r = Discount(DiscountRule::new(
                GroupSelection::Items(ItemRestriction::Anything.not()),
                DiscountAction::percentage(to_amount(10)),
            ))
            .or(Discount(DiscountRule::new(
                GroupSelection::Items(ItemRestriction::Anything.not()),
                DiscountAction::percentage(to_amount(10)),
            )));

            let order = test_order();

            let res = r.apply((&order, vec![]));

            assert!(res.is_none());
        }
    }

    mod promotion {
        use super::*;

        #[test]
        fn free_shipping() {
            let p = Promotion {
                id: "p1".into(),
                name: "test promotion 1".into(),
                description: None,
                restriction: Restriction::HasItem(ItemRestriction::Item(vec!["item-1".into()])),
                rule: Rule::FreeShipping,
            };

            let order = test_order();

            let res = p.apply((&order, vec![]));

            if let Some(vs) = res {
                assert_eq!(1, vs.len());
                assert_eq!(Reward::FreeShipping, vs[0]);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn free_shipping_failed_restrict() {
            let p = Promotion {
                id: "p1".into(),
                name: "test promotion 1".into(),
                description: None,
                restriction: Restriction::HasItem(ItemRestriction::Item(vec![
                    "invalid-item".into()
                ])),
                rule: Rule::FreeShipping,
            };

            let order = test_order();

            let res = p.apply((&order, vec![]));

            assert!(res.is_none());
        }

        #[test]
        fn new_promotion() {
            let p = Promotion::new(
                "p2".into(),
                "test promotion 2".into(),
                Restriction::SubtotalRange(ItemRestriction::Anything, to_amount(150), None),
                Rule::FreeShipping,
                None,
            );

            let order = test_order();

            let res = p.apply((&order, vec![]));

            assert!(res.is_some());
        }
    }

    mod target {
        use super::RewardTarget::*;
        use super::*;

        #[test]
        fn none() {
            assert!(None::<&str>.targets().is_empty());
        }

        #[test]
        fn single() {
            let t = Single("item-1");

            let res = t.targets();
            assert_eq!(1, res.len());
            assert_eq!("item-1", res[0]);
        }

        #[test]
        fn group() {
            let t = Group(vec!["item-1", "item-2"]);

            let res = t.targets();
            assert_eq!(2, res.len());
            assert_eq!("item-1", res[0]);
            assert_eq!("item-2", res[1]);
        }
    }

    mod reward {
        use super::Reward::*;
        use super::*;

        #[test]
        fn free_shipping() {
            let r = FreeShipping::<&str>;
            assert!(r.targets().is_empty());
        }

        #[test]
        fn free_item() {
            let r = FreeItem::<&str>("free-item-1".into());
            assert!(r.targets().is_empty());
        }

        #[test]
        fn coupon() {
            let r = Coupon::<&str>("coupon-1".into());
            assert!(r.targets().is_empty());
        }

        #[test]
        fn discount() {
            let r = Discount::<&str>(DiscountReward::One(
                to_amount(10),
                RewardTarget::Group(vec!["item-1", "item-2"]),
            ));
            assert_eq!(vec!["item-1", "item-2"], r.targets());
        }

        #[test]
        fn discount_price() {
            let r = DiscountPrice::<&str>((
                to_amount(100),
                RewardTarget::Group(vec!["item-1", "item-2"]),
            ));
            assert_eq!(vec!["item-1", "item-2"], r.targets());
        }

        #[test]
        fn promotion_reward() {
            let r: PromotionReward<&str> = (
                PromotionTarget::Current,
                vec![
                    Reward::FreeShipping,
                    Reward::Discount(DiscountReward::One(
                        to_amount(10),
                        RewardTarget::Single("item-1"),
                    )),
                    Reward::DiscountPrice((
                        to_amount(100),
                        RewardTarget::Group(vec!["item-2", "item-1"]),
                    )),
                ],
            );
            assert_eq!(vec!["item-1", "item-2", "item-1"], r.targets());
        }
    }
}
