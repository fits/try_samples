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

pub trait Conditional<T> {
    fn check(&self, target: T) -> bool;
}

#[derive(Debug, Clone)]
pub struct PromotionGroup(Vec<PromotionRule>);

#[derive(Debug, Clone)]
pub struct PromotionRule {
    condition: OrderCondition,
    action: PromotionAction,
}

#[derive(Debug, Clone)]
pub enum OrderCondition {
    Anything,
    Attribute(AttrKey, Vec<AttrValue>),
    SubtotalRange(ItemCondition, Amount, Option<Amount>),
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

fn has_attr(target: &Attrs, key: &AttrKey, values: &Vec<AttrValue>) -> bool {
    target.get(key).map(|x| values.contains(x)).unwrap_or(false)
}

fn in_range_amount(target: &Amount, from: &Amount, to: &Option<Amount>) -> bool {
    target >= from && to.clone().map(|x| target <= &x).unwrap_or(true)
}

impl OrderCondition {
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

impl Conditional<&Order> for OrderCondition {
    fn check(&self, target: &Order) -> bool {
        match self {
            Self::Anything => true,
            Self::Attribute(k, v) => has_attr(&target.attrs, k, v),
            Self::IncludeItem(c) => target.lines.iter().any(|x| c.check(x)),
            Self::SubtotalRange(c, from, to) => {
                let mut v = Amount::zero();

                for l in &target.lines {
                    if c.check(l) {
                        v += l.price.clone();
                    }
                }

                in_range_amount(&v, from, to)
            }
            Self::Not(c) => !c.check(target),
            Self::And(c1, c2) => c1.check(target) && c2.check(target),
            Self::Or(c1, c2) => c1.check(target) || c2.check(target),
        }
    }
}

impl GroupCondition {
    pub fn qty_limit(&self, from: Quantity, to: Option<Quantity>) -> Self {
        Self::QtyLimit(Box::new(self.to_owned()), from, to)
    }

    fn select<'a>(&self, items: Vec<&'a OrderLine>) -> Option<Vec<&'a OrderLine>> {
        match self {
            Self::Items(c) => {
                let rs = items
                    .into_iter()
                    .filter(move |&x| c.check(x))
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
                        for i in &items {
                            if rs.contains(&i) {
                                continue;
                            }

                            if c.check(i) {
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
}

impl Conditional<&OrderLine> for ItemCondition {
    fn check(&self, target: &OrderLine) -> bool {
        match self {
            Self::Anything => true,
            Self::Item(items) => items.contains(&target.item_id),
            Self::Attribute(k, v) => has_attr(&target.attrs, k, v),
            Self::PriceRange(from, to) => in_range_amount(&target.price, from, to),
            Self::Not(c) => !c.check(target),
            Self::And(c1, c2) => c1.check(target) && c2.check(target),
            Self::Or(c1, c2) => c1.check(target) || c2.check(target),
        }
    }
}

impl<'a> RewardTarget<&'a OrderLine> {
    pub fn targets(&self) -> Vec<&'a OrderLine> {
        match self {
            RewardTarget::None => vec![],
            RewardTarget::Group(ts) => ts.clone(),
            RewardTarget::Single(t) => vec![t],
        }
    }
}

impl<'a> Reward<&'a OrderLine> {
    pub fn targets(&self) -> Vec<&'a OrderLine> {
        match self {
            Reward::Discount(d) => match d {
                DiscountReward::SingleDiscount(_, t, _) => t.targets(),
                DiscountReward::MultiDiscount(ts, _) => {
                    let mut res = vec![];

                    for (_, t) in ts {
                        res.append(&mut t.targets());
                    }

                    res
                }
            },
        }
    }
}

impl PromotionAction {
    pub fn apply<'a>(&self, target: Vec<&'a OrderLine>) -> Option<Vec<Reward<&'a OrderLine>>> {
        let (RewardAction::Discount(rule), upper) = match self {
            Self::All(a) => (a, None),
            Self::Any(n, a) => (a, Some(*n)),
        };

        let mut res = vec![];
        let mut items = target;

        loop {
            if upper.map(|x| x <= res.len()).unwrap_or(false) {
                break;
            }

            if let Some(r) = rule.apply(items.to_owned()) {
                let ts = r.targets();

                items.retain(|x| !ts.contains(x));

                res.push(r);
            } else {
                break;
            }
        }

        if res.is_empty() {
            None
        } else {
            Some(res)
        }
    }
}

impl PromotionRule {
    pub fn apply<'a>(
        &self,
        target: &'a Order,
        excludes: Option<Vec<&'a OrderLine>>,
    ) -> Option<Vec<Reward<&'a OrderLine>>> {
        if self.condition.check(target) {
            let lines = target
                .lines
                .iter()
                .filter(|x| excludes.to_owned().map(|y| !y.contains(x)).unwrap_or(true))
                .collect();

            self.action.apply(lines)
        } else {
            None
        }
    }
}

