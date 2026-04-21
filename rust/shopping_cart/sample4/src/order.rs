#![allow(dead_code)]

use chrono::prelude::*;
use rust_decimal::prelude::*;
use std::fmt::Debug;

pub type Quantity = usize;
pub type Amount = Decimal;
pub type Date = DateTime<Local>;

pub type ItemId = NotBlankString;
pub type UserId = NotBlankString;
pub type PromotionId = NotBlankString;

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

impl TryFrom<&str> for NotBlankString {
    type Error = String;

    fn try_from(value: &str) -> std::result::Result<Self, Self::Error> {
        Self::new(value.into()).ok_or(format!("'{value}' is invalid"))
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

impl TryFrom<usize> for OneOrMore {
    type Error = String;

    fn try_from(value: usize) -> std::result::Result<Self, Self::Error> {
        Self::new(value).ok_or("must be more than one".into())
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

impl TryFrom<usize> for PositiveAmount {
    type Error = String;

    fn try_from(value: usize) -> std::result::Result<Self, Self::Error> {
        let v = Amount::from_usize(value).ok_or("failed convert")?;
        Self::try_from(v)
    }
}

#[derive(Debug, Clone, PartialEq)]
pub struct UnderZeroAmount {
    value: Amount,
}

impl ConstraintType<Amount> for UnderZeroAmount {
    fn new(value: Amount) -> Option<Self> {
        if value < Amount::ZERO {
            Some(UnderZeroAmount { value })
        } else {
            None
        }
    }

    fn value(&self) -> &Amount {
        &self.value
    }
}

impl TryFrom<isize> for UnderZeroAmount {
    type Error = String;

    fn try_from(value: isize) -> std::result::Result<Self, Self::Error> {
        let v = Amount::from_isize(value).ok_or("failed convert")?;
        Self::new(v).ok_or(format!("{value} is not strictly negative"))
    }
}

#[derive(Debug, Clone, PartialEq)]
pub struct TwoOrMoreVec<T> {
    value: Vec<T>,
}

impl<T> TwoOrMoreVec<T>
where
    T: PartialEq,
{
    fn contains(&self, x: &T) -> bool {
        self.value.contains(x)
    }
}

impl<T> ConstraintType<Vec<T>> for TwoOrMoreVec<T>
where
    T: Debug + Clone + PartialEq,
{
    fn new(value: Vec<T>) -> Option<Self> {
        if value.len() >= 2 {
            Some(Self { value })
        } else {
            None
        }
    }

    fn value(&self) -> &Vec<T> {
        &self.value
    }
}

impl<T> TryFrom<Vec<T>> for TwoOrMoreVec<T>
where
    T: Debug + Clone + PartialEq,
{
    type Error = String;

    fn try_from(value: Vec<T>) -> std::result::Result<Self, Self::Error> {
        Self::new(value).ok_or("must be two or more elements".into())
    }
}

pub trait EventSource {
    fn at(&self) -> &Date;
    fn by(&self) -> &Option<Who>;
}

pub trait Subtotal<T> {
    fn subtotal(&self) -> Option<T>;
}

#[derive(Debug, Clone, PartialEq)]
pub enum Who {
    Anonymous,
    User(UserId),
    System(Option<NotBlankString>),
    Auto,
}

#[derive(Debug, Clone, PartialEq)]
pub enum Dependence {
    Single { index: usize },
    Multi { indexes: TwoOrMoreVec<usize> },
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
    Discounted {
        at: Date,
        by: Option<Who>,
        discount_value: UnderZeroAmount,
        promotion: Option<PromotionId>,
        dependencies: Option<Dependence>,
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
            Self::Cancelled { .. } => OrderError::already_cancelled("order"),
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
            Self::Cancelled { .. } => OrderError::invalid_operation("cancelled"),
        }
    }

    pub fn cancel_line(&self, index: usize, by: Option<Who>) -> Result<Self> {
        match self {
            Order::Started { .. } => {
                OrderError::invalid_operation("started order has not ordered item")
            }
            Order::Cancelled { .. } => OrderError::invalid_operation("cancelled order"),
            Order::Ordered {
                at,
                by: by_s,
                target,
                lines,
            } => {
                if let Some(line) = lines.get(index) {
                    if line.is_cancelled() {
                        OrderError::already_cancelled(&format!("line index={index}"))
                    } else {
                        let mut new_lines = lines.clone();

                        for i in 0..new_lines.len() {
                            if i == index {
                                new_lines[i] = line.cancel(by.clone())?;
                            } else if new_lines[i].is_dependent(index) {
                                new_lines[i] = new_lines[i].cancel(Some(Who::Auto))?;
                            }
                        }

                        Ok(Order::Ordered {
                            at: at.clone(),
                            by: by_s.clone(),
                            target: target.clone(),
                            lines: new_lines,
                        })
                    }
                } else {
                    OrderError::not_found_line(&format!("line index={index}"))
                }
            }
        }
    }

    pub fn discount(
        &self,
        discount_value: UnderZeroAmount,
        dependencies: Option<Dependence>,
        promotion: Option<PromotionId>,
        by: Option<Who>,
    ) -> Result<Self> {
        match self {
            Self::Started { .. } | Self::Cancelled { .. } => {
                OrderError::invalid_operation("state is not ordered")
            }
            Self::Ordered {
                at,
                by: by_s,
                target,
                lines,
            } => {
                self.validate_discount(&discount_value, &dependencies, lines)?;

                let mut new_lines = lines.clone();

                new_lines.push(OrderLine::Discounted {
                    at: now(),
                    by,
                    discount_value,
                    promotion,
                    dependencies,
                });

                Ok(Self::Ordered {
                    at: at.clone(),
                    by: by_s.clone(),
                    target: target.clone(),
                    lines: new_lines,
                })
            }
        }
    }

    fn validate_discount(
        &self,
        discount_value: &UnderZeroAmount,
        dependencies: &Option<Dependence>,
        lines: &Vec<OrderLine>,
    ) -> Result<()> {
        if self.subtotal().unwrap_or(Amount::zero()) < discount_value.value.abs() {
            OrderError::over_discount("discount more than subtotal")
        } else {
            if let Some(d) = dependencies {
                let os = Self::pickup_ordered(lines, d)?;

                if os.subtotal().unwrap_or(Amount::zero()) < discount_value.value.abs() {
                    return OrderError::over_discount(
                        "discount more than target order line's subtotal",
                    );
                }
            }

            Ok(())
        }
    }

    fn pickup_ordered<'a>(
        lines: &'a Vec<OrderLine>,
        dep: &Dependence,
    ) -> Result<Vec<&'a OrderLine>> {
        let mut res = vec![];

        let idx = match dep {
            Dependence::Single { index } => &vec![*index],
            Dependence::Multi { indexes } => indexes.value(),
        };

        for i in idx {
            let line = lines.get(*i);

            if let Some(line) = line {
                if line.is_ordered() {
                    res.push(line);
                } else {
                    return OrderError::invalid_line("target line is not ordered item");
                }
            } else {
                return OrderError::not_found_line(&format!("invalid index={i}"));
            }
        }

        Ok(res)
    }
}

impl OrderLine {
    fn is_ordered(&self) -> bool {
        match self {
            Self::OrderedItem { .. } => true,
            _ => false,
        }
    }

