#![allow(dead_code)]

use num::BigRational;

pub type OrderId = String;
pub type LineNo = usize;
pub type ItemId = String;
pub type Amount = BigRational;

#[derive(Debug, Clone)]
pub struct Order {
    id: OrderId,
    lines: Vec<OrderLine>,
}

#[derive(Debug, Clone)]
pub struct OrderLine {
    line_no: LineNo,
    item_id: ItemId,
    price: Amount,
}

pub trait Predicate<T> {
    fn eval(&self, target: T) -> bool;
}

#[derive(Debug, Clone)]
pub enum Condition {
    ItemId(Vec<ItemId>),
    Not(Box<Condition>),
}

impl Predicate<&OrderLine> for Condition {
    fn eval(&self, target: &OrderLine) -> bool {
        match self {
            Self::ItemId(ids) => ids.contains(&target.item_id),
            Self::Not(c) => !c.eval(target),
        }
    }
}

#[derive(Debug, Clone)]
pub enum Reward {
    Discount(Amount),
}

#[derive(Debug, Clone)]
pub enum DiscountMethod {
    Value(Amount),
    Rate(Amount),
}

#[derive(Debug, Clone)]
pub struct DiscountAction {
    method: DiscountMethod,
    condition: Option<Condition>,
    min_num: Option<usize>,
    skip: Option<usize>,
}

type AppError = Box<dyn std::error::Error>;
type Result<T> = std::result::Result<T, AppError>;
type RewardResult<R, T> = Result<Vec<(R, T)>>;

impl DiscountAction {
    pub fn new(method: DiscountMethod, condition: Option<Condition>) -> Self {
        Self {
            method,
            condition,
            min_num: None,
            skip: None,
        }
    }

    pub fn new_with_opts(
        method: DiscountMethod,
        condition: Option<Condition>,
        min_num: Option<usize>,
        skip: Option<usize>,
    ) -> Self {
        Self {
            method,
            condition,
            min_num,
            skip,
        }
    }

    pub fn action(&self, data: &Order) -> RewardResult<Reward, LineNo> {
        let method = &self.method;
        let cond = &self.condition;

        let res = data
            .lines
            .iter()
            .filter(|&x| if let Some(c) = cond { c.eval(x) } else { true })
            .map(|x| {
                let r = match method {
                    DiscountMethod::Value(a) => a.min(&x.price).clone(),
                    DiscountMethod::Rate(a) => a * &x.price,
                };

                (Reward::Discount(r), x.line_no)
            })
            .collect::<Vec<_>>();

        if let Some(n) = self.min_num {
            if res.len() < n {
                return Ok(vec![]);
            }
        }

        Ok(res
            .into_iter()
            .skip(self.skip.unwrap_or(0))
            .collect::<Vec<_>>())
    }
}

impl DiscountMethod {
    pub fn to_value(v: usize) -> Result<Self> {
        if v > 0 {
            Ok(Self::Value(Amount::from_integer(v.into())))
        } else {
            Err("invalid value".into())
        }
    }

