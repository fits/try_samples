#![allow(dead_code)]

use super::*;

#[derive(Debug, Clone, PartialEq)]
pub struct DiscountRule {
    pub condition: GroupCondition,
    pub action: DiscountAction,
}

#[derive(Debug, Clone, PartialEq)]
pub enum DiscountAction {
    Whole(DiscountMethod),
    Each(DiscountMethod, Option<Quantity>, Option<Quantity>),
}

#[derive(Debug, Clone, PartialEq)]
pub enum DiscountMethod {
    FixedOff(Amount),
    PercentageOff(Amount),
    SpecialPrice(Amount),
}

#[derive(Debug, Clone)]
pub enum DiscountReward<T> {
    SingleDiscount(Amount, RewardTarget<T>, DiscountMethod),
    MultiDiscount(Vec<(Option<Amount>, RewardTarget<T>)>, DiscountMethod),
}

impl DiscountRule {
    pub fn apply<'a>(&self, items: Vec<&'a OrderLine>) -> Option<Reward<&'a OrderLine>> {
        self.condition
            .select(items)
            .and_then(|x| self.action.action(x).map(|r| Reward::Discount(r)))
    }
}

impl DiscountAction {
    pub fn each(m: DiscountMethod) -> Self {
        Self::Each(m, None, None)
    }

    pub fn each_with_skip(m: DiscountMethod, skip: Quantity) -> Self {
        Self::Each(m, Some(skip), None)
    }

    pub fn each_with_skip_take(m: DiscountMethod, skip: Quantity, take: Quantity) -> Self {
        Self::Each(m, Some(skip), Some(take))
    }

    fn action<'a>(&self, items: Vec<&'a OrderLine>) -> Option<DiscountReward<&'a OrderLine>> {
        match self {
            Self::Whole(m) => whole_discount(items, m),
            Self::Each(m, skip, take) => each_discount(&items, m, skip.unwrap_or(0), take),
        }
    }
}

impl DiscountMethod {
    pub fn fixed_off(v: Amount) -> Self {
        Self::FixedOff(v.max(Amount::zero()))
    }

    pub fn percentage_off(v: Amount) -> Self {
        let r = v.max(Amount::zero()).min(amount_100()) / amount_100();
        Self::PercentageOff(r)
    }

    pub fn special_price(v: Amount) -> Self {
        Self::SpecialPrice(v.max(Amount::zero()))
    }
}

fn is_valid_discount(method: &DiscountMethod) -> bool {
    match method {
        DiscountMethod::FixedOff(x) => *x > Amount::zero(),
        DiscountMethod::PercentageOff(x) => *x > Amount::zero() && *x <= Amount::one(),
        DiscountMethod::SpecialPrice(x) => *x >= Amount::zero(),
    }
}

fn whole_discount<'a>(
    items: Vec<&'a OrderLine>,
    method: &DiscountMethod,
) -> Option<DiscountReward<&'a OrderLine>> {
    if !is_valid_discount(method) {
        return None;
    }

    let subtotal = subtotal(&items);

    if subtotal <= Amount::zero() {
        return None;
    }

    let discount = match method {
        DiscountMethod::FixedOff(v) => Some(v.clone().min(subtotal)),
        DiscountMethod::PercentageOff(v) => Some(subtotal * v),
        DiscountMethod::SpecialPrice(v) => Some(v.clone()).filter(|v| *v < subtotal),
    };

    discount.map(|d| DiscountReward::SingleDiscount(d, RewardTarget::Group(items), method.clone()))
}

fn each_discount<'a>(
    items: &Vec<&'a OrderLine>,
    method: &DiscountMethod,
    skip: usize,
    take: &Option<usize>,
) -> Option<DiscountReward<&'a OrderLine>> {
    if !is_valid_discount(method) || items.len() <= skip {
        return None;
    }

    let mut count: Quantity = 0;

    let rs = items
        .into_iter()
        .enumerate()
        .map(|(i, &x)| {
            if i < skip || is_full_count(take, &count) || x.price <= Amount::zero() {
                return (None, RewardTarget::Single(x));
            }

            let discount = match method {
                DiscountMethod::FixedOff(v) => Some(v.clone().min(x.price.clone())),
                DiscountMethod::PercentageOff(v) => Some(v.clone() * x.price.clone()),
                DiscountMethod::SpecialPrice(v) => Some(v.clone()).filter(|v| *v < x.price),
            };

            if discount.is_some() {
                count += 1;
            }

            (discount, RewardTarget::Single(x))
        })
        .collect::<Vec<_>>();

    if is_all_none(&rs) {
        None
    } else {
        Some(DiscountReward::MultiDiscount(rs, method.clone()))
    }
}

fn is_all_none(rs: &Vec<(Option<Amount>, RewardTarget<&OrderLine>)>) -> bool {
    for (r, _) in rs {
        if r.is_some() {
            return false;
        }
    }

    true
}

fn is_full_count(max_count: &Option<Quantity>, count: &Quantity) -> bool {
    max_count.map(|t| t <= *count).unwrap_or(false)
}

fn subtotal(items: &Vec<&OrderLine>) -> Amount {
    let mut total = Amount::zero();

    for t in items {
        total += t.price.clone();
    }

    total
}