    fn is_cancelled(&self) -> bool {
        match self {
            Self::Cancelled { .. } => true,
            _ => false,
        }
    }

    fn is_dependent(&self, index: usize) -> bool {
        match self {
            Self::Discounted { dependencies, .. } => match dependencies {
                Some(Dependence::Single { index: x }) => *x == index,
                Some(Dependence::Multi { indexes }) => indexes.contains(&index),
                None => false,
            },
            _ => false,
        }
    }

    fn cancel(&self, by: Option<Who>) -> Result<Self> {
        if self.is_cancelled() {
            OrderError::already_cancelled("order line")
        } else {
            Ok(Self::Cancelled {
                at: now(),
                by,
                target: Box::new(self.clone()),
            })
        }
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

impl EventSource for OrderLine {
    fn at(&self) -> &Date {
        match self {
            Self::OrderedItem { at, .. }
            | Self::Cancelled { at, .. }
            | Self::Discounted { at, .. } => at,
        }
    }

    fn by(&self) -> &Option<Who> {
        match self {
            Self::OrderedItem { by, .. }
            | Self::Cancelled { by, .. }
            | Self::Discounted { by, .. } => by,
        }
    }
}

impl Subtotal<Amount> for Order {
    fn subtotal(&self) -> Option<Amount> {
        match self {
            Self::Started { .. } | Self::Cancelled { .. } => None,
            Self::Ordered { lines, .. } => Some(lines.iter().fold(Amount::ZERO, |acc, v| {
                acc + v.subtotal().unwrap_or(Amount::ZERO)
            })),
        }
    }
}

impl Subtotal<Amount> for OrderLine {
    fn subtotal(&self) -> Option<Amount> {
        match self {
            Self::OrderedItem {
                qty, unit_price, ..
            } => Amount::from_usize(qty.value).map(|q| q * unit_price.value),
            Self::Cancelled { .. } => None,
            Self::Discounted { discount_value, .. } => Some(discount_value.value),
        }
    }
}

impl Subtotal<Amount> for Vec<&OrderLine> {
    fn subtotal(&self) -> Option<Amount> {
        let res = self.iter().fold(Amount::ZERO, |acc, v| {
            acc + v.subtotal().unwrap_or(Amount::ZERO)
        });

        Some(res)
    }
}

#[derive(Debug, Clone, PartialEq)]
pub enum OrderError {
    InvalidOperation(Option<String>),
    AlreadyCancelled(Option<String>),
    NotFoundLine(Option<String>),
    OverDiscount(Option<String>),
    InvalidLine(Option<String>),
}

impl OrderError {
    fn over_discount<T>(msg: &str) -> Result<T> {
        Err(OrderError::OverDiscount(Some(msg.into())))
    }

    fn not_found_line<T>(msg: &str) -> Result<T> {
        Err(OrderError::NotFoundLine(Some(msg.into())))
    }

    fn invalid_operation<T>(msg: &str) -> Result<T> {
        Err(OrderError::InvalidOperation(Some(msg.into())))
    }

    fn already_cancelled<T>(msg: &str) -> Result<T> {
        Err(OrderError::AlreadyCancelled(Some(msg.into())))
    }

    fn invalid_line<T>(msg: &str) -> Result<T> {
        Err(OrderError::InvalidLine(Some(msg.into())))
    }
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
    fn new_underzeroamount() {
        let r = UnderZeroAmount::new(Amount::NEGATIVE_ONE);

        assert!(r.is_some());
        assert_eq!(Amount::NEGATIVE_ONE, *r.unwrap().value());

        let r2 = UnderZeroAmount::new(Amount::from_isize(-10).unwrap());

        assert!(r2.is_some());
        assert_eq!(Amount::from_isize(-10).unwrap(), *r2.unwrap().value());
    }

    #[test]
    fn new_underzeroamount_with_zero() {
        let r = UnderZeroAmount::new(Amount::ZERO);

        assert!(r.is_none());
    }

    #[test]
    fn new_underzeroamount_with_positive() {
        let r = UnderZeroAmount::new(Amount::ONE);

        assert!(r.is_none());
    }

    #[test]
    fn new_twooremorevec() {
        let r = TwoOrMoreVec::new(vec!["a", ""]);

        assert!(r.is_some());
        assert_eq!(vec!["a", ""], *r.unwrap().value());
    }

    #[test]
    fn new_twooremorevec_with_single() {
        let r = TwoOrMoreVec::new(vec![1]);

        assert!(r.is_none());
    }

    #[test]
    fn new_twooremorevec_with_empty() {
        let r = TwoOrMoreVec::<&str>::new(vec![]);

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
        let by = Some(Who::System("test1".try_into().ok()));
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
            assert!(at > *s.at());
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

        let item = "item-1".try_into().unwrap();
        let qty = OneOrMore::one();
        let unit_price = to_positive(1100);
        let by = Some(Who::User("u1".try_into().unwrap()));

        let r = s.order_item(item, qty, unit_price, by.clone());

        if let Ok(Order::Ordered {
            at: at_1,
            by: by_1,
            target,
            lines,
        }) = r
        {
            assert!(at_1 > *s.at());
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

        let item = "item1".try_into().unwrap();
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
                item: "item-1".try_into().unwrap(),
                qty: OneOrMore::one(),
                unit_price: to_positive(550),
            }],
        };

        let item = "item-2".try_into().unwrap();
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
                assert!(*at_2 > at_1);
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
                item: "item-1".try_into().unwrap(),
                qty: OneOrMore::one(),
                unit_price: to_positive(550),
            }],
        };

        let item = "item-1".try_into().unwrap();
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
                assert!(*at_2 > at_1);
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
            item: "item-1".try_into().unwrap(),
            qty: OneOrMore::one(),
            unit_price: to_positive(550),
        };

        let s2 = Order::Ordered {
            at: at.clone(),
            by: None,
            target: Box::new(s1.clone()),
            lines: vec![line.clone()],
        };

        let r = s2.cancel_line(0, Some(Who::Anonymous));

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
                assert!(*at_2 > at_1);
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
    fn cancel_line_ordered_item() {
        let s1 = Order::start(None);
        let at = Local::now();

        let line1 = OrderLine::OrderedItem {
            at: at.clone(),
            by: None,
            item: "item-1".try_into().unwrap(),
            qty: OneOrMore::one(),
            unit_price: to_positive(550),
        };

        let line2 = OrderLine::OrderedItem {
            at: now(),
            by: None,
            item: "item-2".try_into().unwrap(),
            qty: OneOrMore::new(3).unwrap(),
            unit_price: to_positive(2200),
        };

        let s2 = Order::Ordered {
            at: at.clone(),
            by: None,
            target: Box::new(s1.clone()),
            lines: vec![line1.clone(), line2.clone()],
        };

        let r = s2.cancel_line(1, None);

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
    fn cancel_line_discounted() {
        let s1 = Order::start(None);
        let at = Local::now();

        let line1 = OrderLine::OrderedItem {
            at: at.clone(),
            by: None,
            item: "item-1".try_into().unwrap(),
            qty: OneOrMore::one(),
            unit_price: to_positive(550),
        };

        let line2 = OrderLine::OrderedItem {
            at: now(),
            by: None,
            item: "item-2".try_into().unwrap(),
            qty: OneOrMore::new(3).unwrap(),
            unit_price: to_positive(2200),
        };

        let line3 = OrderLine::Discounted {
            at: now(),
            by: None,
            discount_value: (-50).try_into().unwrap(),
            promotion: None,
            dependencies: Some(Dependence::Single { index: 0 }),
        };

        let s2 = Order::Ordered {
            at: at.clone(),
            by: None,
            target: Box::new(s1.clone()),
            lines: vec![line1, line2, line3],
        };

        let r = s2.cancel_line(2, None);

        if let Ok(Order::Ordered { lines, .. }) = r {
            assert_eq!(3, lines.len());
            assert!(lines.last().unwrap().is_cancelled());
        } else {
            assert!(false, "failed to cancel discounted");
        }
    }

    #[test]
    fn cancel_line_to_started() {
        let s = Order::start(None);

        let r = s.cancel_line(0, None);

        assert!(r.is_err(), "cancelled ordered item to started state");
    }

    #[test]
    fn cancel_line_to_cancelled() {
        let s = Order::start(None).cancel(None).unwrap();

        let r = s.cancel_line(0, Some(Who::System("test1".try_into().ok())));

        assert!(r.is_err(), "cancelled ordered item to cancelled state");
    }

    #[test]
    fn cancel_line_with_invalid_index() {
        let s1 = Order::start(None);
        let at = Local::now();

        let s2 = Order::Ordered {
            at: at.clone(),
            by: None,
            target: Box::new(s1.clone()),
            lines: vec![OrderLine::OrderedItem {
                at: at.clone(),
                by: None,
                item: "item-1".try_into().unwrap(),
                qty: OneOrMore::one(),
                unit_price: to_positive(550),
            }],
        };

        let r = s2.cancel_line(1, None);

        assert!(r.is_err(), "cancelled not exists ordered item");
    }

    #[test]
    fn cancel_line_cancelled_ordered_item() {
        let s1 = Order::start(None);
        let at = Local::now();

        let line = OrderLine::OrderedItem {
            at: at.clone(),
            by: None,
            item: "item-1".try_into().unwrap(),
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

        let r = s2.cancel_line(0, None);

        assert!(r.is_err(), "duplicate cancelled ordered item")
    }

    #[test]
    fn subtotal_started() {
        let s = Order::start(None);
        assert!(s.subtotal().is_none());
    }

    #[test]
    fn subtotal_cancelled() {
        let s = Order::start(None).cancel(None).unwrap();
        assert!(s.subtotal().is_none());
    }

    #[test]
    fn subtotal_single_ordered() {
        let s1 = Order::start(None);
        let at = Local::now();

        let s2 = Order::Ordered {
            at: at.clone(),
            by: None,
            target: Box::new(s1.clone()),
            lines: vec![OrderLine::OrderedItem {
                at: at.clone(),
                by: None,
                item: "item-1".try_into().unwrap(),
                qty: OneOrMore::new(3).unwrap(),
                unit_price: to_positive(550),
            }],
        };

        let r = s2.subtotal();

        assert!(r.is_some());
        assert_eq!(Amount::from_usize(3 * 550), r);
    }

    #[test]
    fn subtotal_multi_ordered() {
        let s1 = Order::start(None);
        let at = Local::now();

        let s2 = Order::Ordered {
            at: at.clone(),
            by: None,
            target: Box::new(s1.clone()),
            lines: vec![
                OrderLine::OrderedItem {
                    at: at.clone(),
                    by: None,
                    item: "item-1".try_into().unwrap(),
                    qty: OneOrMore::new(3).unwrap(),
                    unit_price: to_positive(550),
                },
                OrderLine::OrderedItem {
                    at: at.clone(),
                    by: None,
                    item: "item-2".try_into().unwrap(),
                    qty: OneOrMore::new(2).unwrap(),
                    unit_price: to_positive(250),
                },
            ],
        };

        let r = s2.subtotal();

        assert!(r.is_some());
        assert_eq!(Amount::from_usize(3 * 550 + 2 * 250), r);
    }

    #[test]
    fn subtotal_multi_ordered_with_cancelled() {
        let s1 = Order::start(None);
        let at = Local::now();

        let s2 = Order::Ordered {
            at: at.clone(),
            by: None,
            target: Box::new(s1.clone()),
            lines: vec![
                OrderLine::OrderedItem {
                    at: at.clone(),
                    by: None,
                    item: "item-1".try_into().unwrap(),
                    qty: OneOrMore::new(3).unwrap(),
                    unit_price: to_positive(550),
                },
                OrderLine::Cancelled {
                    at: at.clone(),
                    by: None,
                    target: Box::new(OrderLine::OrderedItem {
                        at: at.clone(),
                        by: None,
                        item: "item-3".try_into().unwrap(),
                        qty: OneOrMore::new(1).unwrap(),
                        unit_price: to_positive(120),
                    }),
                },
                OrderLine::OrderedItem {
                    at: at.clone(),
                    by: None,
                    item: "item-2".try_into().unwrap(),
                    qty: OneOrMore::new(2).unwrap(),
                    unit_price: to_positive(250),
                },
            ],
        };

        let r = s2.subtotal();

        assert!(r.is_some());
        assert_eq!(Amount::from_usize(3 * 550 + 2 * 250), r);
    }

    #[test]
    fn subtotal_line_ordereditem() {
        let s = OrderLine::OrderedItem {
            at: now(),
            by: None,
            item: "i-1".try_into().unwrap(),
            qty: 3.try_into().unwrap(),
            unit_price: 350.try_into().unwrap(),
        };

        let r = s.subtotal();

        assert!(r.is_some());
        assert_eq!(1050, r.unwrap().to_isize().unwrap());
    }

    #[test]
    fn subtotal_line_cancelled() {
        let s = OrderLine::Cancelled {
            at: now(),
            by: None,
            target: Box::new(OrderLine::OrderedItem {
                at: now(),
                by: None,
                item: "i-1".try_into().unwrap(),
                qty: 3.try_into().unwrap(),
                unit_price: 350.try_into().unwrap(),
            }),
        };

        let r = s.subtotal();

        assert!(r.is_none());
    }

    #[test]
    fn subtotal_line_discount() {
        let s = OrderLine::Discounted {
            at: now(),
            by: None,
            discount_value: (-100).try_into().unwrap(),
            promotion: None,
            dependencies: None,
        };

        let r = s.subtotal();

        assert!(r.is_some());
        assert_eq!(-100, r.unwrap().to_isize().unwrap());
    }

    #[test]
    fn subtotal_lines() {
        let line1 = OrderLine::OrderedItem {
            at: now(),
            by: None,
            item: "item-1".try_into().unwrap(),
            qty: OneOrMore::one(),
            unit_price: to_positive(550),
        };

        let line2 = OrderLine::Cancelled {
            at: now(),
            by: None,
            target: Box::new(OrderLine::OrderedItem {
                at: now(),
                by: None,
                item: "item-2".try_into().unwrap(),
                qty: OneOrMore::new(2).unwrap(),
                unit_price: to_positive(5000),
            }),
        };

        let line3 = OrderLine::OrderedItem {
            at: now(),
            by: None,
            item: "item-3".try_into().unwrap(),
            qty: OneOrMore::new(3).unwrap(),
            unit_price: to_positive(1000),
        };

        let line4 = OrderLine::Discounted {
            at: now(),
            by: None,
            discount_value: (-100).try_into().unwrap(),
            promotion: None,
            dependencies: None,
        };

        let r = vec![&line1, &line2, &line3, &line4].subtotal();

        assert!(r.is_some());
        assert_eq!(3450, r.unwrap().to_isize().unwrap());
    }

    #[test]
    fn discount_to_started() {
        let s = Order::start(None);

        let r = s.discount((-10).try_into().unwrap(), None, None, None);

        assert!(r.is_err(), "discount started");
    }

    #[test]
    fn discount_to_cancelled() {
        let s = Order::start(None).cancel(None).unwrap();

        let r = s.discount((-10).try_into().unwrap(), None, None, None);

        assert!(r.is_err(), "discount cancelled");
    }

    #[test]
    fn discount_no_dependent() {
        let s1 = Order::start(None);
        let at = Local::now();

        let s2 = Order::Ordered {
            at: at.clone(),
            by: None,
            target: Box::new(s1.clone()),
            lines: vec![
                OrderLine::OrderedItem {
                    at: at.clone(),
                    by: None,
                    item: "item-1".try_into().unwrap(),
                    qty: OneOrMore::new(3).unwrap(),
                    unit_price: to_positive(550),
                },
                OrderLine::OrderedItem {
                    at: at.clone(),
                    by: None,
                    item: "item-2".try_into().unwrap(),
                    qty: OneOrMore::new(2).unwrap(),
                    unit_price: to_positive(250),
                },
            ],
        };

        let r = s2.discount(
            (-300).try_into().unwrap(),
            None,
            "p1".try_into().ok(),
            Some(Who::Anonymous),
        );

        if let Ok(Order::Ordered { lines, .. }) = r {
            assert_eq!(3, lines.len());

            if let Some(OrderLine::Discounted {
                at: at_1,
                by,
                discount_value,
                promotion,
                dependencies,
            }) = lines.last()
            {
                assert!(*at_1 > at);
                assert_eq!(Some(Who::Anonymous), *by);
                assert_eq!(-300, discount_value.value.to_isize().unwrap());
                assert_eq!("p1".to_string(), promotion.clone().unwrap().value);
                assert_eq!(None, *dependencies);
            } else {
                assert!(false, "not discounted")
            }
        } else {
            assert!(false, "failed to discount")
        }
    }

    #[test]
    fn discount_all_no_dependent() {
        let s1 = Order::start(None);
        let at = Local::now();

        let s2 = Order::Ordered {
            at: at.clone(),
            by: None,
            target: Box::new(s1.clone()),
            lines: vec![
                OrderLine::OrderedItem {
                    at: at.clone(),
                    by: None,
                    item: "item-1".try_into().unwrap(),
                    qty: OneOrMore::new(3).unwrap(),
                    unit_price: to_positive(550),
                },
                OrderLine::OrderedItem {
                    at: at.clone(),
                    by: None,
                    item: "item-2".try_into().unwrap(),
                    qty: OneOrMore::new(2).unwrap(),
                    unit_price: to_positive(250),
                },
            ],
        };

        let r = s2.discount(
            (-2150).try_into().unwrap(),
            None,
            "p1".try_into().ok(),
            Some(Who::Anonymous),
        );

        if let Ok(Order::Ordered { lines, .. }) = r {
            assert_eq!(3, lines.len());

            if let Some(OrderLine::Discounted { discount_value, .. }) = lines.last() {
                assert_eq!(-2150, discount_value.value.to_isize().unwrap());
            } else {
                assert!(false, "not discounted")
            }
        } else {
            assert!(false, "failed to discount")
        }
    }

    #[test]
    fn discount_over_no_dependent() {
        let s1 = Order::start(None);
        let at = Local::now();

        let s2 = Order::Ordered {
            at: at.clone(),
            by: None,
            target: Box::new(s1.clone()),
            lines: vec![
                OrderLine::OrderedItem {
                    at: at.clone(),
                    by: None,
                    item: "item-1".try_into().unwrap(),
                    qty: OneOrMore::new(3).unwrap(),
                    unit_price: to_positive(550),
                },
                OrderLine::OrderedItem {
                    at: at.clone(),
                    by: None,
                    item: "item-2".try_into().unwrap(),
                    qty: OneOrMore::new(2).unwrap(),
                    unit_price: to_positive(250),
                },
            ],
        };

        let r = s2.discount(
            (-2151).try_into().unwrap(),
            None,
            "p1".try_into().ok(),
            Some(Who::Anonymous),
        );

        assert!(r.is_err(), "permit over discount");
    }

    #[test]
    fn discount_single_dependent() {
        let s1 = Order::start(None);
        let at = Local::now();

        let s2 = Order::Ordered {
            at: at.clone(),
            by: None,
            target: Box::new(s1.clone()),
            lines: vec![
                OrderLine::OrderedItem {
                    at: at.clone(),
                    by: None,
                    item: "item-1".try_into().unwrap(),
                    qty: OneOrMore::new(3).unwrap(),
                    unit_price: to_positive(550),
                },
                OrderLine::OrderedItem {
                    at: at.clone(),
                    by: None,
                    item: "item-2".try_into().unwrap(),
                    qty: OneOrMore::new(2).unwrap(),
                    unit_price: to_positive(250),
                },
            ],
        };

        let r = s2.discount(
            (-300).try_into().unwrap(),
            Some(Dependence::Single { index: 1 }),
            "p1".try_into().ok(),
            Some(Who::Anonymous),
        );

        if let Ok(Order::Ordered { lines, .. }) = r {
            assert_eq!(3, lines.len());

            if let Some(OrderLine::Discounted {
                discount_value,
                dependencies,
                ..
            }) = lines.last()
            {
                assert_eq!(-300, discount_value.value.to_isize().unwrap());
                assert_eq!(Some(Dependence::Single { index: 1 }), *dependencies);
            } else {
                assert!(false, "not discounted")
            }
        } else {
            assert!(false, "failed to discount")
        }
    }

    #[test]
    fn discount_over_single_dependent() {
        let s1 = Order::start(None);
        let at = Local::now();

        let s2 = Order::Ordered {
            at: at.clone(),
            by: None,
            target: Box::new(s1.clone()),
            lines: vec![
                OrderLine::OrderedItem {
                    at: at.clone(),
                    by: None,
                    item: "item-1".try_into().unwrap(),
                    qty: OneOrMore::new(3).unwrap(),
                    unit_price: to_positive(550),
                },
                OrderLine::OrderedItem {
                    at: at.clone(),
                    by: None,
                    item: "item-2".try_into().unwrap(),
                    qty: OneOrMore::new(2).unwrap(),
                    unit_price: to_positive(250),
                },
            ],
        };

        let r = s2.discount(
            (-501).try_into().unwrap(),
            Some(Dependence::Single { index: 1 }),
            "p1".try_into().ok(),
            Some(Who::Anonymous),
        );

        assert!(r.is_err(), "over discount dependent ordered");
    }

    #[test]
    fn discount_over_single_dependent_with_invalid_index() {
        let s1 = Order::start(None);
        let at = Local::now();

        let s2 = Order::Ordered {
            at: at.clone(),
            by: None,
            target: Box::new(s1.clone()),
            lines: vec![
                OrderLine::OrderedItem {
                    at: at.clone(),
                    by: None,
                    item: "item-1".try_into().unwrap(),
                    qty: OneOrMore::new(3).unwrap(),
                    unit_price: to_positive(550),
                },
                OrderLine::OrderedItem {
                    at: at.clone(),
                    by: None,
                    item: "item-2".try_into().unwrap(),
                    qty: OneOrMore::new(2).unwrap(),
                    unit_price: to_positive(250),
                },
            ],
        };

        let r = s2.discount(
            (-100).try_into().unwrap(),
            Some(Dependence::Single { index: 2 }),
            "p1".try_into().ok(),
            Some(Who::Anonymous),
        );

        assert!(r.is_err(), "out of range dependent index");
    }

    #[test]
    fn discount_over_single_dependent_with_cancelled_index() {
        let s1 = Order::start(None);
        let at = Local::now();

        let s2 = Order::Ordered {
            at: at.clone(),
            by: None,
            target: Box::new(s1.clone()),
            lines: vec![
                OrderLine::OrderedItem {
                    at: at.clone(),
                    by: None,
                    item: "item-1".try_into().unwrap(),
                    qty: OneOrMore::new(3).unwrap(),
                    unit_price: to_positive(550),
                },
                OrderLine::Cancelled {
                    at: at.clone(),
                    by: None,
                    target: Box::new(OrderLine::OrderedItem {
                        at: at.clone(),
                        by: None,
                        item: "item-2".try_into().unwrap(),
                        qty: OneOrMore::new(2).unwrap(),
                        unit_price: to_positive(250),
                    }),
                },
            ],
        };

        let r = s2.discount(
            (-100).try_into().unwrap(),
            Some(Dependence::Single { index: 1 }),
            "p1".try_into().ok(),
            Some(Who::Anonymous),
        );

        assert!(r.is_err(), "cencelled index");
    }

    #[test]
    fn discount_multi_dependent() {
        let s1 = Order::start(None);
        let at = Local::now();

        let s2 = Order::Ordered {
            at: at.clone(),
            by: None,
            target: Box::new(s1.clone()),
            lines: vec![
                OrderLine::OrderedItem {
                    at: at.clone(),
                    by: None,
                    item: "item-1".try_into().unwrap(),
                    qty: OneOrMore::new(3).unwrap(),
                    unit_price: to_positive(550),
                },
                OrderLine::OrderedItem {
                    at: at.clone(),
                    by: None,
                    item: "item-2".try_into().unwrap(),
                    qty: OneOrMore::new(2).unwrap(),
                    unit_price: to_positive(250),
                },
                OrderLine::OrderedItem {
                    at: at.clone(),
                    by: None,
                    item: "item-3".try_into().unwrap(),
                    qty: OneOrMore::new(1).unwrap(),
                    unit_price: to_positive(110),
                },
            ],
        };

        let r = s2.discount(
            (-300).try_into().unwrap(),
            Some(Dependence::Multi {
                indexes: vec![0, 1].try_into().unwrap(),
            }),
            "p1".try_into().ok(),
            Some(Who::Anonymous),
        );

        if let Ok(Order::Ordered { lines, .. }) = r {
            assert_eq!(4, lines.len());

            if let Some(OrderLine::Discounted {
                discount_value,
                dependencies,
                ..
            }) = lines.last()
            {
                assert_eq!(-300, discount_value.value.to_isize().unwrap());
                assert_eq!(
                    Some(Dependence::Multi {
                        indexes: vec![0, 1].try_into().unwrap()
                    }),
                    *dependencies
                );
            } else {
                assert!(false, "not discounted")
            }
        } else {
            assert!(false, "failed to discount")
        }
    }

    #[test]
    fn discount_over_multi_dependent() {
        let s1 = Order::start(None);
        let at = Local::now();

        let s2 = Order::Ordered {
            at: at.clone(),
            by: None,
            target: Box::new(s1.clone()),
            lines: vec![
                OrderLine::OrderedItem {
                    at: at.clone(),
                    by: None,
                    item: "item-1".try_into().unwrap(),
                    qty: OneOrMore::new(3).unwrap(),
                    unit_price: to_positive(550),
                },
                OrderLine::OrderedItem {
                    at: at.clone(),
                    by: None,
                    item: "item-2".try_into().unwrap(),
                    qty: OneOrMore::new(2).unwrap(),
                    unit_price: to_positive(250),
                },
                OrderLine::OrderedItem {
                    at: at.clone(),
                    by: None,
                    item: "item-3".try_into().unwrap(),
                    qty: OneOrMore::new(1).unwrap(),
                    unit_price: to_positive(110),
                },
            ],
        };

        let r = s2.discount(
            (-1761).try_into().unwrap(),
            Some(Dependence::Multi {
                indexes: vec![0, 2].try_into().unwrap(),
            }),
            "p1".try_into().ok(),
            Some(Who::Anonymous),
        );

        assert!(r.is_err(), "over discount dependent lines");
    }

    #[test]
    fn discount_multi_dependent_include_cancelled_index() {
        let s1 = Order::start(None);
        let at = Local::now();

        let s2 = Order::Ordered {
            at: at.clone(),
            by: None,
            target: Box::new(s1.clone()),
            lines: vec![
                OrderLine::OrderedItem {
                    at: at.clone(),
                    by: None,
                    item: "item-1".try_into().unwrap(),
                    qty: OneOrMore::new(3).unwrap(),
                    unit_price: to_positive(550),
                },
                OrderLine::Cancelled {
                    at: at.clone(),
                    by: None,
                    target: Box::new(OrderLine::OrderedItem {
                        at: at.clone(),
                        by: None,
                        item: "item-2".try_into().unwrap(),
                        qty: OneOrMore::new(2).unwrap(),
                        unit_price: to_positive(250),
                    }),
                },
                OrderLine::OrderedItem {
                    at: at.clone(),
                    by: None,
                    item: "item-3".try_into().unwrap(),
                    qty: OneOrMore::new(1).unwrap(),
                    unit_price: to_positive(110),
                },
            ],
        };

        let r = s2.discount(
            (-300).try_into().unwrap(),
            Some(Dependence::Multi {
                indexes: vec![0, 1].try_into().unwrap(),
            }),
            "p1".try_into().ok(),
            Some(Who::Anonymous),
        );

        assert!(r.is_err(), "permit cancelled dependent indexes");
    }

    #[test]
    fn discount_multi_dependent_include_discounted_index() {
        let s1 = Order::start(None);
        let at = Local::now();

        let s2 = Order::Ordered {
            at: at.clone(),
            by: None,
            target: Box::new(s1.clone()),
            lines: vec![
                OrderLine::OrderedItem {
                    at: at.clone(),
                    by: None,
                    item: "item-1".try_into().unwrap(),
                    qty: OneOrMore::new(3).unwrap(),
                    unit_price: to_positive(550),
                },
                OrderLine::Discounted {
                    at: now(),
                    by: None,
                    discount_value: (-30).try_into().unwrap(),
                    promotion: None,
                    dependencies: None,
                },
                OrderLine::OrderedItem {
                    at: at.clone(),
                    by: None,
                    item: "item-3".try_into().unwrap(),
                    qty: OneOrMore::new(1).unwrap(),
                    unit_price: to_positive(110),
                },
            ],
        };

        let r = s2.discount(
            (-300).try_into().unwrap(),
            Some(Dependence::Multi {
                indexes: vec![0, 1].try_into().unwrap(),
            }),
            "p1".try_into().ok(),
            Some(Who::Anonymous),
        );

        assert!(r.is_err(), "permit discounted dependent indexes");
    }

    #[test]
    fn cancel_ordered_item_with_dependent_discount() {
        let s1 = Order::start(None);
        let at = Local::now();

        let line1 = OrderLine::OrderedItem {
            at: at.clone(),
            by: None,
            item: "item-1".try_into().unwrap(),
            qty: OneOrMore::one(),
            unit_price: to_positive(550),
        };

        let line2 = OrderLine::OrderedItem {
            at: now(),
            by: None,
            item: "item-2".try_into().unwrap(),
            qty: OneOrMore::new(3).unwrap(),
            unit_price: to_positive(2200),
        };

        let line3 = OrderLine::OrderedItem {
            at: now(),
            by: None,
            item: "item-3".try_into().unwrap(),
            qty: OneOrMore::new(2).unwrap(),
            unit_price: to_positive(5000),
        };

        let line4 = OrderLine::Discounted {
            at: now(),
            by: None,
            discount_value: (-500).try_into().unwrap(),
            promotion: None,
            dependencies: None,
        };

        let line5 = OrderLine::Discounted {
            at: now(),
            by: None,
            discount_value: (-200).try_into().unwrap(),
            promotion: None,
            dependencies: Some(Dependence::Single { index: 0 }),
        };

        let line6 = OrderLine::Discounted {
            at: now(),
            by: None,
            discount_value: (-600).try_into().unwrap(),
            promotion: None,
            dependencies: Some(Dependence::Multi {
                indexes: vec![1, 2].try_into().unwrap(),
            }),
        };

        let line7 = OrderLine::Discounted {
            at: now(),
            by: None,
            discount_value: (-300).try_into().unwrap(),
            promotion: None,
            dependencies: Some(Dependence::Multi {
                indexes: vec![2, 0].try_into().unwrap(),
            }),
        };

        let s2 = Order::Ordered {
            at: at.clone(),
            by: None,
            target: Box::new(s1.clone()),
            lines: vec![line1, line2, line3, line4, line5, line6, line7],
        };

        let r = s2.cancel_line(0, None);

        if let Ok(Order::Ordered { lines, .. }) = r {
            assert!(
                lines.get(0).unwrap().is_cancelled(),
                "no cancel ordered item"
            );
            assert!(
                lines.get(4).unwrap().is_cancelled(),
                "no cancel dependent discount"
            );
            assert!(
                lines.get(6).unwrap().is_cancelled(),
                "no cancel dependent discount"
            );
        } else {
            assert!(false, "failed to cancel ordered item");
        }
    }

    #[test]
    fn cancel_selfline() {
        let at = Local::now();

        let line = OrderLine::OrderedItem {
            at: at.clone(),
            by: None,
            item: "item-1".try_into().unwrap(),
            qty: OneOrMore::one(),
            unit_price: to_positive(550),
        };

        let r = line.cancel(Some(Who::Anonymous));

        if let Ok(r) = r {
            assert!(r.is_cancelled());
            assert!(*r.at() > at);
            assert_eq!(Some(Who::Anonymous), *r.by());
        } else {
            assert!(false, "failed to cancel order line");
        }
    }

    #[test]
    fn cancel_selfline_cancelled() {
        let at = Local::now();

        let line = OrderLine::Cancelled {
            at: now(),
            by: None,
            target: Box::new(OrderLine::OrderedItem {
                at: at.clone(),
                by: None,
                item: "item-1".try_into().unwrap(),
                qty: OneOrMore::one(),
                unit_price: to_positive(550),
            }),
        };

        let r = line.cancel(Some(Who::Anonymous));
        assert!(r.is_err());
    }

    #[test]
    fn is_dependent_no_dependency() {
        let line = OrderLine::Discounted {
            at: now(),
            by: None,
            discount_value: (-500).try_into().unwrap(),
            promotion: None,
            dependencies: None,
        };

        assert_eq!(false, line.is_dependent(0));
    }

    #[test]
    fn is_dependent_single() {
        let line = OrderLine::Discounted {
            at: now(),
            by: None,
            discount_value: (-500).try_into().unwrap(),
            promotion: None,
            dependencies: Some(Dependence::Single { index: 2 }),
        };

        assert_eq!(false, line.is_dependent(0));
        assert_eq!(false, line.is_dependent(1));
        assert!(line.is_dependent(2));
        assert_eq!(false, line.is_dependent(3));
    }

    #[test]
    fn is_dependent_multi() {
        let line = OrderLine::Discounted {
            at: now(),
            by: None,
            discount_value: (-500).try_into().unwrap(),
            promotion: None,
            dependencies: Some(Dependence::Multi {
                indexes: vec![2, 0].try_into().unwrap(),
            }),
        };

        assert!(line.is_dependent(0));
        assert_eq!(false, line.is_dependent(1));
        assert!(line.is_dependent(2));
        assert_eq!(false, line.is_dependent(3));
    }

    #[test]
    fn is_dependent_cancelled() {
        let line = OrderLine::Discounted {
            at: now(),
            by: None,
            discount_value: (-500).try_into().unwrap(),
            promotion: None,
            dependencies: Some(Dependence::Multi {
                indexes: vec![2, 0].try_into().unwrap(),
            }),
        }
        .cancel(None)
        .unwrap();

        assert_eq!(false, line.is_dependent(0));
        assert_eq!(false, line.is_dependent(1));
        assert_eq!(false, line.is_dependent(2));
        assert_eq!(false, line.is_dependent(3));
    }

    #[test]
    fn is_dependent_ordered() {
        let line = OrderLine::OrderedItem {
            at: now(),
            by: None,
            item: "item-1".try_into().unwrap(),
            qty: OneOrMore::one(),
            unit_price: to_positive(550),
        };

        assert_eq!(false, line.is_dependent(0));
    }
}
