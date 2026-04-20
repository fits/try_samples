#![allow(dead_code)]

use chrono::prelude::*;
use rust_decimal::prelude::*;
use std::fmt::Debug;

pub type Quantity = usize;
pub type Amount = Decimal;
pub type Date = DateTime<Local>;

pub trait ConstraintType<T>
where
    Self: Sized,
    T: Debug + Clone + PartialEq,
{
    fn new(value: T) -> Option<Self>;
    fn value(&self) -> &T;
}

#[derive(Debug, Clone, PartialEq)]
pub struct NotBlankString {
    value: String,
}

impl NotBlankString {
    pub fn from_str(value: &str) -> Option<Self> {
        Self::new(value.into())
    }
}

impl ConstraintType<String> for NotBlankString {
    fn new(value: String) -> Option<Self> {
        let value = value.trim();

        if value.is_empty() {
            None
        } else {
            Some(Self {
                value: value.into(),
            })
        }
    }

    fn value(&self) -> &String {
        &self.value
    }
}

#[derive(Debug, Clone, PartialEq)]
pub struct OneOrMore {
    value: Quantity,
}

impl OneOrMore {
    pub fn one() -> Self {
        Self::new(1).unwrap()
    }
}

impl ConstraintType<Quantity> for OneOrMore {
    fn new(value: Quantity) -> Option<Self> {
        if value >= 1 {
            return Some(Self { value });
        }
        None
    }

    fn value(&self) -> &Quantity {
        &self.value
    }
}

#[derive(Debug, Clone, PartialEq)]
pub struct PositiveAmount {
    value: Amount,
}

impl ConstraintType<Amount> for PositiveAmount {
    fn new(value: Amount) -> Option<Self> {
        if value.is_sign_positive() {
            Some(Self { value })
        } else {
            None
        }
    }

    fn value(&self) -> &Amount {
        &self.value
    }
}

impl TryFrom<Amount> for PositiveAmount {
    type Error = String;

    fn try_from(value: Amount) -> std::result::Result<Self, Self::Error> {
        Self::new(value).ok_or(format!("{value} is not positive"))
    }
}

pub trait EventSource {
    fn at(&self) -> &Date;
    fn by(&self) -> &Option<Who>;
}

pub type ItemId = NotBlankString;
pub type UserId = NotBlankString;

#[derive(Debug, Clone, PartialEq)]
pub enum Who {
    Anonymous,
    User(UserId),
    System(Option<NotBlankString>),
}

#[derive(Debug, Clone, PartialEq)]
pub enum Order {
    Started {
        at: Date,
        by: Option<Who>,
    },
    Cancelled {
        at: Date,
        by: Option<Who>,
        target: Box<Self>,
    },
    Ordered {
        at: Date,
        by: Option<Who>,
        target: Box<Self>,
        lines: Vec<OrderLine>,
    },
}

#[derive(Debug, Clone, PartialEq)]
pub enum OrderLine {
    OrderedItem {
        at: Date,
        by: Option<Who>,
        item: ItemId,
        qty: OneOrMore,
        unit_price: PositiveAmount,
    },
    Cancelled {
        at: Date,
        by: Option<Who>,
        target: Box<Self>,
    },
}

pub type Result<T> = std::result::Result<T, OrderError>;

fn now() -> Date {
    Local::now()
}

impl Order {
    pub fn start(by: Option<Who>) -> Self {
        Self::Started { at: now(), by }
    }

    pub fn cancel(&self, by: Option<Who>) -> Result<Self> {
        match self {
            Self::Cancelled { .. } => Self::already_cancelled("order"),
            _ => Ok(Self::Cancelled {
                at: now(),
                by,
                target: Box::new(self.clone()),
            }),
        }
    }

