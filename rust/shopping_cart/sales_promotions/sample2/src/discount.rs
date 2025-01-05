#![allow(dead_code)]

use super::*;

#[derive(Debug, Clone, PartialEq)]
pub struct DiscountRule {
    select: GroupSelection,
    action: DiscountAction,
}

#[derive(Debug, Clone, PartialEq)]
pub enum DiscountAction {
    FixedOff(Amount),
    PercentageOff(Amount),
    Each(Box<Self>, Quantity, Option<Quantity>),
}

#[derive(Debug, Clone, PartialEq)]
pub enum DiscountReward<T> {
    One(Amount, RewardTarget<T>),
    Bundle(Vec<(Option<Amount>, RewardTarget<T>)>),
}

impl DiscountRule {
    pub fn new(select: GroupSelection, action: DiscountAction) -> Self {
        Self { select, action }
    }

    pub fn apply<'a>(&self, target: PromotionInput<'a>) -> Option<DiscountReward<&'a OrderLine>> {
        self.select
            .select_by(target)
            .and_then(|x| self.action.action(x))
    }
}

impl DiscountAction {
    pub fn fixed(v: Amount) -> Self {
        Self::FixedOff(v.max(Amount::zero()))
    }

    pub fn percentage(v: Amount) -> Self {
        let r = v.max(Amount::zero()).min(Self::amount_100()) / Self::amount_100();
        Self::PercentageOff(r)
    }

    pub fn each(&self, skip: Quantity, take: Option<Quantity>) -> Self {
        Self::Each(Box::new(self.to_owned()), skip, take)
    }

    fn action<'a>(&self, lines: Vec<&'a OrderLine>) -> Option<DiscountReward<&'a OrderLine>> {
        if lines.is_empty() {
            return None;
        }

        let target = if lines.len() == 1 {
            RewardTarget::Single(lines[0])
        } else {
            RewardTarget::Group(lines.clone())
        };

        match self {
            Self::FixedOff(v) => {
                let subtotal = subtotal_lines(&lines);

                if *v > Amount::zero() && subtotal > Amount::zero() {
                    Some(DiscountReward::One(subtotal.min(v.clone()), target))
                } else {
                    None
                }
            }
            Self::PercentageOff(v) => {
                if *v > Amount::zero() && *v <= Amount::one() {
                    let discount = subtotal_lines(&lines) * v;

                    if discount > Amount::zero() {
                        Some(DiscountReward::One(discount, target))
                    } else {
                        None
                    }
                } else {
                    None
                }
            }
            Self::Each(s, skip, take) => {
                let mut rs = vec![];
                let take = take.unwrap_or(lines.len());

                for (i, &x) in lines.iter().enumerate() {
                    if i < *skip || i >= take + skip {
                        rs.push((None, RewardTarget::Single(x)));
                        continue;
                    }

                    let r = s.action(vec![x]);

                    if let Some(DiscountReward::One(d, t)) = r {
                        rs.push((Some(d), t));
                    } else {
                        rs.push((None, RewardTarget::Single(x)));
                    }
                }

                Some(rs.to_owned())
                    .filter(Self::is_any_some)
                    .map(DiscountReward::Bundle)
            }
        }
    }

    fn amount_100() -> Amount {
        Amount::from_integer(100.into())
    }

    fn is_any_some(ds: &Vec<(Option<Amount>, RewardTarget<&OrderLine>)>) -> bool {
        ds.iter().any(|(d, _)| d.is_some())
    }
}