    pub fn to_rate(v: u8) -> Result<Self> {
        if v > 0 && v <= 100 {
            Ok(Self::Rate(Amount::new(v.into(), 100.into())))
        } else {
            Err("invalid value".into())
        }
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    fn from_u(v: usize) -> Amount {
        Amount::from_integer(v.into())
    }

    fn from_f(v: f32) -> Amount {
        Amount::from_float(v).unwrap()
    }

    mod condition {
        use super::*;

        #[test]
        fn item_id_include() {
            let c = Condition::ItemId(vec!["item-1".into(), "item-2".into()]);

            assert!(c.eval(&OrderLine {
                line_no: 1,
                item_id: "item-1".into(),
                price: from_u(100)
            }));

            assert!(c.eval(&OrderLine {
                line_no: 1,
                item_id: "item-2".into(),
                price: from_u(100)
            }));
        }

        #[test]
        fn item_id_exclude() {
            let c = Condition::ItemId(vec!["item-1".into(), "item-2".into()]);

            assert_eq!(
                false,
                c.eval(&OrderLine {
                    line_no: 1,
                    item_id: "item-3".into(),
                    price: from_u(100),
                })
            );
        }

        #[test]
        fn not() {
            let c = Condition::Not(Box::new(Condition::ItemId(vec!["item-1".into()])));

            assert_eq!(
                false,
                c.eval(&OrderLine {
                    line_no: 1,
                    item_id: "item-1".into(),
                    price: from_u(100)
                })
            );

            assert!(c.eval(&OrderLine {
                line_no: 1,
                item_id: "item-2".into(),
                price: from_u(100)
            }));
        }
    }

    mod discount {
        use super::*;

        #[test]
        fn to_value_100() {
            let r = DiscountMethod::to_value(100).unwrap();

            if let DiscountMethod::Value(v) = r {
                assert_eq!(from_u(100), v);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn to_value_zero() {
            let r = DiscountMethod::to_value(0);

            assert!(r.is_err());
        }

        #[test]
        fn to_rate_half() {
            let r = DiscountMethod::to_rate(50).unwrap();

            if let DiscountMethod::Rate(v) = r {
                assert_eq!(from_f(0.5), v);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn to_rate_full() {
            let r = DiscountMethod::to_rate(100).unwrap();

            if let DiscountMethod::Rate(v) = r {
                assert_eq!(from_u(1), v);
            } else {
                assert!(false);
            }
        }

        #[test]
        fn to_rate_zero() {
            let r = DiscountMethod::to_rate(0);

            assert!(r.is_err());
        }

        #[test]
        fn to_rate_over100() {
            let r = DiscountMethod::to_rate(101);

            assert!(r.is_err());
        }

        #[test]
        fn all_100_off() {
            let p = DiscountAction::new(DiscountMethod::to_value(100).unwrap(), None);

            let d = Order {
                id: "order-1".into(),
                lines: vec![
                    OrderLine {
                        line_no: 1,
                        item_id: "item-1".into(),
                        price: Amount::from_integer(200.into()),
                    },
                    OrderLine {
                        line_no: 2,
                        item_id: "item-2".into(),
                        price: Amount::from_integer(300.into()),
                    },
                ],
            };

            let r = p.action(&d).unwrap();

            assert_eq!(2, r.len());

            let (Reward::Discount(r1), n1) = r.get(0).unwrap();
            assert_eq!(from_u(100), *r1);
            assert_eq!(1, *n1);

            let (Reward::Discount(r2), n2) = r.get(1).unwrap();
            assert_eq!(from_u(100), *r2);
            assert_eq!(2, *n2);
        }

        #[test]
        fn all_100_off_with_over_price() {
            let p = DiscountAction::new(DiscountMethod::to_value(100).unwrap(), None);

            let d = Order {
                id: "order-1".into(),
                lines: vec![
                    OrderLine {
                        line_no: 1,
                        item_id: "item-1".into(),
                        price: Amount::from_integer(80.into()),
                    },
                    OrderLine {
                        line_no: 2,
                        item_id: "item-2".into(),
                        price: Amount::from_integer(150.into()),
                    },
                ],
            };

            let r = p.action(&d).unwrap();

            let (Reward::Discount(r1), _) = r.get(0).unwrap();
            assert_eq!(from_u(80), *r1);
        }

        #[test]
        fn all_10percent_off() {
            let p = DiscountAction::new(DiscountMethod::to_rate(10).unwrap(), None);

            let d = Order {
                id: "order-1".into(),
                lines: vec![
                    OrderLine {
                        line_no: 1,
                        item_id: "item-1".into(),
                        price: Amount::from_integer(200.into()),
                    },
                    OrderLine {
                        line_no: 2,
                        item_id: "item-2".into(),
                        price: Amount::from_integer(300.into()),
                    },
                ],
            };

            let r = p.action(&d).unwrap();

            assert_eq!(2, r.len());

            let (Reward::Discount(r1), _) = r.get(0).unwrap();
            assert_eq!(from_u(20), *r1);

            let (Reward::Discount(r2), _) = r.get(1).unwrap();
            assert_eq!(from_u(30), *r2);
        }

        #[test]
        fn specific_items_20percent_off() {
            let c = Condition::ItemId(vec!["item-3".into(), "item-2".into()]);
            let p = DiscountAction::new(DiscountMethod::to_rate(20).unwrap(), Some(c));

            let d = Order {
                id: "order-1".into(),
                lines: vec![
                    OrderLine {
                        line_no: 1,
                        item_id: "item-1".into(),
                        price: Amount::from_integer(100.into()),
                    },
                    OrderLine {
                        line_no: 2,
                        item_id: "item-2".into(),
                        price: Amount::from_integer(200.into()),
                    },
                    OrderLine {
                        line_no: 3,
                        item_id: "item-3".into(),
                        price: Amount::from_integer(300.into()),
                    },
                    OrderLine {
                        line_no: 4,
                        item_id: "item-4".into(),
                        price: Amount::from_integer(400.into()),
                    },
                ],
            };

            let r = p.action(&d).unwrap();

            assert_eq!(2, r.len());

            let (Reward::Discount(r1), n1) = r.get(0).unwrap();
            assert_eq!(from_u(40), *r1);
            assert_eq!(2, *n1);

            let (Reward::Discount(r2), n2) = r.get(1).unwrap();
            assert_eq!(from_u(60), *r2);
            assert_eq!(3, *n2);
        }

        #[test]
        fn specific_2more_items_20percent_off() {
            let c = Condition::ItemId(vec!["item-3".into(), "item-2".into()]);
            let p = DiscountAction::new_with_opts(
                DiscountMethod::to_rate(20).unwrap(),
                Some(c),
                Some(2),
                None,
            );

            let d = Order {
                id: "order-1".into(),
                lines: vec![
                    OrderLine {
                        line_no: 1,
                        item_id: "item-1".into(),
                        price: Amount::from_integer(100.into()),
                    },
                    OrderLine {
                        line_no: 2,
                        item_id: "item-2".into(),
                        price: Amount::from_integer(200.into()),
                    },
                    OrderLine {
                        line_no: 3,
                        item_id: "item-3".into(),
                        price: Amount::from_integer(300.into()),
                    },
                    OrderLine {
                        line_no: 4,
                        item_id: "item-4".into(),
                        price: Amount::from_integer(400.into()),
                    },
                    OrderLine {
                        line_no: 5,
                        item_id: "item-3".into(),
                        price: Amount::from_integer(300.into()),
                    },
                ],
            };

            let r = p.action(&d).unwrap();

            assert_eq!(3, r.len());

            let (Reward::Discount(r1), n1) = r.get(0).unwrap();
            assert_eq!(from_u(40), *r1);
            assert_eq!(2, *n1);

            let (Reward::Discount(r2), n2) = r.get(1).unwrap();
            assert_eq!(from_u(60), *r2);
            assert_eq!(3, *n2);

            let (Reward::Discount(r3), n3) = r.get(2).unwrap();
            assert_eq!(from_u(60), *r3);
            assert_eq!(5, *n3);
        }

        #[test]
        fn specific_2more_items_20percent_off_with_single_item() {
            let c = Condition::ItemId(vec!["item-3".into(), "item-2".into()]);
            let p = DiscountAction::new_with_opts(
                DiscountMethod::to_rate(20).unwrap(),
                Some(c),
                Some(2),
                None,
            );

            let d = Order {
                id: "order-1".into(),
                lines: vec![
                    OrderLine {
                        line_no: 1,
                        item_id: "item-1".into(),
                        price: Amount::from_integer(100.into()),
                    },
                    OrderLine {
                        line_no: 2,
                        item_id: "item-2".into(),
                        price: Amount::from_integer(200.into()),
                    },
                    OrderLine {
                        line_no: 3,
                        item_id: "item-4".into(),
                        price: Amount::from_integer(300.into()),
                    },
                ],
            };

            let r = p.action(&d).unwrap();

            assert_eq!(0, r.len());
        }

        #[test]
        fn specific_2more_1skip_items_20percent_off() {
            let c = Condition::ItemId(vec!["item-3".into(), "item-2".into()]);
            let p = DiscountAction::new_with_opts(
                DiscountMethod::to_rate(20).unwrap(),
                Some(c),
                Some(2),
                Some(1),
            );

            let d = Order {
                id: "order-1".into(),
                lines: vec![
                    OrderLine {
                        line_no: 1,
                        item_id: "item-1".into(),
                        price: Amount::from_integer(100.into()),
                    },
                    OrderLine {
                        line_no: 2,
                        item_id: "item-2".into(),
                        price: Amount::from_integer(200.into()),
                    },
                    OrderLine {
                        line_no: 3,
                        item_id: "item-3".into(),
                        price: Amount::from_integer(300.into()),
                    },
                    OrderLine {
                        line_no: 4,
                        item_id: "item-4".into(),
                        price: Amount::from_integer(400.into()),
                    },
                    OrderLine {
                        line_no: 5,
                        item_id: "item-3".into(),
                        price: Amount::from_integer(300.into()),
                    },
                ],
            };

            let r = p.action(&d).unwrap();

            assert_eq!(2, r.len());

            let (Reward::Discount(r1), n1) = r.get(0).unwrap();
            assert_eq!(from_u(60), *r1);
            assert_eq!(3, *n1);

            let (Reward::Discount(r2), n2) = r.get(1).unwrap();
            assert_eq!(from_u(60), *r2);
            assert_eq!(5, *n2);
        }

        #[test]
        fn specific_2more_allskip_items_20percent_off() {
            let c = Condition::ItemId(vec!["item-3".into(), "item-2".into()]);
            let p = DiscountAction::new_with_opts(
                DiscountMethod::to_rate(20).unwrap(),
                Some(c),
                Some(2),
                Some(3),
            );

            let d = Order {
                id: "order-1".into(),
                lines: vec![
                    OrderLine {
                        line_no: 1,
                        item_id: "item-1".into(),
                        price: Amount::from_integer(100.into()),
                    },
                    OrderLine {
                        line_no: 2,
                        item_id: "item-2".into(),
                        price: Amount::from_integer(200.into()),
                    },
                    OrderLine {
                        line_no: 3,
                        item_id: "item-3".into(),
                        price: Amount::from_integer(300.into()),
                    },
                    OrderLine {
                        line_no: 4,
                        item_id: "item-4".into(),
                        price: Amount::from_integer(400.into()),
                    },
                    OrderLine {
                        line_no: 5,
                        item_id: "item-3".into(),
                        price: Amount::from_integer(300.into()),
                    },
                ],
            };

            let r = p.action(&d).unwrap();

            assert_eq!(0, r.len());
        }
    }
}