    pub fn order_item(
        &self,
        item: ItemId,
        qty: OneOrMore,
        unit_price: PositiveAmount,
        by: Option<Who>,
    ) -> Result<Self> {
        match self {
            Self::Started { .. } => {
                let at = now();
                let line = OrderLine::OrderedItem {
                    at: at.clone(),
                    by: by.clone(),
                    item,
                    qty,
                    unit_price,
                };

                Ok(Self::Ordered {
                    at,
                    by,
                    target: Box::new(self.clone()),
                    lines: vec![line],
                })
            }
            Self::Ordered {
                at,
                by: by_s,
                target,
                lines,
            } => {
                let mut lines = lines.clone();

                lines.push(OrderLine::OrderedItem {
                    at: now(),
                    by,
                    item,
                    qty,
                    unit_price,
                });

                Ok(Self::Ordered {
                    at: at.clone(),
                    by: by_s.clone(),
                    target: target.clone(),
                    lines,
                })
            }
            Self::Cancelled { .. } => Self::invalid_operation("cancelled"),
        }
    }

    pub fn cancel_ordered_item(&self, index: usize, by: Option<Who>) -> Result<Self> {
        match self {
            Order::Started { .. } => Self::invalid_operation("started order has not ordered item"),
            Order::Cancelled { .. } => Self::invalid_operation("cancelled order"),
            Order::Ordered {
                at,
                by: by_s,
                target,
                lines,
            } => {
                let line = lines.get(index);

                if let Some(line) = line {
                    match line {
                        OrderLine::OrderedItem { .. } => {
                            let mut new_lines = lines.clone();

                            new_lines[index] = OrderLine::Cancelled {
                                at: now(),
                                by,
                                target: Box::new(line.clone()),
                            };

                            Ok(Order::Ordered {
                                at: at.clone(),
                                by: by_s.clone(),
                                target: target.clone(),
                                lines: new_lines,
                            })
                        }
                        OrderLine::Cancelled { .. } => {
                            Self::already_cancelled(&format!("line index={index}"))
                        }
                    }
                } else {
                    Self::not_found(&format!("line index={index}"))
                }
            }
        }
    }

    fn invalid_operation(msg: &str) -> Result<Self> {
        Err(OrderError::InvalidOperation(Some(msg.into())))
    }

    fn already_cancelled(msg: &str) -> Result<Self> {
        Err(OrderError::AlreadyCancelled(Some(msg.into())))
    }

    fn not_found(msg: &str) -> Result<Self> {
        Err(OrderError::NotFoundLine(Some(msg.into())))
    }
}

impl EventSource for Order {
    fn at(&self) -> &Date {
        match self {
            Self::Started { at, .. } | Self::Cancelled { at, .. } | Self::Ordered { at, .. } => at,
        }
    }

    fn by(&self) -> &Option<Who> {
        match self {
            Self::Started { by, .. } | Self::Cancelled { by, .. } | Self::Ordered { by, .. } => by,
        }
    }
}

#[derive(Debug, Clone, PartialEq)]
pub enum OrderError {
    InvalidOperation(Option<String>),
    AlreadyCancelled(Option<String>),
    NotFoundLine(Option<String>),
}

#[cfg(test)]
mod tests {
    use super::*;

    fn to_positive(v: usize) -> PositiveAmount {
        Amount::from_usize(v).unwrap().try_into().unwrap()
    }

    #[test]
    fn new_notblankid() {
        let r = NotBlankString::new("id1".into());

        assert!(r.is_some());
        assert_eq!("id1", r.unwrap().value);
    }

    #[test]
    fn new_notblankid_with_blank() {
        let r = NotBlankString::new("  ".into());

        assert!(r.is_none());
    }

    #[test]
    fn new_one_or_more() {
        let r = OneOrMore::new(1);

        assert!(r.is_some());
        assert_eq!(1, r.unwrap().value);
    }

    #[test]
    fn new_one_or_more_with_two() {
        let r = OneOrMore::new(2);

        assert!(r.is_some());
        assert_eq!(2, r.unwrap().value);
    }