impl PromotionGroup {
    pub fn apply<'a>(&self, target: &'a Order) -> Option<Vec<Reward<&'a OrderLine>>> {
        let mut res: Vec<Reward<&'a OrderLine>> = vec![];

        for rule in &self.0 {
            let mut exc = vec![];

            for r in &res {
                exc.append(&mut r.targets());
            }

            let exc = if exc.is_empty() {
                None
            } else {
                Some(exc)
            };

            if let Some(r) = rule.apply(target, exc) {
                for x in r {
                    res.push(x.to_owned());    
                }
            }
        }

        if res.is_empty() {
            None
        } else {
            Some(res)
        }
    }
}


#[cfg(test)]
mod tests {
    use super::*;

    fn from_u(v: usize) -> Amount {
        Amount::from_integer(v.into())
    }

    fn order(id: OrderId, lines: Vec<OrderLine>) -> Order {
        Order {
            id,
            attrs: HashMap::new(),
            lines,
        }
    }

    fn order_with_attr(
        id: OrderId,
        lines: Vec<OrderLine>,
        key: AttrKey,
        value: AttrValue,
    ) -> Order {
        Order {
            id,
            attrs: HashMap::from([(key, value)]),
            lines,
        }
    }

    fn line(line_id: OrderLineId, item_id: ItemId) -> OrderLine {
        OrderLine {
            line_id,
            attrs: HashMap::new(),
            item_id,
            price: from_u(100),
        }
    }

    fn line_with_attr(line_id: OrderLineId, key: AttrKey, value: AttrValue) -> OrderLine {
        OrderLine {
            line_id,
            attrs: HashMap::from([(key, value)]),
            item_id: "item-1".into(),
            price: from_u(100),
        }
    }