fn amount_100() -> Amount {
    Amount::from_integer(100.into())
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

    fn item_price_order(line_id: OrderLineId, item_id: ItemId, price: Amount) -> OrderLine {
        OrderLine {
            line_id,
            attrs: HashMap::new(),
            item_id,
            price,
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
        assert_eq!(from_i(-300), r);
    }

    mod method {
        use super::DiscountAction::*;
        use super::DiscountMethod::*;
        use super::DiscountReward::*;
        use super::*;

        fn assert_line(exp: &OrderLine, act: &RewardTarget<&OrderLine>) {
            match act {
                RewardTarget::Single(t) => assert_eq!(&exp, t),
                _ => assert!(false),
            }
        }

        #[test]
        fn value_100() {
            let m = DiscountMethod::fixed_off(from_u(100));

            if let FixedOff(v) = m {
                assert_eq!(from_u(100), v);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn value_negative() {
            let m = DiscountMethod::fixed_off(from_i(-100));

            if let FixedOff(v) = m {
                assert_eq!(from_u(0), v);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn price_100() {
            let m = DiscountMethod::special_price(from_u(100));

            if let SpecialPrice(v) = m {
                assert_eq!(from_u(100), v);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn price_negative() {
            let m = DiscountMethod::special_price(from_i(-100));

            if let SpecialPrice(v) = m {
                assert_eq!(from_i(0), v);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn rate_10() {
            let m = DiscountMethod::percentage_off(from_u(50));

            if let PercentageOff(v) = m {
                assert_eq!(from_f(0.5), v);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn rate_over_100() {
            let m = DiscountMethod::percentage_off(from_u(200));

            if let PercentageOff(v) = m {
                assert_eq!(from_u(1), v);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn rate_negative() {
            let m = DiscountMethod::percentage_off(from_i(-50));

            if let PercentageOff(v) = m {
                assert_eq!(from_u(0), v);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn whole_value_discount() {
            let a = Whole(DiscountMethod::fixed_off(from_u(100)));

            let o1 = price_order("o1".into(), from_u(100));
            let o2 = price_order("o2".into(), from_u(150));
            let o3 = price_order("o3".into(), from_u(200));

            let r = a.action(vec![&o1, &o2, &o3]);

            if let Some(SingleDiscount(d, ts, m)) = r {
                assert_eq!(from_u(100), d);
                assert_eq!(DiscountMethod::fixed_off(from_u(100)), m);

                if let RewardTarget::Group(ts) = ts {
                    assert_eq!(3, ts.len());
                    assert_eq!(&&o1, ts.get(0).unwrap());
                    assert_eq!(&&o3, ts.get(2).unwrap());
                } else {
                    assert!(false);
                }
            } else {
                assert!(false);
            }
        }

        #[test]
        fn whole_value_zero_discount() {
            let a = Whole(DiscountMethod::fixed_off(from_u(0)));

            let o1 = price_order("o1".into(), from_u(100));
            let o2 = price_order("o2".into(), from_u(150));
            let o3 = price_order("o3".into(), from_u(200));

            let r = a.action(vec![&o1, &o2, &o3]);

            assert!(r.is_none());
        }

        #[test]
        fn whole_value_over_discount() {
            let a = Whole(DiscountMethod::fixed_off(from_u(500)));

            let o1 = price_order("o1".into(), from_u(100));
            let o2 = price_order("o2".into(), from_u(150));
            let o3 = price_order("o3".into(), from_u(200));

            let r = a.action(vec![&o1, &o2, &o3]);

            if let Some(SingleDiscount(d, ts, m)) = r {
                assert_eq!(from_u(450), d);
                assert_eq!(DiscountMethod::fixed_off(from_u(500)), m);

                if let RewardTarget::Group(ts) = ts {
                    assert_eq!(3, ts.len());
                    assert_eq!(&&o1, ts.get(0).unwrap());
                    assert_eq!(&&o3, ts.get(2).unwrap());
                } else {
                    assert!(false);
                }
            } else {
                assert!(false);
            }
        }

        #[test]
        fn whole_value_discount_zero_subtotal() {
            let a = Whole(DiscountMethod::fixed_off(from_u(500)));

            let o1 = price_order("o1".into(), from_u(0));

            let r = a.action(vec![&o1]);

            assert!(r.is_none());
        }

        #[test]
        fn whole_value_discount_include_negative_price() {
            let a = Whole(DiscountMethod::fixed_off(from_u(200)));

            let o1 = price_order("o1".into(), from_u(100));
            let o2 = price_order("o2".into(), from_u(150));
            let o3 = price_order("o3".into(), from_i(-200));

            let r = a.action(vec![&o1, &o2, &o3]);

            if let Some(SingleDiscount(d, _ts, m)) = r {
                assert_eq!(from_u(50), d);
                assert_eq!(DiscountMethod::fixed_off(from_u(200)), m);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn whole_value_discount_negative_subtotal() {
            let a = Whole(DiscountMethod::fixed_off(from_u(200)));

            let o1 = price_order("o1".into(), from_u(100));
            let o2 = price_order("o2".into(), from_i(-250));
            let o3 = price_order("o3".into(), from_i(-200));

            let r = a.action(vec![&o1, &o2, &o3]);

            assert!(r.is_none());
        }

        #[test]
        fn whole_rate_discount() {
            let a = Whole(DiscountMethod::percentage_off(from_u(10)));

            let o1 = price_order("o1".into(), from_u(100));
            let o2 = price_order("o2".into(), from_u(150));
            let o3 = price_order("o3".into(), from_u(200));

            let r = a.action(vec![&o1, &o2, &o3]);

            if let Some(SingleDiscount(d, ts, m)) = r {
                assert_eq!(from_u(45), d);
                assert_eq!(DiscountMethod::percentage_off(from_u(10)), m);

                if let RewardTarget::Group(ts) = ts {
                    assert_eq!(3, ts.len());
                    assert_eq!(&&o1, ts.get(0).unwrap());
                    assert_eq!(&&o3, ts.get(2).unwrap());
                } else {
                    assert!(false);
                }
            } else {
                assert!(false);
            }
        }

        #[test]
        fn whole_rate_discount_zero() {
            let a = Whole(DiscountMethod::percentage_off(from_u(0)));

            let o1 = price_order("o1".into(), from_u(100));
            let o2 = price_order("o2".into(), from_u(150));
            let o3 = price_order("o3".into(), from_u(200));

            let r = a.action(vec![&o1, &o2, &o3]);

            assert!(r.is_none());
        }

        #[test]
        fn whole_rate_discount_zero_subtotal() {
            let a = Whole(DiscountMethod::percentage_off(from_u(10)));

            let o1 = price_order("o1".into(), from_u(0));

            let r = a.action(vec![&o1]);

            assert!(r.is_none());
        }

        #[test]
        fn whole_rate_discount_include_negative_price() {
            let a = Whole(DiscountMethod::percentage_off(from_u(10)));

            let o1 = price_order("o1".into(), from_u(100));
            let o2 = price_order("o2".into(), from_u(150));
            let o3 = price_order("o3".into(), from_i(-150));

            let r = a.action(vec![&o1, &o2, &o3]);

            if let Some(SingleDiscount(d, _ts, m)) = r {
                assert_eq!(from_u(10), d);
                assert_eq!(DiscountMethod::percentage_off(from_u(10)), m);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn whole_rate_discount_subtotal_negative() {
            let a = Whole(DiscountMethod::percentage_off(from_u(10)));

            let o1 = price_order("o1".into(), from_u(100));
            let o2 = price_order("o2".into(), from_u(150));
            let o3 = price_order("o3".into(), from_i(-300));

            let r = a.action(vec![&o1, &o2, &o3]);

            assert!(r.is_none());
        }

        #[test]
        fn whole_price() {
            let a = Whole(DiscountMethod::special_price(from_u(400)));

            let o1 = price_order("o1".into(), from_u(100));
            let o2 = price_order("o2".into(), from_u(150));
            let o3 = price_order("o3".into(), from_u(200));

            let r = a.action(vec![&o1, &o2, &o3]);

            if let Some(SingleDiscount(d, ts, m)) = r {
                assert_eq!(from_u(400), d);
                assert_eq!(DiscountMethod::special_price(from_u(400)), m);

                if let RewardTarget::Group(ts) = ts {
                    assert_eq!(3, ts.len());
                    assert_eq!(&&o1, ts.get(0).unwrap());
                    assert_eq!(&&o3, ts.get(2).unwrap());
                } else {
                    assert!(false);
                }
            } else {
                assert!(false);
            }
        }

        #[test]
        fn whole_price_over() {
            let a = Whole(DiscountMethod::special_price(from_u(500)));

            let o1 = price_order("o1".into(), from_u(100));
            let o2 = price_order("o2".into(), from_u(150));
            let o3 = price_order("o3".into(), from_u(200));

            let r = a.action(vec![&o1, &o2, &o3]);

            assert!(r.is_none());
        }

        #[test]
        fn whole_price_same_subtotal() {
            let a = Whole(DiscountMethod::special_price(from_u(450)));

            let o1 = price_order("o1".into(), from_u(100));
            let o2 = price_order("o2".into(), from_u(150));
            let o3 = price_order("o3".into(), from_u(200));

            let r = a.action(vec![&o1, &o2, &o3]);

            assert!(r.is_none());
        }

        #[test]
        fn whole_price_include_negative_price() {
            let a = Whole(DiscountMethod::special_price(from_u(100)));

            let o1 = price_order("o1".into(), from_u(100));
            let o2 = price_order("o2".into(), from_u(150));
            let o3 = price_order("o3".into(), from_i(-100));

            let r = a.action(vec![&o1, &o2, &o3]);

            if let Some(SingleDiscount(d, _ts, _m)) = r {
                assert_eq!(from_u(100), d);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn whole_price_subtotal_negative() {
            let a = Whole(DiscountMethod::special_price(from_u(450)));

            let o1 = price_order("o1".into(), from_u(100));
            let o2 = price_order("o2".into(), from_u(150));
            let o3 = price_order("o3".into(), from_i(-300));

            let r = a.action(vec![&o1, &o2, &o3]);

            assert!(r.is_none());
        }

        #[test]
        fn whole_price_zero_subtotal() {
            let a = Whole(DiscountMethod::special_price(from_u(450)));

            let o1 = price_order("o1".into(), from_u(0));

            let r = a.action(vec![&o1]);

            assert!(r.is_none());
        }

        #[test]
        fn each_value_discount() {
            let a = DiscountAction::each(DiscountMethod::fixed_off(from_u(100)));

            let o1 = price_order("o1".into(), from_u(100));
            let o2 = price_order("o2".into(), from_u(150));
            let o3 = price_order("o3".into(), from_u(200));

            let r = a.action(vec![&o1, &o2, &o3]);

            if let Some(MultiDiscount(d, m)) = r {
                assert_eq!(3, d.len());
                assert_eq!(DiscountMethod::fixed_off(from_u(100)), m);

                let (d1, i1) = d.get(0).unwrap();
                assert_eq!(from_u(100), d1.to_owned().unwrap());
                assert_line(&o1, i1);

                let (d2, i2) = d.get(1).unwrap();
                assert_eq!(from_u(100), d2.to_owned().unwrap());
                assert_line(&o2, i2);

                let (d3, i3) = d.get(2).unwrap();
                assert_eq!(from_u(100), d3.to_owned().unwrap());
                assert_line(&o3, i3);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn each_value_zero_discount() {
            let a = DiscountAction::each(DiscountMethod::fixed_off(from_u(0)));

            let o1 = price_order("o1".into(), from_u(100));
            let o2 = price_order("o2".into(), from_u(50));

            let r = a.action(vec![&o1, &o2]);

            assert!(r.is_none());
        }

        #[test]
        fn each_value_over_discount() {
            let a = DiscountAction::each(DiscountMethod::fixed_off(from_u(100)));

            let o1 = price_order("o1".into(), from_u(100));
            let o2 = price_order("o2".into(), from_u(50));

            let r = a.action(vec![&o1, &o2]);

            if let Some(MultiDiscount(d, m)) = r {
                assert_eq!(2, d.len());
                assert_eq!(DiscountMethod::fixed_off(from_u(100)), m);

                let (d2, i2) = d.get(1).unwrap();
                assert_eq!(from_u(50), d2.to_owned().unwrap());
                assert_line(&o2, i2);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn each_value_discount_include_negative_price() {
            let a = DiscountAction::each(DiscountMethod::fixed_off(from_u(100)));

            let o1 = price_order("o1".into(), from_u(100));
            let o2 = price_order("o2".into(), from_u(150));
            let o3 = price_order("o3".into(), from_i(-200));

            let r = a.action(vec![&o1, &o2, &o3]);

            if let Some(MultiDiscount(d, m)) = r {
                assert_eq!(3, d.len());
                assert_eq!(DiscountMethod::fixed_off(from_u(100)), m);

                let (d1, i1) = d.get(0).unwrap();
                assert_eq!(from_u(100), d1.to_owned().unwrap());
                assert_line(&o1, i1);

                let (d2, i2) = d.get(1).unwrap();
                assert_eq!(from_u(100), d2.to_owned().unwrap());
                assert_line(&o2, i2);

                let (d3, i3) = d.get(2).unwrap();
                assert!(d3.is_none());
                assert_line(&o3, i3);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn each_value_discount_skip1() {
            let a = DiscountAction::each_with_skip(DiscountMethod::fixed_off(from_u(100)), 1);

            let o1 = price_order("o1".into(), from_u(100));
            let o2 = price_order("o2".into(), from_u(50));
            let o3 = price_order("o3".into(), from_u(200));

            let r = a.action(vec![&o1, &o2, &o3]);

            if let Some(MultiDiscount(d, _m)) = r {
                assert_eq!(3, d.len());

                // no discount by skip
                let (d1, i1) = d.get(0).unwrap();
                assert!(d1.is_none());
                assert_line(&o1, i1);

                let (d2, i2) = d.get(1).unwrap();
                assert_eq!(from_u(50), d2.to_owned().unwrap());
                assert_line(&o2, i2);

                let (d3, i3) = d.get(2).unwrap();
                assert_eq!(from_u(100), d3.to_owned().unwrap());
                assert_line(&o3, i3);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn each_value_discount_skip1_only_zero_discount() {
            let a = DiscountAction::each_with_skip(DiscountMethod::fixed_off(from_u(100)), 1);

            let o1 = price_order("o1".into(), from_u(100));
            let o2 = price_order("o2".into(), from_u(0));

            let r = a.action(vec![&o1, &o2]);

            assert!(r.is_none());
        }

        #[test]
        fn each_value_discount_over_skip() {
            let a = DiscountAction::each_with_skip(DiscountMethod::fixed_off(from_u(100)), 3);

            let o1 = price_order("o1".into(), from_u(100));
            let o2 = price_order("o2".into(), from_u(50));
            let o3 = price_order("o3".into(), from_u(200));

            let r = a.action(vec![&o1, &o2, &o3]);

            assert!(r.is_none());
        }

        #[test]
        fn each_value_discount_take2() {
            let a =
                DiscountAction::each_with_skip_take(DiscountMethod::fixed_off(from_u(100)), 0, 2);

            let o1 = price_order("o1".into(), from_u(100));
            let o2 = price_order("o2".into(), from_u(150));
            let o3 = price_order("o3".into(), from_u(200));

            let r = a.action(vec![&o1, &o2, &o3]);

            if let Some(MultiDiscount(d, m)) = r {
                assert_eq!(3, d.len());
                assert_eq!(DiscountMethod::fixed_off(from_u(100)), m);

                let (d1, i1) = d.get(0).unwrap();
                assert_eq!(from_u(100), d1.to_owned().unwrap());
                assert_line(&o1, i1);

                let (d2, i2) = d.get(1).unwrap();
                assert_eq!(from_u(100), d2.to_owned().unwrap());
                assert_line(&o2, i2);

                let (d3, i3) = d.get(2).unwrap();
                assert!(d3.is_none());
                assert_line(&o3, i3);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn each_value_discount_take3() {
            let a =
                DiscountAction::each_with_skip_take(DiscountMethod::fixed_off(from_u(100)), 0, 3);

            let o1 = price_order("o1".into(), from_u(100));
            let o2 = price_order("o2".into(), from_u(150));
            let o3 = price_order("o3".into(), from_u(200));

            let r = a.action(vec![&o1, &o2, &o3]);

            if let Some(MultiDiscount(d, m)) = r {
                assert_eq!(3, d.len());
                assert_eq!(DiscountMethod::fixed_off(from_u(100)), m);

                let (d1, i1) = d.get(0).unwrap();
                assert_eq!(from_u(100), d1.to_owned().unwrap());
                assert_line(&o1, i1);

                let (d2, i2) = d.get(1).unwrap();
                assert_eq!(from_u(100), d2.to_owned().unwrap());
                assert_line(&o2, i2);

                let (d3, i3) = d.get(2).unwrap();
                assert_eq!(from_u(100), d3.to_owned().unwrap());
                assert_line(&o3, i3);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn each_value_discount_over_take4() {
            let a =
                DiscountAction::each_with_skip_take(DiscountMethod::fixed_off(from_u(100)), 0, 4);

            let o1 = price_order("o1".into(), from_u(100));
            let o2 = price_order("o2".into(), from_u(150));
            let o3 = price_order("o3".into(), from_u(200));

            let r = a.action(vec![&o1, &o2, &o3]);

            if let Some(MultiDiscount(d, m)) = r {
                assert_eq!(3, d.len());
                assert_eq!(DiscountMethod::fixed_off(from_u(100)), m);

                let (d1, i1) = d.get(0).unwrap();
                assert_eq!(from_u(100), d1.to_owned().unwrap());
                assert_line(&o1, i1);

                let (d2, i2) = d.get(1).unwrap();
                assert_eq!(from_u(100), d2.to_owned().unwrap());
                assert_line(&o2, i2);

                let (d3, i3) = d.get(2).unwrap();
                assert_eq!(from_u(100), d3.to_owned().unwrap());
                assert_line(&o3, i3);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn each_value_discount_take_zero() {
            let a =
                DiscountAction::each_with_skip_take(DiscountMethod::fixed_off(from_u(100)), 0, 0);

            let o1 = price_order("o1".into(), from_u(100));
            let o2 = price_order("o2".into(), from_u(150));
            let o3 = price_order("o3".into(), from_u(200));

            let r = a.action(vec![&o1, &o2, &o3]);

            assert!(r.is_none());
        }

        #[test]
        fn each_value_discount_skip1_take1() {
            let a =
                DiscountAction::each_with_skip_take(DiscountMethod::fixed_off(from_u(100)), 1, 1);

            let o1 = price_order("o1".into(), from_u(100));
            let o2 = price_order("o2".into(), from_u(150));
            let o3 = price_order("o3".into(), from_u(200));

            let r = a.action(vec![&o1, &o2, &o3]);

            if let Some(MultiDiscount(d, m)) = r {
                assert_eq!(3, d.len());
                assert_eq!(DiscountMethod::fixed_off(from_u(100)), m);

                let (d1, i1) = d.get(0).unwrap();
                assert!(d1.is_none());
                assert_line(&o1, i1);

                let (d2, i2) = d.get(1).unwrap();
                assert_eq!(from_u(100), d2.to_owned().unwrap());
                assert_line(&o2, i2);

                let (d3, i3) = d.get(2).unwrap();
                assert!(d3.is_none());
                assert_line(&o3, i3);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn each_rate_discount() {
            let a = DiscountAction::each(DiscountMethod::percentage_off(from_u(20)));

            let o1 = price_order("o1".into(), from_u(100));
            let o2 = price_order("o2".into(), from_u(150));
            let o3 = price_order("o3".into(), from_u(200));

            let r = a.action(vec![&o1, &o2, &o3]);

            if let Some(MultiDiscount(d, m)) = r {
                assert_eq!(3, d.len());
                assert_eq!(DiscountMethod::percentage_off(from_u(20)), m);

                let (d1, i1) = d.get(0).unwrap();
                assert_eq!(from_u(20), d1.to_owned().unwrap());
                assert_line(&o1, i1);

                let (d2, i2) = d.get(1).unwrap();
                assert_eq!(from_u(30), d2.to_owned().unwrap());
                assert_line(&o2, i2);

                let (d3, i3) = d.get(2).unwrap();
                assert_eq!(from_u(40), d3.to_owned().unwrap());
                assert_line(&o3, i3);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn each_rate_discount_zero() {
            let a = DiscountAction::each(DiscountMethod::percentage_off(from_u(0)));

            let o1 = price_order("o1".into(), from_u(100));
            let o2 = price_order("o2".into(), from_u(150));

            let r = a.action(vec![&o1, &o2]);

            assert!(r.is_none());
        }

        #[test]
        fn each_rate_discount_include_negative_price() {
            let a = DiscountAction::each(DiscountMethod::percentage_off(from_u(20)));

            let o1 = price_order("o1".into(), from_u(100));
            let o2 = price_order("o2".into(), from_u(150));
            let o3 = price_order("o3".into(), from_i(-200));

            let r = a.action(vec![&o1, &o2, &o3]);

            if let Some(MultiDiscount(d, m)) = r {
                assert_eq!(3, d.len());
                assert_eq!(DiscountMethod::percentage_off(from_u(20)), m);

                let (d1, i1) = d.get(0).unwrap();
                assert_eq!(from_u(20), d1.to_owned().unwrap());
                assert_line(&o1, i1);

                let (d2, i2) = d.get(1).unwrap();
                assert_eq!(from_u(30), d2.to_owned().unwrap());
                assert_line(&o2, i2);

                let (d3, i3) = d.get(2).unwrap();
                assert!(d3.is_none());
                assert_line(&o3, i3);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn each_rate_discount_skip1() {
            let a = DiscountAction::each_with_skip(DiscountMethod::percentage_off(from_u(20)), 1);

            let o1 = price_order("o1".into(), from_u(100));
            let o2 = price_order("o2".into(), from_u(150));
            let o3 = price_order("o3".into(), from_u(200));

            let r = a.action(vec![&o1, &o2, &o3]);

            if let Some(MultiDiscount(d, m)) = r {
                assert_eq!(3, d.len());
                assert_eq!(DiscountMethod::percentage_off(from_u(20)), m);

                // no discount by skip
                let (d1, i1) = d.get(0).unwrap();
                assert!(d1.is_none());
                assert_line(&o1, i1);

                let (d2, i2) = d.get(1).unwrap();
                assert_eq!(from_u(30), d2.to_owned().unwrap());
                assert_line(&o2, i2);

                let (d3, i3) = d.get(2).unwrap();
                assert_eq!(from_u(40), d3.to_owned().unwrap());
                assert_line(&o3, i3);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn each_rate_discount_skip1_zero_discount() {
            let a = DiscountAction::each_with_skip(DiscountMethod::percentage_off(from_u(20)), 1);

            let o1 = price_order("o1".into(), from_u(100));
            let o2 = price_order("o2".into(), from_u(0));

            let r = a.action(vec![&o1, &o2]);

            assert!(r.is_none());
        }

        #[test]
        fn each_rate_discount_take2() {
            let a = DiscountAction::each_with_skip_take(
                DiscountMethod::percentage_off(from_u(20)),
                0,
                2,
            );

            let o1 = price_order("o1".into(), from_u(100));
            let o2 = price_order("o2".into(), from_u(150));
            let o3 = price_order("o3".into(), from_u(200));

            let r = a.action(vec![&o1, &o2, &o3]);

            if let Some(MultiDiscount(d, m)) = r {
                assert_eq!(3, d.len());
                assert_eq!(DiscountMethod::percentage_off(from_u(20)), m);

                let (d1, i1) = d.get(0).unwrap();
                assert_eq!(from_u(20), d1.to_owned().unwrap());
                assert_line(&o1, i1);

                let (d2, i2) = d.get(1).unwrap();
                assert_eq!(from_u(30), d2.to_owned().unwrap());
                assert_line(&o2, i2);

                let (d3, i3) = d.get(2).unwrap();
                assert!(d3.is_none());
                assert_line(&o3, i3);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn each_rate_discount_skip1_take1() {
            let a = DiscountAction::each_with_skip_take(
                DiscountMethod::percentage_off(from_u(20)),
                1,
                1,
            );

            let o1 = price_order("o1".into(), from_u(100));
            let o2 = price_order("o2".into(), from_u(150));
            let o3 = price_order("o3".into(), from_u(200));

            let r = a.action(vec![&o1, &o2, &o3]);

            if let Some(MultiDiscount(d, m)) = r {
                assert_eq!(3, d.len());
                assert_eq!(DiscountMethod::percentage_off(from_u(20)), m);

                let (d1, i1) = d.get(0).unwrap();
                assert!(d1.is_none());
                assert_line(&o1, i1);

                let (d2, i2) = d.get(1).unwrap();
                assert_eq!(from_u(30), d2.to_owned().unwrap());
                assert_line(&o2, i2);

                let (d3, i3) = d.get(2).unwrap();
                assert!(d3.is_none());
                assert_line(&o3, i3);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn each_rate_discount_take_zero() {
            let a = DiscountAction::each_with_skip_take(
                DiscountMethod::percentage_off(from_u(20)),
                0,
                0,
            );

            let o1 = price_order("o1".into(), from_u(100));
            let o2 = price_order("o2".into(), from_u(150));
            let o3 = price_order("o3".into(), from_u(200));

            let r = a.action(vec![&o1, &o2, &o3]);
            assert!(r.is_none());
        }

        #[test]
        fn each_price() {
            let a = DiscountAction::each(DiscountMethod::special_price(from_u(100)));

            let o1 = price_order("o1".into(), from_u(110));
            let o2 = price_order("o2".into(), from_u(150));
            let o3 = price_order("o3".into(), from_u(200));

            let r = a.action(vec![&o1, &o2, &o3]);

            if let Some(MultiDiscount(d, m)) = r {
                assert_eq!(3, d.len());
                assert_eq!(DiscountMethod::special_price(from_u(100)), m);

                let (d1, _i1) = d.get(0).unwrap();
                assert_eq!(from_u(100), d1.to_owned().unwrap());

                let (d2, _i2) = d.get(1).unwrap();
                assert_eq!(from_u(100), d2.to_owned().unwrap());

                let (d3, _i3) = d.get(2).unwrap();
                assert_eq!(from_u(100), d3.to_owned().unwrap());
            } else {
                assert!(false);
            }
        }

        #[test]
        fn each_price_over() {
            let a = DiscountAction::each(DiscountMethod::special_price(from_u(100)));

            let o1 = price_order("o1".into(), from_u(110));
            let o2 = price_order("o2".into(), from_u(80));

            let r = a.action(vec![&o1, &o2]);

            if let Some(MultiDiscount(d, m)) = r {
                assert_eq!(2, d.len());
                assert_eq!(DiscountMethod::special_price(from_u(100)), m);

                let (d1, i1) = d.get(0).unwrap();
                assert_eq!(from_u(100), d1.to_owned().unwrap());
                assert_line(&o1, i1);

                let (d2, i2) = d.get(1).unwrap();
                assert!(d2.is_none());
                assert_line(&o2, i2);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn each_price_all_same_or_over() {
            let a = DiscountAction::each(DiscountMethod::special_price(from_u(100)));

            let o1 = price_order("o1".into(), from_u(100));
            let o2 = price_order("o2".into(), from_u(80));

            let r = a.action(vec![&o1, &o2]);

            assert!(r.is_none());
        }

        #[test]
        fn each_price_include_negative_price() {
            let a = DiscountAction::each(DiscountMethod::special_price(from_u(100)));

            let o1 = price_order("o1".into(), from_u(110));
            let o2 = price_order("o2".into(), from_u(150));
            let o3 = price_order("o3".into(), from_i(-200));

            let r = a.action(vec![&o1, &o2, &o3]);

            if let Some(MultiDiscount(d, m)) = r {
                assert_eq!(3, d.len());
                assert_eq!(DiscountMethod::special_price(from_u(100)), m);

                let (d1, i1) = d.get(0).unwrap();
                assert_eq!(from_u(100), d1.to_owned().unwrap());
                assert_line(&o1, i1);

                let (d2, _i2) = d.get(1).unwrap();
                assert_eq!(from_u(100), d2.to_owned().unwrap());

                let (d3, _i3) = d.get(2).unwrap();
                assert!(d3.is_none());
            } else {
                assert!(false);
            }
        }

        #[test]
        fn each_price_skip1() {
            let a = DiscountAction::each_with_skip(DiscountMethod::special_price(from_u(100)), 1);

            let o1 = price_order("o1".into(), from_u(110));
            let o2 = price_order("o2".into(), from_u(150));
            let o3 = price_order("o3".into(), from_u(200));

            let r = a.action(vec![&o1, &o2, &o3]);

            if let Some(MultiDiscount(d, m)) = r {
                assert_eq!(3, d.len());
                assert_eq!(DiscountMethod::special_price(from_u(100)), m);

                // no change by skip
                let (d1, i1) = d.get(0).unwrap();
                assert!(d1.is_none());
                assert_line(&o1, i1);

                let (d2, i2) = d.get(1).unwrap();
                assert_eq!(from_u(100), d2.to_owned().unwrap());
                assert_line(&o2, i2);

                let (d3, i3) = d.get(2).unwrap();
                assert_eq!(from_u(100), d3.to_owned().unwrap());
                assert_line(&o3, i3);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn each_price_take2() {
            let a = DiscountAction::each_with_skip_take(
                DiscountMethod::special_price(from_u(100)),
                0,
                2,
            );

            let o1 = price_order("o1".into(), from_u(110));
            let o2 = price_order("o2".into(), from_u(150));
            let o3 = price_order("o3".into(), from_u(200));

            let r = a.action(vec![&o1, &o2, &o3]);

            if let Some(MultiDiscount(d, m)) = r {
                assert_eq!(3, d.len());
                assert_eq!(DiscountMethod::special_price(from_u(100)), m);

                let (d1, i1) = d.get(0).unwrap();
                assert_eq!(from_u(100), d1.to_owned().unwrap());
                assert_line(&o1, i1);

                let (d2, i2) = d.get(1).unwrap();
                assert_eq!(from_u(100), d2.to_owned().unwrap());
                assert_line(&o2, i2);

                let (d3, i3) = d.get(2).unwrap();
                assert!(d3.is_none());
                assert_line(&o3, i3);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn each_price_take_zero() {
            let a = DiscountAction::each_with_skip_take(
                DiscountMethod::special_price(from_u(100)),
                0,
                0,
            );

            let o1 = price_order("o1".into(), from_u(110));
            let o2 = price_order("o2".into(), from_u(150));
            let o3 = price_order("o3".into(), from_u(200));

            let r = a.action(vec![&o1, &o2, &o3]);

            assert!(r.is_none());
        }

        #[test]
        fn each_price_skip1_take1() {
            let a = DiscountAction::each_with_skip_take(
                DiscountMethod::special_price(from_u(100)),
                1,
                1,
            );

            let o1 = price_order("o1".into(), from_u(110));
            let o2 = price_order("o2".into(), from_u(150));
            let o3 = price_order("o3".into(), from_u(200));

            let r = a.action(vec![&o1, &o2, &o3]);

            if let Some(MultiDiscount(d, m)) = r {
                assert_eq!(3, d.len());
                assert_eq!(DiscountMethod::special_price(from_u(100)), m);

                let (d1, i1) = d.get(0).unwrap();
                assert!(d1.is_none());
                assert_line(&o1, i1);

                let (d2, i2) = d.get(1).unwrap();
                assert_eq!(from_u(100), d2.to_owned().unwrap());
                assert_line(&o2, i2);

                let (d3, i3) = d.get(2).unwrap();
                assert!(d3.is_none());
                assert_line(&o3, i3);
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

        fn line_id(t: &RewardTarget<&OrderLine>) -> Option<OrderLineId> {
            match t {
                RewardTarget::Single(s) => Some(s.line_id.clone()),
                _ => None,
            }
        }

        #[test]
        fn bogo_free() {
            let rule = DiscountRule {
                condition: Items(Item(vec!["item-1".into()])).qty_limit(2, Some(2)),
                action: DiscountAction::each_with_skip(
                    DiscountMethod::percentage_off(from_u(100)),
                    1,
                ),
            };

            let items = vec![
                item_price_order("o1".into(), "item-1".into(), from_u(100)),
                item_price_order("o2".into(), "item-2".into(), from_u(500)),
                item_price_order("o3".into(), "item-1".into(), from_u(100)),
                item_price_order("o4".into(), "item-1".into(), from_u(100)),
                item_price_order("o5".into(), "item-2".into(), from_u(500)),
            ];

            let it = items.iter().collect();

            let r = rule.apply(it);

            if let Some(Reward::Discount(r)) = r {
                if let DiscountReward::MultiDiscount(d, m) = r {
                    assert_eq!(2, d.len());
                    assert_eq!(DiscountMethod::percentage_off(from_u(100)), m);

                    let (d1, i1) = d.get(0).unwrap();
                    assert!(d1.is_none());

                    assert_eq!("o1", line_id(i1).unwrap());

                    let (d2, i2) = d.get(1).unwrap();
                    assert_eq!(from_u(100), d2.to_owned().unwrap());
                    assert_eq!("o3", line_id(i2).unwrap());
                } else {
                    assert!(false);
                }
            } else {
                assert!(false);
            }
        }

        #[test]
        fn bogo_half() {
            let rule = DiscountRule {
                condition: Items(Item(vec!["item-1".into()])).qty_limit(2, Some(2)),
                action: DiscountAction::each_with_skip(
                    DiscountMethod::percentage_off(from_u(50)),
                    1,
                ),
            };

            let items = vec![
                item_price_order("o1".into(), "item-1".into(), from_u(100)),
                item_price_order("o2".into(), "item-2".into(), from_u(500)),
                item_price_order("o3".into(), "item-1".into(), from_u(100)),
                item_price_order("o4".into(), "item-1".into(), from_u(100)),
                item_price_order("o5".into(), "item-2".into(), from_u(500)),
            ];

            let it = items.iter().collect();

            let r = rule.apply(it);

            if let Some(Reward::Discount(r)) = r {
                if let DiscountReward::MultiDiscount(d, m) = r {
                    assert_eq!(2, d.len());
                    assert_eq!(DiscountMethod::percentage_off(from_u(50)), m);

                    let (d1, i1) = d.get(0).unwrap();
                    assert!(d1.is_none());
                    assert_eq!("o1", line_id(i1).unwrap());

                    let (d2, i2) = d.get(1).unwrap();
                    assert_eq!(from_u(50), d2.to_owned().unwrap());
                    assert_eq!("o3", line_id(i2).unwrap());
                } else {
                    assert!(false);
                }
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
                action: Whole(DiscountMethod::special_price(from_u(700))),
            };

            let items = vec![
                item_price_order("o1".into(), "item-1".into(), from_u(100)),
                item_price_order("o2".into(), "item-2".into(), from_u(500)),
                item_price_order("o3".into(), "item-1".into(), from_u(100)),
                item_price_order("o4".into(), "item-1".into(), from_u(100)),
                item_price_order("o5".into(), "item-2".into(), from_u(500)),
            ];

            let it = items.iter().collect();

            let r = rule.apply(it);

            if let Some(Reward::Discount(r)) = r {
                if let DiscountReward::SingleDiscount(p, is, m) = r {
                    assert_eq!(from_u(700), p);
                    assert_eq!(DiscountMethod::special_price(from_u(700)), m);

                    if let RewardTarget::Group(is) = is {
                        assert_eq!(3, is.len());
                        assert_eq!("o1", is.get(0).unwrap().line_id);
                        assert_eq!("o2", is.get(1).unwrap().line_id);
                        assert_eq!("o5", is.get(2).unwrap().line_id);
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
    }
}