    #[test]
    fn new_one_or_more_with_zero() {
        let r = OneOrMore::new(0);

        assert!(r.is_none());
    }

    #[test]
    fn new_positiveamount() {
        let r = PositiveAmount::new(Amount::ONE);

        assert!(r.is_some());
        assert_eq!(Amount::ONE, *r.unwrap().value());
    }

    #[test]
    fn new_positiveamount_with_zero() {
        let r = PositiveAmount::new(Amount::ZERO);

        assert!(r.is_some());
        assert_eq!(Amount::ZERO, *r.unwrap().value());
    }

    #[test]
    fn new_positiveamount_with_negative() {
        let r = PositiveAmount::new(Amount::from_isize(-12).unwrap());

        assert!(r.is_none());
    }

    #[test]
    fn start() {
        let r = Order::start(None);

        if let Order::Started { at, by } = r.clone() {
            assert_eq!(at, *r.at());
            assert!(by.is_none());
        } else {
            assert!(false, "not Started");
        }
    }

    #[test]
    fn start_by_system() {
        let by = Some(Who::System(NotBlankString::from_str("test1")));
        let r = Order::start(by.clone());

        if let Order::Started { by, .. } = r.clone() {
            assert!(by.is_some());

            if let Who::System(s) = by.unwrap() {
                assert!(s.is_some());
                assert_eq!("test1", s.unwrap().value);
            }
        } else {
            assert!(false, "not Started");
        }
    }

    #[test]
    fn cancel_from_started() {
        let s = Order::start(None);

        let r = s.cancel(Some(Who::Anonymous));

        if let Ok(Order::Cancelled { at, by, target }) = r {
            assert_eq!(Some(Who::Anonymous), by);
            assert_eq!(s, *target);
            assert!(at >= *s.at());
        } else {
            assert!(false, "failed to cancel");
        }
    }

    #[test]
    fn cancel_from_cancelled() {
        if let Ok(s) = Order::start(None).cancel(None) {
            let r = s.cancel(Some(Who::Anonymous));

            assert!(r.is_err(), "multi cancelled");
        } else {
            assert!(false, "failed to cancel from started");
        }
    }

    #[test]
    fn order_item_to_started() {
        let s = Order::start(None);

        let item = NotBlankString::from_str("item-1").unwrap();
        let qty = OneOrMore::one();
        let unit_price = to_positive(1100);
        let by = Some(Who::User(NotBlankString::from_str("u1").unwrap()));

        let r = s.order_item(item, qty, unit_price, by.clone());

        if let Ok(Order::Ordered {
            at: at_1,
            by: by_1,
            target,
            lines,
        }) = r
        {
            assert!(at_1 >= *s.at());
            assert_eq!(by, by_1);
            assert_eq!(s, *target);

            assert_eq!(1, lines.len());

            if let Some(OrderLine::OrderedItem {
                at: at_2,
                by: by_2,
                item: i,
                qty: q,
                unit_price: p,
            }) = lines.first()
            {
                assert_eq!(at_1, *at_2);
                assert_eq!(by, *by_2);
                assert_eq!("item-1", i.value);
                assert_eq!(1, q.value);
                assert_eq!(1100, p.value().to_usize().unwrap());
            } else {
                assert!(false, "not found ordered-item");
            }
        } else {
            assert!(false, "failed to order item")
        }
    }

    #[test]
    fn order_item_to_cancelled() {
        let s = Order::start(None).cancel(None).unwrap();

        let item = NotBlankString::from_str("item1").unwrap();
        let qty = OneOrMore::one();
        let unit_price = to_positive(1100);
        let by = Some(Who::Anonymous);

        let r = s.order_item(item, qty, unit_price, by);

        assert!(r.is_err(), "ordered item to cancelled");
    }