    fn line_with_item_attr(
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

    fn line_with_price(line_id: OrderLineId, price: Amount) -> OrderLine {
        OrderLine {
            line_id,
            attrs: HashMap::new(),
            item_id: "item-1".into(),
            price,
        }
    }

    fn line_with_item_price(line_id: OrderLineId, item_id: ItemId, price: Amount) -> OrderLine {
        OrderLine {
            line_id,
            attrs: HashMap::new(),
            item_id,
            price,
        }
    }

    mod order_condition {
        use super::OrderCondition::*;
        use super::*;

        #[test]
        fn anything() {
            let c = Anything;

            assert!(c.check(&order("order1".into(), Vec::new())));
        }

        #[test]
        fn not() {
            let c = Anything.not();
            let o = order("order1".into(), Vec::new());

            assert_eq!(false, c.check(&o));
            assert!(c.not().check(&o));
        }

        #[test]
        fn and() {
            let o = order("order1".into(), Vec::new());

            assert!(Anything.and(Anything).check(&o));
            assert_eq!(false, Anything.and(Anything.not()).check(&o));
            assert_eq!(false, Anything.not().and(Anything).check(&o));
        }

        #[test]
        fn or() {
            let o = order("order1".into(), Vec::new());

            assert!(Anything.or(Anything).check(&o));
            assert!(Anything.or(Anything.not()).check(&o));
            assert!(Anything.not().or(Anything).check(&o));
            assert_eq!(false, Anything.not().or(Anything.not()).check(&o));
        }

        #[test]
        fn in_item() {
            let c = IncludeItem(ItemCondition::Item(vec!["item-2".into()]));

            let o = order(
                "order1".into(),
                vec![
                    line("line-1".into(), "item-1".into()),
                    line("line-2".into(), "item-2".into()),
                ],
            );

            assert!(c.check(&o));
        }

        #[test]
        fn not_in_item() {
            let c = IncludeItem(ItemCondition::Item(vec!["item-5".into()]));

            let o = order(
                "order1".into(),
                vec![
                    line("line-1".into(), "item-1".into()),
                    line("line-2".into(), "item-2".into()),
                ],
            );

            assert_eq!(false, c.check(&o));
        }

        #[test]
        fn attr_match() {
            let c = Attribute("category".into(), vec!["c1".into(), "c2".into()]);

            assert!(c.check(&order_with_attr(
                "o1".into(),
                vec![],
                "category".into(),
                "c1".into()
            )));
            assert!(c.check(&order_with_attr(
                "o2".into(),
                vec![],
                "category".into(),
                "c2".into()
            )));
        }

        #[test]
        fn attr_unmatch() {
            let c = Attribute("category".into(), vec!["c1".into(), "c2".into()]);

            assert_eq!(
                false,
                c.check(&order_with_attr(
                    "o1".into(),
                    vec![],
                    "category".into(),
                    "c5".into()
                ))
            );
        }

        #[test]
        fn subtotal_in_range() {
            let c = SubtotalRange(
                ItemCondition::Item(vec!["item-1".into(), "item-2".into()]),
                from_u(600),
                None,
            );

            let o = order(
                "order1".into(),
                vec![
                    line_with_item_price("line-1".into(), "item-1".into(), from_u(100)),
                    line_with_item_price("line-2".into(), "item-2".into(), from_u(200)),
                    line_with_item_price("line-3".into(), "item-3".into(), from_u(300)),
                    line_with_item_price("line-4".into(), "item-1".into(), from_u(100)),
                    line_with_item_price("line-5".into(), "item-2".into(), from_u(200)),
                    line_with_item_price("line-6".into(), "item-3".into(), from_u(300)),
                ],
            );

            assert!(c.check(&o));
        }

        #[test]
        fn subtotal_under() {
            let c = SubtotalRange(
                ItemCondition::Item(vec!["item-1".into(), "item-2".into()]),
                from_u(600),
                None,
            );

            let o = order(
                "order1".into(),
                vec![
                    line_with_item_price("line-1".into(), "item-1".into(), from_u(100)),
                    line_with_item_price("line-2".into(), "item-2".into(), from_u(200)),
                    line_with_item_price("line-3".into(), "item-3".into(), from_u(300)),
                    line_with_item_price("line-4".into(), "item-1".into(), from_u(100)),
                ],
            );

            assert_eq!(false, c.check(&o));
        }

        #[test]
        fn subtotal_with_upper_in_range() {
            let c = SubtotalRange(
                ItemCondition::Item(vec!["item-1".into(), "item-2".into()]),
                from_u(600),
                Some(from_u(700)),
            );

            let o = order(
                "order1".into(),
                vec![
                    line_with_item_price("line-1".into(), "item-1".into(), from_u(100)),
                    line_with_item_price("line-2".into(), "item-2".into(), from_u(200)),
                    line_with_item_price("line-3".into(), "item-3".into(), from_u(300)),
                    line_with_item_price("line-4".into(), "item-1".into(), from_u(100)),
                    line_with_item_price("line-5".into(), "item-2".into(), from_u(200)),
                    line_with_item_price("line-6".into(), "item-3".into(), from_u(300)),
                    line_with_item_price("line-7".into(), "item-1".into(), from_u(100)),
                ],
            );

            assert!(c.check(&o));
        }

        #[test]
        fn subtotal_over() {
            let c = SubtotalRange(
                ItemCondition::Item(vec!["item-1".into(), "item-2".into()]),
                from_u(600),
                Some(from_u(700)),
            );

            let o = order(
                "order1".into(),
                vec![
                    line_with_item_price("line-1".into(), "item-1".into(), from_u(100)),
                    line_with_item_price("line-2".into(), "item-2".into(), from_u(200)),
                    line_with_item_price("line-3".into(), "item-3".into(), from_u(300)),
                    line_with_item_price("line-4".into(), "item-1".into(), from_u(100)),
                    line_with_item_price("line-5".into(), "item-2".into(), from_u(200)),
                    line_with_item_price("line-6".into(), "item-3".into(), from_u(300)),
                    line_with_item_price("line-7".into(), "item-1".into(), from_u(100)),
                    line_with_item_price("line-8".into(), "item-1".into(), from_u(100)),
                ],
            );

            assert_eq!(false, c.check(&o));
        }
    }

    mod item_condition {
        use super::GroupCondition::*;
        use super::ItemCondition::*;
        use super::*;

        #[test]
        fn anything() {
            let c = Anything;

            assert!(c.check(&line("o1".into(), "item-1".into())));
            assert!(c.check(&line("o2".into(), "item-2".into())));
        }

        #[test]
        fn item_include() {
            let c = Item(vec!["item-1".into(), "item-2".into()]);

            assert!(c.check(&line("o1".into(), "item-1".into())));
            assert!(c.check(&line("o2".into(), "item-2".into())));
        }

        #[test]
        fn item_exclude() {
            let c = Item(vec!["item-1".into(), "item-2".into()]);

            assert_eq!(false, c.check(&line("o1".into(), "item-3".into())));
        }

        #[test]
        fn attr_match() {
            let c: ItemCondition = Attribute("category".into(), vec!["c1".into(), "c2".into()]);

            assert!(c.check(&line_with_attr("o1".into(), "category".into(), "c1".into())));
            assert!(c.check(&line_with_attr("o2".into(), "category".into(), "c2".into())));
        }

        #[test]
        fn attr_unmatch() {
            let c: ItemCondition = Attribute("category".into(), vec!["c1".into(), "c2".into()]);

            assert_eq!(
                false,
                c.check(&line_with_attr("o1".into(), "category".into(), "c3".into()))
            );
        }

        #[test]
        fn no_attr() {
            let c: ItemCondition = Attribute("category".into(), vec!["c1".into(), "c2".into()]);

            assert_eq!(
                false,
                c.check(&line_with_attr("o1".into(), "keyword".into(), "k1".into()))
            );
        }

        #[test]
        fn price_lower_match() {
            let c = PriceRange(from_u(100), None);

            assert!(c.check(&line_with_price("o1".into(), from_u(100))));
            assert!(c.check(&line_with_price("o1".into(), from_u(200))));
        }

        #[test]
        fn price_lower_unmatch() {
            let c = PriceRange(from_u(100), None);

            assert_eq!(false, c.check(&line_with_price("o1".into(), from_u(99))));
            assert_eq!(false, c.check(&line_with_price("o2".into(), from_u(0))));
        }

        #[test]
        fn price_upper_match() {
            let c = PriceRange(from_u(100), Some(from_u(200)));

            assert!(c.check(&line_with_price("o1".into(), from_u(100))));
            assert!(c.check(&line_with_price("o2".into(), from_u(150))));
            assert!(c.check(&line_with_price("o3".into(), from_u(200))));
        }

        #[test]
        fn price_upper_unmatch() {
            let c = PriceRange(from_u(100), Some(from_u(200)));

            assert_eq!(false, c.check(&line_with_price("o1".into(), from_u(201))));
            assert_eq!(false, c.check(&line_with_price("o2".into(), from_u(300))));
        }

        #[test]
        fn not_item() {
            let c = Item(vec!["item-1".into()]).not();

            assert_eq!(false, c.check(&line("o1".into(), "item-1".into())));
            assert!(c.check(&line("o2".into(), "item-2".into())));
        }

        #[test]
        fn and_match() {
            let c = Item(vec!["item-1".into(), "item-2".into()])
                .and(Attribute("category".into(), vec!["c1".into(), "c2".into()]));

            assert!(c.check(&line_with_item_attr(
                "o1".into(),
                "item-1".into(),
                "category".into(),
                "c2".into()
            )));
            assert!(c.check(&line_with_item_attr(
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
                c.check(&line_with_item_attr(
                    "o1".into(),
                    "item-3".into(),
                    "category".into(),
                    "c2".into()
                ))
            );
            assert_eq!(
                false,
                c.check(&line_with_item_attr(
                    "o2".into(),
                    "item-2".into(),
                    "category".into(),
                    "c3".into()
                ))
            );
            assert_eq!(
                false,
                c.check(&line_with_item_attr(
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

            assert!(c.check(&line_with_item_attr(
                "o1".into(),
                "item-1".into(),
                "category".into(),
                "c2".into()
            )));
            assert!(c.check(&line_with_item_attr(
                "o2".into(),
                "item-3".into(),
                "category".into(),
                "c1".into()
            )));

            assert!(c.check(&line_with_item_attr(
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
                c.check(&line_with_item_attr(
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

            let l1 = line("o1".into(), "item-1".into());
            let l2 = line("o2".into(), "item-3".into());
            let l3 = line("o3".into(), "item-1".into());
            let l4 = line("o4".into(), "item-2".into());
            let l5 = line("o5".into(), "item-4".into());

            let it = vec![&l1, &l2, &l3, &l4, &l5];

            let r = c.select(it).unwrap();

            assert_eq!(3, r.len());
            assert_eq!("o1", r.get(0).unwrap().line_id);
            assert_eq!("o3", r.get(1).unwrap().line_id);
            assert_eq!("o4", r.get(2).unwrap().line_id);
        }

        #[test]
        fn select_items_unmatch() {
            let c = Items(Item(vec!["item-10".into(), "item-11".into()]));

            let l1 = line("o1".into(), "item-1".into());
            let l2 = line("o2".into(), "item-3".into());
            let l3 = line("o3".into(), "item-1".into());
            let l4 = line("o4".into(), "item-2".into());
            let l5 = line("o5".into(), "item-4".into());

            let it = vec![&l1, &l2, &l3, &l4, &l5];

            assert!(c.select(it).is_none());
        }

        #[test]
        fn select_qty_lower_match() {
            let c = Items(Item(vec!["item-1".into(), "item-2".into()])).qty_limit(3, None);

            let l1 = line("o1".into(), "item-1".into());
            let l2 = line("o2".into(), "item-3".into());
            let l3 = line("o3".into(), "item-1".into());
            let l4 = line("o4".into(), "item-2".into());
            let l5 = line("o5".into(), "item-4".into());

            let it = vec![&l1, &l2, &l3, &l4, &l5];

            assert!(c.select(it).is_some());
        }

        #[test]
        fn select_qty_lower_unmatch() {
            let c = Items(Item(vec!["item-1".into(), "item-2".into()])).qty_limit(4, None);

            let l1 = line("o1".into(), "item-1".into());
            let l2 = line("o2".into(), "item-3".into());
            let l3 = line("o3".into(), "item-1".into());
            let l4 = line("o4".into(), "item-2".into());
            let l5 = line("o5".into(), "item-4".into());

            let it = vec![&l1, &l2, &l3, &l4, &l5];

            assert!(c.select(it).is_none());
        }

        #[test]
        fn select_qty_upper_over() {
            let c = Items(Item(vec!["item-1".into(), "item-2".into()])).qty_limit(1, Some(2));

            let l1 = line("o1".into(), "item-1".into());
            let l2 = line("o2".into(), "item-3".into());
            let l3 = line("o3".into(), "item-1".into());
            let l4 = line("o4".into(), "item-2".into());
            let l5 = line("o5".into(), "item-4".into());

            let it = vec![&l1, &l2, &l3, &l4, &l5];

            let r = c.select(it).unwrap();

            assert_eq!(2, r.len());
        }

        #[test]
        fn select_qty_upper_under() {
            let c = Items(Item(vec!["item-1".into(), "item-2".into()])).qty_limit(2, Some(5));

            let l1 = line("o1".into(), "item-1".into());
            let l2 = line("o2".into(), "item-3".into());
            let l3 = line("o3".into(), "item-1".into());
            let l4 = line("o4".into(), "item-2".into());
            let l5 = line("o5".into(), "item-4".into());

            let it = vec![&l1, &l2, &l3, &l4, &l5];

            let r = c.select(it).unwrap();

            assert_eq!(3, r.len());
        }

        #[test]
        fn select_qty_upper_zero() {
            let c = Items(Item(vec!["item-1".into(), "item-2".into()])).qty_limit(0, Some(0));

            let l1 = line("o1".into(), "item-1".into());
            let l2 = line("o2".into(), "item-3".into());
            let l3 = line("o3".into(), "item-1".into());
            let l4 = line("o4".into(), "item-2".into());
            let l5 = line("o5".into(), "item-4".into());

            let it = vec![&l1, &l2, &l3, &l4, &l5];

            assert!(c.select(it).is_none());
        }

        #[test]
        fn select_qty_upper_under_lower() {
            let c = Items(Item(vec!["item-1".into(), "item-2".into()])).qty_limit(2, Some(1));

            let l1 = line("o1".into(), "item-1".into());
            let l2 = line("o2".into(), "item-3".into());
            let l3 = line("o3".into(), "item-1".into());
            let l4 = line("o4".into(), "item-2".into());
            let l5 = line("o5".into(), "item-4".into());

            let it = vec![&l1, &l2, &l3, &l4, &l5];

            assert!(c.select(it).is_none());
        }

        #[test]
        fn select_pickone() {
            let c = PickOne(vec![
                Item(vec!["item-1".into()]),
                Item(vec!["item-2".into()]),
            ]);

            let l1 = line("o1".into(), "item-1".into());
            let l2 = line("o2".into(), "item-3".into());
            let l3 = line("o3".into(), "item-1".into());
            let l4 = line("o4".into(), "item-2".into());
            let l5 = line("o5".into(), "item-4".into());

            let it = vec![&l1, &l2, &l3, &l4, &l5];

            let r = c.select(it).unwrap();

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

            let l1 = line("o1".into(), "item-1".into());
            let l2 = line("o2".into(), "item-3".into());
            let l3 = line("o3".into(), "item-1".into());
            let l4 = line("o4".into(), "item-2".into());
            let l5 = line("o5".into(), "item-4".into());

            let it = vec![&l1, &l2, &l3, &l4, &l5];

            let r = c.select(it).unwrap();

            assert_eq!(3, r.len());
            assert_eq!("o1", r.get(0).unwrap().line_id);
            assert_eq!("o4", r.get(1).unwrap().line_id);
            assert_eq!("o3", r.get(2).unwrap().line_id);
        }

        #[test]
        fn select_pickone_empty() {
            let c = PickOne(vec![]);

            let l1 = line("o1".into(), "item-1".into());
            let l2 = line("o2".into(), "item-3".into());
            let l3 = line("o3".into(), "item-1".into());
            let l4 = line("o4".into(), "item-2".into());
            let l5 = line("o5".into(), "item-4".into());

            let it = vec![&l1, &l2, &l3, &l4, &l5];

            let r = c.select(it);

            assert!(r.is_none());
        }

        #[test]
        fn select_pickone_any_none() {
            let c = PickOne(vec![
                Item(vec!["item-1".into()]),
                Item(vec!["item-7".into()]),
            ]);

            let l1 = line("o1".into(), "item-1".into());
            let l2 = line("o2".into(), "item-3".into());
            let l3 = line("o3".into(), "item-1".into());
            let l4 = line("o4".into(), "item-2".into());
            let l5 = line("o5".into(), "item-4".into());

            let it = vec![&l1, &l2, &l3, &l4, &l5];

            let r = c.select(it);

            assert!(r.is_none());
        }

        #[test]
        fn select_pickone_last_missing() {
            let c = PickOne(vec![
                Item(vec!["item-3".into()]),
                Item(vec!["item-3".into()]),
            ]);

            let l1 = line("o1".into(), "item-1".into());
            let l2 = line("o2".into(), "item-3".into());
            let l3 = line("o3".into(), "item-1".into());
            let l4 = line("o4".into(), "item-2".into());
            let l5 = line("o5".into(), "item-4".into());

            let it = vec![&l1, &l2, &l3, &l4, &l5];

            let r = c.select(it);

            assert!(r.is_none());
        }
    }

    mod reward_target {
        use super::RewardTarget::*;
        use super::*;

        #[test]
        fn none_empty_targets() {
            assert_eq!(0, None::<&OrderLine>.targets().len());
        }

        #[test]
        fn single_targets() {
            let line = line("line1".into(), "item-1".into());

            let t = Single(&line);

            let ts = t.targets();

            assert_eq!(1, ts.len());
            assert_eq!("line1", ts.first().unwrap().line_id);
        }

        #[test]
        fn group_targets() {
            let line1 = line("line1".into(), "item-1".into());
            let line2 = line("line2".into(), "item-2".into());

            let t = Group(vec![&line1, &line2]);

            let ts = t.targets();

            assert_eq!(2, ts.len());
            assert_eq!("line1", ts.first().unwrap().line_id);
            assert_eq!("line2", ts.last().unwrap().line_id);
        }
    }

    mod reward {
        use super::Reward::*;
        use super::*;

        #[test]
        fn single_discount_targets() {
            let line = line("line1".into(), "item-1".into());

            let t = RewardTarget::Single(&line);

            let r = Discount(DiscountReward::SingleDiscount(
                from_u(50),
                t,
                discount::DiscountMethod::ValueDiscount(from_u(50)),
            ));

            let ts = r.targets();

            assert_eq!(1, ts.len());
            assert_eq!("line1", ts.first().unwrap().line_id);
        }

        #[test]
        fn multi_discount_targets() {
            let line1 = line("line1".into(), "item-1".into());
            let line2 = line("line2".into(), "item-2".into());
            let line3 = line("line3".into(), "item-3".into());

            let t1 = RewardTarget::Single(&line1);
            let t2 = RewardTarget::Group(vec![&line2, &line3]);

            let r = Discount(DiscountReward::MultiDiscount(
                vec![(None, t1), (None, t2)],
                discount::DiscountMethod::ValueDiscount(from_u(50)),
            ));

            let ts = r.targets();

            assert_eq!(3, ts.len());
            assert_eq!("line1", ts.first().unwrap().line_id);
            assert_eq!("line2", ts.get(1).unwrap().line_id);
            assert_eq!("line3", ts.last().unwrap().line_id);
        }
    }

    mod promotion_action {
        use super::PromotionAction::*;
        use super::RewardAction::*;
        use super::*;

        #[test]
        fn all_action() {
            let items = vec![
                line_with_item_price("line-1".into(), "item-1".into(), from_u(100)),
                line_with_item_price("line-2".into(), "item-2".into(), from_u(200)),
                line_with_item_price("line-3".into(), "item-3".into(), from_u(300)),
                line_with_item_price("line-4".into(), "item-1".into(), from_u(100)),
                line_with_item_price("line-5".into(), "item-2".into(), from_u(200)),
                line_with_item_price("line-6".into(), "item-3".into(), from_u(300)),
                line_with_item_price("line-7".into(), "item-1".into(), from_u(100)),
            ];

            let d = DiscountRule {
                condition: GroupCondition::PickOne(vec![
                    ItemCondition::Item(vec!["item-1".into(), "item-2".into()]),
                    ItemCondition::Item(vec!["item-3".into()]),
                ]),
                action: discount::DiscountAction::Whole(discount::DiscountMethod::ValueDiscount(
                    from_u(100),
                )),
            };

            let a = All(Discount(d));

            let it = items.iter().collect();

            let r = a.apply(it);

            assert!(r.is_some());

            let rs = r.unwrap();

            assert_eq!(2, rs.len());

            let r1 = rs.first().unwrap();
            let ts1 = r1.targets();
            assert_eq!(2, ts1.len());
            assert_eq!("line-1", ts1.first().unwrap().line_id);
            assert_eq!("line-3", ts1.last().unwrap().line_id);

            let r2 = rs.last().unwrap();
            let ts2 = r2.targets();

            assert_eq!(2, ts2.len());
            assert_eq!("line-2", ts2.first().unwrap().line_id);
            assert_eq!("line-6", ts2.last().unwrap().line_id);
        }

        #[test]
        fn any0_action() {
            let items = vec![
                line_with_item_price("line-1".into(), "item-1".into(), from_u(100)),
                line_with_item_price("line-2".into(), "item-2".into(), from_u(200)),
                line_with_item_price("line-3".into(), "item-3".into(), from_u(300)),
                line_with_item_price("line-4".into(), "item-1".into(), from_u(100)),
                line_with_item_price("line-5".into(), "item-2".into(), from_u(200)),
                line_with_item_price("line-6".into(), "item-3".into(), from_u(300)),
                line_with_item_price("line-7".into(), "item-1".into(), from_u(100)),
            ];

            let d = DiscountRule {
                condition: GroupCondition::PickOne(vec![
                    ItemCondition::Item(vec!["item-1".into(), "item-2".into()]),
                    ItemCondition::Item(vec!["item-3".into()]),
                ]),
                action: discount::DiscountAction::Whole(discount::DiscountMethod::ValueDiscount(
                    from_u(100),
                )),
            };

            let a = Any(0, Discount(d));

            let it = items.iter().collect();

            let r = a.apply(it);

            assert!(r.is_none());
        }

        #[test]
        fn any1_action() {
            let items = vec![
                line_with_item_price("line-1".into(), "item-1".into(), from_u(100)),
                line_with_item_price("line-2".into(), "item-2".into(), from_u(200)),
                line_with_item_price("line-3".into(), "item-3".into(), from_u(300)),
                line_with_item_price("line-4".into(), "item-1".into(), from_u(100)),
                line_with_item_price("line-5".into(), "item-2".into(), from_u(200)),
                line_with_item_price("line-6".into(), "item-3".into(), from_u(300)),
                line_with_item_price("line-7".into(), "item-1".into(), from_u(100)),
            ];

            let d = DiscountRule {
                condition: GroupCondition::PickOne(vec![
                    ItemCondition::Item(vec!["item-1".into(), "item-2".into()]),
                    ItemCondition::Item(vec!["item-3".into()]),
                ]),
                action: discount::DiscountAction::Whole(discount::DiscountMethod::ValueDiscount(
                    from_u(100),
                )),
            };

            let a = Any(1, Discount(d));

            let it = items.iter().collect();

            let r = a.apply(it);

            assert!(r.is_some());

            let rs = r.unwrap();

            assert_eq!(1, rs.len());

            let r1 = rs.first().unwrap();
            let ts1 = r1.targets();
            assert_eq!(2, ts1.len());
            assert_eq!("line-1", ts1.first().unwrap().line_id);
            assert_eq!("line-3", ts1.last().unwrap().line_id);
        }

        #[test]
        fn any2_action() {
            let items = vec![
                line_with_item_price("line-1".into(), "item-1".into(), from_u(100)),
                line_with_item_price("line-2".into(), "item-2".into(), from_u(200)),
                line_with_item_price("line-3".into(), "item-3".into(), from_u(300)),
                line_with_item_price("line-4".into(), "item-1".into(), from_u(100)),
                line_with_item_price("line-5".into(), "item-2".into(), from_u(200)),
                line_with_item_price("line-6".into(), "item-3".into(), from_u(300)),
                line_with_item_price("line-7".into(), "item-1".into(), from_u(100)),
            ];

            let d = DiscountRule {
                condition: GroupCondition::PickOne(vec![
                    ItemCondition::Item(vec!["item-1".into(), "item-2".into()]),
                    ItemCondition::Item(vec!["item-3".into()]),
                ]),
                action: discount::DiscountAction::Whole(discount::DiscountMethod::ValueDiscount(
                    from_u(100),
                )),
            };

            let a = Any(2, Discount(d));

            let it = items.iter().collect();

            let r = a.apply(it);

            assert!(r.is_some());

            let rs = r.unwrap();

            assert_eq!(2, rs.len());

            let r2 = rs.last().unwrap();
            let ts2 = r2.targets();

            assert_eq!(2, ts2.len());
            assert_eq!("line-2", ts2.first().unwrap().line_id);
            assert_eq!("line-6", ts2.last().unwrap().line_id);
        }
    }

    mod promotion_rule {
        use super::*;

        #[test]
        fn condition_ok() {
            let o = order_with_attr(
                "order1".into(),
                vec![
                    line_with_item_price("line-1".into(), "item-1".into(), from_u(100)),
                    line_with_item_price("line-2".into(), "item-2".into(), from_u(200)),
                    line_with_item_price("line-3".into(), "item-3".into(), from_u(300)),
                    line_with_item_price("line-4".into(), "item-1".into(), from_u(100)),
                    line_with_item_price("line-5".into(), "item-2".into(), from_u(200)),
                    line_with_item_price("line-6".into(), "item-3".into(), from_u(300)),
                    line_with_item_price("line-7".into(), "item-1".into(), from_u(100)),
                ],
                "kind".into(),
                "A1".into(),
            );

            let rule = PromotionRule {
                condition: OrderCondition::Attribute("kind".into(), vec!["A1".into(), "B2".into()]),
                action: PromotionAction::All(RewardAction::Discount(DiscountRule {
                    condition: GroupCondition::PickOne(vec![
                        ItemCondition::Item(vec!["item-1".into(), "item-2".into()]),
                        ItemCondition::Item(vec!["item-3".into()]),
                    ]),
                    action: discount::DiscountAction::Whole(
                        discount::DiscountMethod::ValueDiscount(from_u(100)),
                    ),
                })),
            };

            let r = rule.apply(&o, None);

            assert!(r.is_some());

            let rs = r.unwrap();

            assert_eq!(2, rs.len());
        }

        #[test]
        fn condition_ng() {
            let o = order_with_attr(
                "order1".into(),
                vec![
                    line_with_item_price("line-1".into(), "item-1".into(), from_u(100)),
                    line_with_item_price("line-2".into(), "item-2".into(), from_u(200)),
                    line_with_item_price("line-3".into(), "item-3".into(), from_u(300)),
                    line_with_item_price("line-4".into(), "item-1".into(), from_u(100)),
                    line_with_item_price("line-5".into(), "item-2".into(), from_u(200)),
                    line_with_item_price("line-6".into(), "item-3".into(), from_u(300)),
                    line_with_item_price("line-7".into(), "item-1".into(), from_u(100)),
                ],
                "kind".into(),
                "A1".into(),
            );

            let rule = PromotionRule {
                condition: OrderCondition::Attribute("kind".into(), vec!["B2".into()]),
                action: PromotionAction::All(RewardAction::Discount(DiscountRule {
                    condition: GroupCondition::PickOne(vec![
                        ItemCondition::Item(vec!["item-1".into(), "item-2".into()]),
                        ItemCondition::Item(vec!["item-3".into()]),
                    ]),
                    action: discount::DiscountAction::Whole(
                        discount::DiscountMethod::ValueDiscount(from_u(100)),
                    ),
                })),
            };

            let r = rule.apply(&o, None);

            assert!(r.is_none());
        }

        #[test]
        fn condition_ok_with_excludes() {
            let o = order_with_attr(
                "order1".into(),
                vec![
                    line_with_item_price("line-1".into(), "item-1".into(), from_u(100)),
                    line_with_item_price("line-2".into(), "item-2".into(), from_u(200)),
                    line_with_item_price("line-3".into(), "item-3".into(), from_u(300)),
                    line_with_item_price("line-4".into(), "item-1".into(), from_u(100)),
                    line_with_item_price("line-5".into(), "item-2".into(), from_u(200)),
                    line_with_item_price("line-6".into(), "item-3".into(), from_u(300)),
                    line_with_item_price("line-7".into(), "item-1".into(), from_u(100)),
                ],
                "kind".into(),
                "A1".into(),
            );

            let rule = PromotionRule {
                condition: OrderCondition::Attribute("kind".into(), vec!["A1".into(), "B2".into()]),
                action: PromotionAction::All(RewardAction::Discount(DiscountRule {
                    condition: GroupCondition::PickOne(vec![
                        ItemCondition::Item(vec!["item-1".into(), "item-2".into()]),
                        ItemCondition::Item(vec!["item-3".into()]),
                    ]),
                    action: discount::DiscountAction::Whole(
                        discount::DiscountMethod::ValueDiscount(from_u(100)),
                    ),
                })),
            };

            let exc = vec![o.lines.get(0).unwrap(), o.lines.get(3).unwrap()];

            let r = rule.apply(&o, Some(exc));

            assert!(r.is_some());

            let rs = r.unwrap();

            assert_eq!(2, rs.len());

            let r1 = rs.first().unwrap();
            let ts1 = r1.targets();
            assert_eq!(2, ts1.len());
            assert_eq!("line-2", ts1.first().unwrap().line_id);
            assert_eq!("line-3", ts1.last().unwrap().line_id);

            let r2 = rs.last().unwrap();
            let ts2 = r2.targets();

            assert_eq!(2, ts2.len());
            assert_eq!("line-5", ts2.first().unwrap().line_id);
            assert_eq!("line-6", ts2.last().unwrap().line_id);
        }
    }

    mod promotion_group {
        use super::*;

        #[test]
        fn multi_rules() {
            let o = order_with_attr(
                "order1".into(),
                vec![
                    line_with_item_price("line-1".into(), "item-1".into(), from_u(100)),
                    line_with_item_price("line-2".into(), "item-2".into(), from_u(200)),
                    line_with_item_price("line-3".into(), "item-3".into(), from_u(300)),
                    line_with_item_price("line-4".into(), "item-1".into(), from_u(100)),
                    line_with_item_price("line-5".into(), "item-2".into(), from_u(200)),
                    line_with_item_price("line-6".into(), "item-3".into(), from_u(300)),
                    line_with_item_price("line-7".into(), "item-1".into(), from_u(100)),
                ],
                "kind".into(),
                "A1".into(),
            );

            let group = PromotionGroup(vec![
                PromotionRule {
                    condition: OrderCondition::Attribute("kind".into(), vec!["A1".into(), "B2".into()]),
                    action: PromotionAction::Any(1, RewardAction::Discount(DiscountRule {
                        condition: GroupCondition::PickOne(vec![
                            ItemCondition::Item(vec!["item-1".into(), "item-2".into()]),
                            ItemCondition::Item(vec!["item-3".into()]),
                        ]),
                        action: discount::DiscountAction::Whole(
                            discount::DiscountMethod::ValueDiscount(from_u(100)),
                        ),
                    })),
                },
                PromotionRule {
                    condition: OrderCondition::Attribute("kind".into(), vec!["A1".into(), "B2".into()]),
                    action: PromotionAction::All(RewardAction::Discount(DiscountRule {
                        condition: GroupCondition::PickOne(vec![
                            ItemCondition::Item(vec!["item-4".into(), "item-5".into()]),
                        ]),
                        action: discount::DiscountAction::Whole(
                            discount::DiscountMethod::ValueDiscount(from_u(100)),
                        ),
                    })),
                },
                PromotionRule {
                    condition: OrderCondition::Attribute("kind".into(), vec!["A1".into(), "B2".into()]),
                    action: PromotionAction::All(RewardAction::Discount(DiscountRule {
                        condition: GroupCondition::PickOne(vec![
                            ItemCondition::Item(vec!["item-3".into()]),
                        ]),
                        action: discount::DiscountAction::Whole(
                            discount::DiscountMethod::ValueDiscount(from_u(100)),
                        ),
                    })),
                },
                PromotionRule {
                    condition: OrderCondition::Attribute("kind".into(), vec!["A1".into(), "B2".into()]),
                    action: PromotionAction::All(RewardAction::Discount(DiscountRule {
                        condition: GroupCondition::PickOne(vec![
                            ItemCondition::Item(vec!["item-3".into()]),
                        ]),
                        action: discount::DiscountAction::Whole(
                            discount::DiscountMethod::ValueDiscount(from_u(100)),
                        ),
                    })),
                },
            ]);

            let r = group.apply(&o);

            assert!(r.is_some());

            let rs = r.unwrap();

            assert_eq!(2, rs.len());

            let r1 = rs.first().unwrap();
            let ts1 = r1.targets();
            assert_eq!(2, ts1.len());
            assert_eq!("line-1", ts1.first().unwrap().line_id);
            assert_eq!("line-3", ts1.last().unwrap().line_id);

            let r2 = rs.last().unwrap();
            let ts2 = r2.targets();

            assert_eq!(1, ts2.len());
            assert_eq!("line-6", ts2.first().unwrap().line_id);

        }
    }
}
