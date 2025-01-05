use super::*;

pub type DiscountPriceReward<T> = (Amount, RewardTarget<T>);

#[derive(Debug, Clone, PartialEq)]
pub struct DiscountPriceRule {
    select: GroupSelection,
    price: Amount,
}

impl DiscountPriceRule {
    pub fn new(select: GroupSelection, price: Amount) -> Self {
        Self { select, price }
    }

    pub fn apply<'a>(
        &self,
        target: PromotionInput<'a>,
    ) -> Option<DiscountPriceReward<&'a OrderLine>> {
        self.select.select_by(target).and_then(|x| {
            let total = subtotal_lines(&x);

            if self.price >= Amount::zero() && self.price < total {
                let t = if x.len() == 1 {
                    RewardTarget::Single(x[0])
                } else {
                    RewardTarget::Group(x.clone())
                };

                Some((self.price.clone(), t))
            } else {
                None
            }
        })
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use std::{collections::HashMap, vec};

    fn order_line(line_id: &str, item_id: &str, price: i32) -> OrderLine {
        OrderLine {
            line_id: line_id.into(),
            item_id: item_id.into(),
            price: to_amount_signed(price),
            attrs: HashMap::new(),
        }
    }

    fn to_amount_signed(v: i32) -> Amount {
        Amount::from_integer(v.into())
    }

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

        let r = DiscountPriceRule::new(
            GroupSelection::Items(ItemRestriction::Item(vec![
                "item-1".into(),
                "item-3".into(),
            ])),
            to_amount_signed(250),
        );

        let res = r.apply((&order, vec![]));

        if let Some((p, t)) = res {
            assert_eq!(to_amount_signed(250), p);

            if let RewardTarget::Group(ts) = t {
                assert_eq!(2, ts.len());
                assert_eq!("item-1", ts[0].item_id);
                assert_eq!("item-3", ts[1].item_id);
            } else {
                assert!(false);
            }
        } else {
            assert!(false);
        }
    }

    #[test]
    fn apply_with_exclude() {
        let order = Order {
            order_at: Utc::now(),
            lines: vec![
                order_line("line-1", "item-1", 100),
                order_line("line-2", "item-2", 200),
                order_line("line-3", "item-3", 300),
                order_line("line-4", "item-4", 400),
                order_line("line-5", "item-1", 100),
            ],
        };

        let r = DiscountPriceRule::new(
            GroupSelection::Items(ItemRestriction::Item(vec![
                "item-1".into(),
                "item-3".into(),
            ])),
            to_amount_signed(250),
        );

        let excludes = vec![(
            PromotionTarget::Id("p1".into()),
            vec![Reward::Discount(DiscountReward::One(
                to_amount_signed(50),
                RewardTarget::Single(&order.lines[0]),
            ))],
        )];
        let res = r.apply((&order, excludes));

        if let Some((p, t)) = res {
            assert_eq!(to_amount_signed(250), p);

            if let RewardTarget::Group(ts) = t {
                assert_eq!(2, ts.len());
                assert_eq!("line-3", ts[0].line_id);
                assert_eq!("line-5", ts[1].line_id);
            } else {
                assert!(false);
            }
        } else {
            assert!(false);
        }
    }

    #[test]
    fn apply_single() {
        let order = Order {
            order_at: Utc::now(),
            lines: vec![
                order_line("line-1", "item-1", 100),
                order_line("line-2", "item-2", 200),
                order_line("line-3", "item-3", 300),
                order_line("line-4", "item-4", 400),
            ],
        };

        let r = DiscountPriceRule::new(
            GroupSelection::Items(ItemRestriction::Item(vec!["item-1".into()])),
            to_amount_signed(80),
        );

        let res = r.apply((&order, vec![]));

        if let Some((p, t)) = res {
            assert_eq!(to_amount_signed(80), p);

            if let RewardTarget::Single(t) = t {
                assert_eq!("item-1", t.item_id);
            } else {
                assert!(false);
            }
        } else {
            assert!(false);
        }
    }

    #[test]
    fn apply_same_price() {
        let order = Order {
            order_at: Utc::now(),
            lines: vec![
                order_line("line-1", "item-1", 100),
                order_line("line-2", "item-2", 200),
                order_line("line-3", "item-3", 300),
                order_line("line-4", "item-4", 400),
            ],
        };

        let r = DiscountPriceRule::new(
            GroupSelection::Items(ItemRestriction::Item(vec![
                "item-1".into(),
                "item-3".into(),
            ])),
            to_amount_signed(400),
        );

        let res = r.apply((&order, vec![]));

        assert!(res.is_none());
    }

    #[test]
    fn apply_over_price() {
        let order = Order {
            order_at: Utc::now(),
            lines: vec![
                order_line("line-1", "item-1", 100),
                order_line("line-2", "item-2", 200),
                order_line("line-3", "item-3", 300),
                order_line("line-4", "item-4", 400),
            ],
        };

        let r = DiscountPriceRule::new(
            GroupSelection::Items(ItemRestriction::Item(vec![
                "item-1".into(),
                "item-3".into(),
            ])),
            to_amount_signed(450),
        );

        let res = r.apply((&order, vec![]));

        assert!(res.is_none());
    }

    #[test]
    fn apply_negative_price() {
        let order = Order {
            order_at: Utc::now(),
            lines: vec![
                order_line("line-1", "item-1", 100),
                order_line("line-2", "item-2", 200),
                order_line("line-3", "item-3", 300),
                order_line("line-4", "item-4", 400),
            ],
        };

        let r = DiscountPriceRule::new(
            GroupSelection::Items(ItemRestriction::Item(vec![
                "item-1".into(),
                "item-3".into(),
            ])),
            to_amount_signed(-100),
        );

        let res = r.apply((&order, vec![]));

        assert!(res.is_none());
    }

    #[test]
    fn apply_zero_price() {
        let order = Order {
            order_at: Utc::now(),
            lines: vec![
                order_line("line-1", "item-1", 100),
                order_line("line-2", "item-2", 200),
                order_line("line-3", "item-3", 300),
                order_line("line-4", "item-4", 400),
            ],
        };

        let r = DiscountPriceRule::new(
            GroupSelection::Items(ItemRestriction::Item(vec![
                "item-1".into(),
                "item-3".into(),
            ])),
            Amount::zero(),
        );

        let res = r.apply((&order, vec![]));

        if let Some((p, _t)) = res {
            assert_eq!(Amount::zero(), p);
        } else {
            assert!(false);
        }
    }
}