    #[test]
    fn order_item_to_ordered() {
        let s1 = Order::start(None);
        let at = Local::now();

        let s2 = Order::Ordered {
            at: at.clone(),
            by: None,
            target: Box::new(s1.clone()),
            lines: vec![OrderLine::OrderedItem {
                at: at.clone(),
                by: None,
                item: NotBlankString::from_str("item-1").unwrap(),
                qty: OneOrMore::one(),
                unit_price: to_positive(550),
            }],
        };

        let item = NotBlankString::from_str("item-2").unwrap();
        let qty = OneOrMore::new(3).unwrap();
        let unit_price = to_positive(220);
        let by = Some(Who::Anonymous);

        let r = s2.order_item(item, qty, unit_price, by);

        if let Ok(Order::Ordered {
            at: at_1,
            by: by_1,
            target,
            lines,
        }) = r
        {
            assert_eq!(at, at_1);
            assert_eq!(None, by_1);
            assert_eq!(s1, *target);

            assert_eq!(2, lines.len());

            if let Some(OrderLine::OrderedItem {
                at: at_2,
                by: by_2,
                item: i,
                qty: q,
                unit_price: p,
            }) = lines.last()
            {
                assert!(*at_2 >= at_1);
                assert_eq!(Some(Who::Anonymous), *by_2);
                assert_eq!("item-2", i.value);
                assert_eq!(3, q.value);
                assert_eq!(220, p.value().to_usize().unwrap());
            } else {
                assert!(false, "not found ordered-item");
            }
        } else {
            assert!(false, "failed to append order")
        }
    }

    #[test]
    fn order_item_to_ordered_with_same_item() {
        let s1 = Order::start(None);
        let at = Local::now();

        let s2 = Order::Ordered {
            at: at.clone(),
            by: None,
            target: Box::new(s1.clone()),
            lines: vec![OrderLine::OrderedItem {
                at: at.clone(),
                by: None,
                item: NotBlankString::from_str("item-1").unwrap(),
                qty: OneOrMore::one(),
                unit_price: to_positive(550),
            }],
        };

        let item = NotBlankString::from_str("item-1").unwrap();
        let qty = OneOrMore::new(3).unwrap();
        let unit_price = to_positive(440);

        let r = s2.order_item(item, qty, unit_price, Some(Who::Anonymous));

        if let Ok(Order::Ordered {
            at: at_1,
            by: by_1,
            target,
            lines,
        }) = r
        {
            assert_eq!(at, at_1);
            assert_eq!(None, by_1);
            assert_eq!(s1, *target);

            assert_eq!(2, lines.len());

            if let Some(OrderLine::OrderedItem {
                at: at_2,
                by: by_2,
                item: i,
                qty: q,
                unit_price: p,
            }) = lines.last()
            {
                assert!(*at_2 >= at_1);
                assert_eq!(Some(Who::Anonymous), *by_2);
                assert_eq!("item-1", i.value);
                assert_eq!(3, q.value);
                assert_eq!(440, p.value().to_usize().unwrap());
            } else {
                assert!(false, "not found ordered-item");
            }
        } else {
            assert!(false, "failed to order same item")
        }
    }

    #[test]
    fn cancel_single_ordered_item() {
        let s1 = Order::start(None);
        let at = Local::now();

        let line = OrderLine::OrderedItem {
            at: at.clone(),
            by: None,
            item: NotBlankString::from_str("item-1").unwrap(),
            qty: OneOrMore::one(),
            unit_price: to_positive(550),
        };

        let s2 = Order::Ordered {
            at: at.clone(),
            by: None,
            target: Box::new(s1.clone()),
            lines: vec![line.clone()],
        };

        let r = s2.cancel_ordered_item(0, Some(Who::Anonymous));

        if let Ok(Order::Ordered {
            at: at_1,
            by: by_1,
            target: t_1,
            lines,
        }) = r
        {
            assert_eq!(at, at_1);
            assert_eq!(None, by_1);
            assert_eq!(s1, *t_1);
            assert_eq!(1, lines.len());

            if let Some(OrderLine::Cancelled {
                at: at_2,
                by: by_2,
                target: t_2,
            }) = lines.first()
            {
                assert!(*at_2 >= at_1);
                assert_eq!(Some(Who::Anonymous), *by_2);
                assert_eq!(line, **t_2);
            } else {
                assert!(false, "not exists cancelled ordered item");
            }
        } else {
            assert!(false, "failed to cancel ordered item");
        }
    }