impl<T: Clone> Target<T> for DiscountReward<T> {
    fn targets(&self) -> Vec<T> {
        match self {
            Self::One(_, t) => t.targets(),
            Self::Bundle(xs) => xs
                .iter()
                .fold(vec![], |acc, x| [acc, x.1.targets()].concat()),
        }
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use std::collections::HashMap;

    fn order_line(line_id: &str, item_id: &str, price: i32) -> OrderLine {
        OrderLine {
            line_id: line_id.into(),
            item_id: item_id.into(),
            price: to_amount_signed(price),
            attrs: HashMap::new(),
        }
    }

    fn to_amount(v: usize) -> Amount {
        Amount::from_integer(v.into())
    }

    fn to_amount_signed(v: i32) -> Amount {
        Amount::from_integer(v.into())
    }

    mod action {
        use super::DiscountAction::*;
        use super::*;

        #[test]
        fn create_fixed() {
            if let FixedOff(v) = DiscountAction::fixed(to_amount(10)) {
                assert_eq!(to_amount(10), v);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn create_fixed_zero() {
            if let FixedOff(v) = DiscountAction::fixed(to_amount(0)) {
                assert_eq!(Amount::zero(), v);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn create_fixed_negative() {
            if let FixedOff(v) = DiscountAction::fixed(to_amount_signed(-10)) {
                assert_eq!(Amount::zero(), v);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn fixed_empty() {
            let act = FixedOff(to_amount(200));

            let res = act.action(vec![]);

            assert!(res.is_none());
        }

        #[test]
        fn fixed_single() {
            let l1 = order_line("line-1", "item-1", 100);

            let act = FixedOff(to_amount(50));

            let res = act.action(vec![&l1]);

            if let Some(DiscountReward::One(v, t)) = res {
                assert_eq!(to_amount(50), v);

                if let RewardTarget::Single(t) = t {
                    assert_eq!("line-1", t.line_id);
                } else {
                    assert!(false);
                }
            } else {
                assert!(false);
            }
        }

        #[test]
        fn fixed_under() {
            let l1 = order_line("line-1", "item-1", 100);
            let l2 = order_line("line-2", "item-2", 200);

            let act = FixedOff(to_amount(200));

            let res = act.action(vec![&l1, &l2]);

            if let Some(DiscountReward::One(v, t)) = res {
                assert_eq!(to_amount(200), v);

                if let RewardTarget::Group(ts) = t {
                    assert_eq!(2, ts.len());
                    assert_eq!("line-1", ts[0].line_id);
                    assert_eq!("line-2", ts[1].line_id);
                } else {
                    assert!(false);
                }
            } else {
                assert!(false);
            }
        }

        #[test]
        fn fixed_zero() {
            let l1 = order_line("line-1", "item-1", 100);
            let l2 = order_line("line-2", "item-2", 200);

            let act = FixedOff(to_amount(0));

            let res = act.action(vec![&l1, &l2]);

            assert!(res.is_none());
        }

        #[test]
        fn fixed_over() {
            let l1 = order_line("line-1", "item-1", 100);
            let l2 = order_line("line-2", "item-2", 200);

            let act = FixedOff(to_amount(400));

            let res = act.action(vec![&l1, &l2]);

            if let Some(DiscountReward::One(v, t)) = res {
                assert_eq!(to_amount(300), v);

                if let RewardTarget::Group(ts) = t {
                    assert_eq!(2, ts.len());
                    assert_eq!("line-1", ts[0].line_id);
                    assert_eq!("line-2", ts[1].line_id);
                } else {
                    assert!(false);
                }
            } else {
                assert!(false);
            }
        }

        #[test]
        fn fixed_negative() {
            let l1 = order_line("line-1", "item-1", 100);
            let l2 = order_line("line-2", "item-2", 200);

            let act = FixedOff(to_amount_signed(-1));

            let res = act.action(vec![&l1, &l2]);

            assert!(res.is_none());
        }

        #[test]
        fn fixed_negative_subtotal() {
            let l1 = order_line("line-1", "item-1", 100);
            let l2 = order_line("line-2", "item-2", -200);

            let act = FixedOff(to_amount(50));

            let res = act.action(vec![&l1, &l2]);

            assert!(res.is_none());
        }

        #[test]
        fn fixed_contains_negative_price() {
            let l1 = order_line("line-1", "item-1", -100);
            let l2 = order_line("line-2", "item-2", 200);

            let act = FixedOff(to_amount(50));

            let res = act.action(vec![&l1, &l2]);

            if let Some(DiscountReward::One(v, t)) = res {
                assert_eq!(to_amount(50), v);

                if let RewardTarget::Group(ts) = t {
                    assert_eq!(2, ts.len());
                } else {
                    assert!(false);
                }
            } else {
                assert!(false);
            }
        }

        #[test]
        fn create_percentage() {
            if let PercentageOff(v) = DiscountAction::percentage(to_amount(10)) {
                assert_eq!(to_amount(10) / to_amount(100), v);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn create_percentage_zero() {
            if let PercentageOff(v) = DiscountAction::percentage(to_amount(0)) {
                assert_eq!(Amount::zero(), v);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn create_percentage_negative() {
            if let PercentageOff(v) = DiscountAction::percentage(to_amount_signed(-10)) {
                assert_eq!(Amount::zero(), v);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn create_percentage_over_100() {
            if let PercentageOff(v) = DiscountAction::percentage(to_amount(101)) {
                assert_eq!(Amount::one(), v);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn percentage_empty() {
            let act = PercentageOff(to_amount(10) / to_amount(100));

            let res = act.action(vec![]);

            assert!(res.is_none());
        }

        #[test]
        fn percentage_single() {
            let l1 = order_line("line-1", "item-1", 100);

            let act = PercentageOff(to_amount(20) / to_amount(100));

            let res = act.action(vec![&l1]);

            if let Some(DiscountReward::One(v, t)) = res {
                assert_eq!(to_amount(20), v);

                if let RewardTarget::Single(t) = t {
                    assert_eq!("line-1", t.line_id);
                } else {
                    assert!(false);
                }
            } else {
                assert!(false);
            }
        }

        #[test]
        fn percentage_under_one() {
            let l1 = order_line("line-1", "item-1", 100);
            let l2 = order_line("line-2", "item-2", 200);

            let act = PercentageOff(to_amount(20) / to_amount(100));

            let res = act.action(vec![&l1, &l2]);

            if let Some(DiscountReward::One(v, t)) = res {
                assert_eq!(to_amount(60), v);

                if let RewardTarget::Group(ts) = t {
                    assert_eq!(2, ts.len());
                    assert_eq!("line-1", ts[0].line_id);
                    assert_eq!("line-2", ts[1].line_id);
                } else {
                    assert!(false);
                }
            } else {
                assert!(false);
            }
        }

        #[test]
        fn percentage_zero() {
            let l1 = order_line("line-1", "item-1", 100);
            let l2 = order_line("line-2", "item-2", 200);

            let act = PercentageOff(to_amount(0));

            let res = act.action(vec![&l1, &l2]);

            assert!(res.is_none());
        }

        #[test]
        fn percentage_one() {
            let l1 = order_line("line-1", "item-1", 100);
            let l2 = order_line("line-2", "item-2", 200);

            let act = PercentageOff(Amount::one());

            let res = act.action(vec![&l1, &l2]);

            if let Some(DiscountReward::One(v, t)) = res {
                assert_eq!(to_amount(300), v);

                if let RewardTarget::Group(ts) = t {
                    assert_eq!(2, ts.len());
                    assert_eq!("line-1", ts[0].line_id);
                    assert_eq!("line-2", ts[1].line_id);
                } else {
                    assert!(false);
                }
            } else {
                assert!(false);
            }
        }

        #[test]
        fn percentage_over_one() {
            let l1 = order_line("line-1", "item-1", 100);
            let l2 = order_line("line-2", "item-2", 200);

            let act = PercentageOff(to_amount(2));

            let res = act.action(vec![&l1, &l2]);

            assert!(res.is_none());
        }

        #[test]
        fn percentage_negative_subtotal() {
            let l1 = order_line("line-1", "item-1", 100);
            let l2 = order_line("line-2", "item-2", -200);

            let act = PercentageOff(to_amount(50) / to_amount(100));

            let res = act.action(vec![&l1, &l2]);

            assert!(res.is_none());
        }

        #[test]
        fn each_fixed() {
            let l1 = order_line("line-1", "item-1", 100);
            let l2 = order_line("line-2", "item-2", 200);

            let act = FixedOff(to_amount(150)).each(0, None);

            let res = act.action(vec![&l1, &l2]);

            if let Some(DiscountReward::Bundle(vs)) = res {
                assert_eq!(2, vs.len());

                let (v1, t1) = &vs[0];
                assert!(v1.is_some());
                assert_eq!(to_amount(100), v1.to_owned().unwrap());

                if let RewardTarget::Single(t) = t1 {
                    assert_eq!("line-1", t.line_id);
                } else {
                    assert!(false);
                }

                let (v2, t2) = &vs[1];
                assert!(v2.is_some());
                assert_eq!(to_amount(150), v2.to_owned().unwrap());

                if let RewardTarget::Single(t) = t2 {
                    assert_eq!("line-2", t.line_id);
                } else {
                    assert!(false);
                }
            } else {
                assert!(false);
            }
        }

        #[test]
        fn each_percentage() {
            let l1 = order_line("line-1", "item-1", 100);
            let l2 = order_line("line-2", "item-2", 200);

            let act = PercentageOff(to_amount(10) / to_amount(100)).each(0, None);

            let res = act.action(vec![&l1, &l2]);

            if let Some(DiscountReward::Bundle(vs)) = res {
                assert_eq!(2, vs.len());

                let (v1, t1) = &vs[0];
                assert!(v1.is_some());
                assert_eq!(to_amount(10), v1.to_owned().unwrap());

                if let RewardTarget::Single(t) = t1 {
                    assert_eq!("line-1", t.line_id);
                } else {
                    assert!(false);
                }

                let (v2, t2) = &vs[1];
                assert!(v2.is_some());
                assert_eq!(to_amount(20), v2.to_owned().unwrap());

                if let RewardTarget::Single(t) = t2 {
                    assert_eq!("line-2", t.line_id);
                } else {
                    assert!(false);
                }
            } else {
                assert!(false);
            }
        }

        #[test]
        fn each_percentage_skip_one() {
            let l1 = order_line("line-1", "item-1", 100);
            let l2 = order_line("line-2", "item-2", 200);

            let act = DiscountAction::percentage(to_amount(10)).each(1, None);

            let res = act.action(vec![&l1, &l2]);

            if let Some(DiscountReward::Bundle(vs)) = res {
                assert_eq!(2, vs.len());

                let (v1, t1) = &vs[0];
                assert!(v1.is_none());

                if let RewardTarget::Single(t) = t1 {
                    assert_eq!("line-1", t.line_id);
                } else {
                    assert!(false);
                }

                let (v2, t2) = &vs[1];
                assert!(v2.is_some());
                assert_eq!(to_amount(20), v2.to_owned().unwrap());

                if let RewardTarget::Single(t) = t2 {
                    assert_eq!("line-2", t.line_id);
                } else {
                    assert!(false);
                }
            } else {
                assert!(false);
            }
        }

        #[test]
        fn each_fixed_take_under() {
            let l1 = order_line("line-1", "item-1", 100);
            let l2 = order_line("line-2", "item-2", 200);
            let l3 = order_line("line-3", "item-3", 300);

            let act = FixedOff(to_amount(30)).each(0, Some(2));

            let res = act.action(vec![&l1, &l2, &l3]);

            if let Some(DiscountReward::Bundle(vs)) = res {
                assert_eq!(3, vs.len());

                let (v1, t1) = &vs[0];
                assert!(v1.is_some());
                assert_eq!(to_amount(30), v1.to_owned().unwrap());

                if let RewardTarget::Single(t) = t1 {
                    assert_eq!("line-1", t.line_id);
                } else {
                    assert!(false);
                }

                let (v2, t2) = &vs[1];
                assert!(v2.is_some());
                assert_eq!(to_amount(30), v2.to_owned().unwrap());

                if let RewardTarget::Single(t) = t2 {
                    assert_eq!("line-2", t.line_id);
                } else {
                    assert!(false);
                }

                let (v3, t3) = &vs[2];
                assert!(v3.is_none());

                if let RewardTarget::Single(t) = t3 {
                    assert_eq!("line-3", t.line_id);
                } else {
                    assert!(false);
                }
            } else {
                assert!(false);
            }
        }

        #[test]
        fn each_fixed_over_skip() {
            let l1 = order_line("line-1", "item-1", 100);
            let l2 = order_line("line-2", "item-2", 200);

            let act = FixedOff(to_amount(150)).each(2, None);

            let res = act.action(vec![&l1, &l2]);

            assert!(res.is_none());
        }

        #[test]
        fn each_fixed_take_zero() {
            let l1 = order_line("line-1", "item-1", 100);
            let l2 = order_line("line-2", "item-2", 200);

            let act = FixedOff(to_amount(150)).each(0, Some(0));

            let res = act.action(vec![&l1, &l2]);

            assert!(res.is_none());
        }

        #[test]
        fn each_percentage_empty() {
            let act = DiscountAction::percentage(to_amount(20)).each(0, None);

            let res = act.action(vec![]);

            assert!(res.is_none());
        }

        #[test]
        fn each_fixed_zero() {
            let l1 = order_line("line-1", "item-1", 100);
            let l2 = order_line("line-2", "item-2", 200);

            let act = FixedOff(to_amount(0)).each(0, None);

            let res = act.action(vec![&l1, &l2]);

            assert!(res.is_none());
        }

        #[test]
        fn each_percentage_zero() {
            let l1 = order_line("line-1", "item-1", 100);
            let l2 = order_line("line-2", "item-2", 200);

            let act = PercentageOff(to_amount(0)).each(0, None);

            let res = act.action(vec![&l1, &l2]);

            assert!(res.is_none());
        }

        #[test]
        fn each_nest() {
            let l1 = order_line("line-1", "item-1", 100);
            let l2 = order_line("line-2", "item-2", 200);

            let act = FixedOff(to_amount(30)).each(0, None).each(0, None);

            let res = act.action(vec![&l1, &l2]);

            assert!(res.is_none());
        }
    }

    mod rule {
        use super::*;

        #[test]
        fn apply() {
            let order = Order {
                order_at: Utc::now(),
                lines: vec![
                    order_line("line-1", "item-1", 100),
                    order_line("line-2", "item-2", 200),
                    order_line("line-3", "item-3", 300),
                    order_line("line-4", "item-4", 400),
                ],
            };

            let rule = DiscountRule {
                select: GroupSelection::Items(ItemRestriction::Item(vec![
                    "item-1".into(),
                    "item-3".into(),
                ])),
                action: DiscountAction::percentage(to_amount(20)),
            };

            let res = rule.apply((&order, vec![]));

            if let Some(DiscountReward::One(v, t)) = res {
                assert_eq!(to_amount(80), v);

                if let RewardTarget::Group(ts) = t {
                    assert_eq!(2, ts.len());
                    assert_eq!("line-1", ts[0].line_id);
                    assert_eq!("line-3", ts[1].line_id);
                } else {
                    assert!(false);
                }
            } else {
                assert!(false);
            }
        }

        #[test]
        fn apply_none_select() {
            let order = Order {
                order_at: Utc::now(),
                lines: vec![
                    order_line("line-1", "item-1", 100),
                    order_line("line-2", "item-2", 200),
                    order_line("line-3", "item-3", 300),
                    order_line("line-4", "item-4", 400),
                ],
            };

            let rule = DiscountRule {
                select: GroupSelection::Items(ItemRestriction::Item(vec!["item-5".into()])),
                action: DiscountAction::percentage(to_amount(20)),
            };

            let res = rule.apply((&order, vec![]));

            assert!(res.is_none());
        }

        #[test]
        fn apply_empty_lines() {
            let order = Order {
                order_at: Utc::now(),
                lines: vec![],
            };

            let rule = DiscountRule {
                select: GroupSelection::Items(ItemRestriction::Item(vec!["item-1".into()])),
                action: DiscountAction::percentage(to_amount(20)),
            };

            let res = rule.apply((&order, vec![]));

            assert!(res.is_none());
        }
    }

    mod reward {
        use super::DiscountReward::*;
        use super::*;

        #[test]
        fn one_single() {
            let r = One(to_amount(10), RewardTarget::Single("item-1"));
            assert_eq!(vec!["item-1"], r.targets());
        }

        #[test]
        fn one_group() {
            let r = One(
                to_amount(10),
                RewardTarget::Group(vec!["item-1", "item-2", "item-3"]),
            );
            assert_eq!(vec!["item-1", "item-2", "item-3"], r.targets());
        }

        #[test]
        fn bundle() {
            let r = Bundle(vec![
                (None, RewardTarget::Single("item-1")),
                (None, RewardTarget::Group(vec!["item-2", "item-3"])),
                (Some(to_amount(10)), RewardTarget::Single("item-4")),
                (Some(to_amount(10)), RewardTarget::Single("item-2")),
            ]);
            assert_eq!(
                vec!["item-1", "item-2", "item-3", "item-4", "item-2"],
                r.targets()
            );
        }

        #[test]
        fn bundle_empty() {
            let r = Bundle::<&str>(vec![]);
            assert!(r.targets().is_empty());
        }
    }
}