    #[test]
    fn cancel_ordered_item() {
        let s1 = Order::start(None);
        let at = Local::now();

        let line1 = OrderLine::OrderedItem {
            at: at.clone(),
            by: None,
            item: NotBlankString::from_str("item-1").unwrap(),
            qty: OneOrMore::one(),
            unit_price: to_positive(550),
        };

        let line2 = OrderLine::OrderedItem {
            at: now(),
            by: None,
            item: NotBlankString::from_str("item-2").unwrap(),
            qty: OneOrMore::new(3).unwrap(),
            unit_price: to_positive(2200),
        };

        let s2 = Order::Ordered {
            at: at.clone(),
            by: None,
            target: Box::new(s1.clone()),
            lines: vec![line1.clone(), line2.clone()],
        };

        let r = s2.cancel_ordered_item(1, None);

        if let Ok(Order::Ordered {
            at: at_1,
            by: by_1,
            target: t_1,
            lines,
        }) = r
        {
            assert_eq!(at, at_1);
            assert_eq!(None, by_1);
            assert_eq!(s1, *t_1);
            assert_eq!(2, lines.len());

            assert_eq!(line1, *lines.first().unwrap());

            if let Some(OrderLine::Cancelled { target: t_2, .. }) = lines.last() {
                assert_eq!(line2, **t_2);
            } else {
                assert!(false, "not exists cancelled ordered item");
            }
        } else {
            assert!(false, "failed to cancel ordered item");
        }
    }

    #[test]
    fn cancel_ordered_item_to_started() {
        let s = Order::start(None);

        let r = s.cancel_ordered_item(0, None);

        assert!(r.is_err(), "cancelled ordered item to started state");
    }

    #[test]
    fn cancel_ordered_item_to_cancelled() {
        let s = Order::start(None).cancel(None).unwrap();

        let r = s.cancel_ordered_item(0, Some(Who::System(NotBlankString::from_str("test1"))));

        assert!(r.is_err(), "cancelled ordered item to cancelled state");
    }

    #[test]
    fn cancel_ordered_item_with_invalid_index() {
        let s1 = Order::start(None);
        let at = Local::now();

        let s2 = Order::Ordered {
            at: at.clone(),
            by: None,
            target: Box::new(s1.clone()),
            lines: vec![OrderLine::OrderedItem {
                at: at.clone(),
                by: None,
                item: NotBlankString::from_str("item-1").unwrap(),
                qty: OneOrMore::one(),
                unit_price: to_positive(550),
            }],
        };

        let r = s2.cancel_ordered_item(1, None);

        assert!(r.is_err(), "cancelled not exists ordered item");
    }

    #[test]
    fn cancel_cancelled_ordered_item() {
        let s1 = Order::start(None);
        let at = Local::now();

        let line = OrderLine::OrderedItem {
            at: at.clone(),
            by: None,
            item: NotBlankString::from_str("item-1").unwrap(),
            qty: OneOrMore::one(),
            unit_price: to_positive(550),
        };

        let s2 = Order::Ordered {
            at: at.clone(),
            by: None,
            target: Box::new(s1.clone()),
            lines: vec![OrderLine::Cancelled {
                at: now(),
                by: None,
                target: Box::new(line),
            }],
        };

        let r = s2.cancel_ordered_item(0, None);

        assert!(r.is_err(), "duplicate cancelled ordered item")
    }
}
