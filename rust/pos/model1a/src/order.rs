use chrono::prelude::*;
use rust_decimal::prelude::*;

pub type Date = DateTime<Local>;
pub type Amount = Decimal;
pub type Quantity = usize;
pub type ItemId = String;

type Result<T> = std::result::Result<T, OrderError>;

fn now() -> Date {
    Local::now()
}

#[derive(Clone, Debug, PartialEq)]
pub struct TransactionId(String);

#[derive(Clone, Debug, PartialEq)]
pub enum Order {
    Nothing,
    Empty {
        id: TransactionId,
        start: Date,
    },
    OnOrder {
        id: TransactionId,
        lines: Vec<OrderLine>,
        start: Date,
    },
    Checkout {
        id: TransactionId,
        billed: Amount,
        lines: Vec<OrderLine>,
        start: Date,
    },
    Paying {
        id: TransactionId,
        billed: Amount,
        paid: Amount,
        lines: Vec<OrderLine>,
        settlements: Vec<Settlement>,
        start: Date,
    },
    Paid {
        id: TransactionId,
        billed_paid: Amount,
        lines: Vec<OrderLine>,
        settlements: Vec<Settlement>,
        start: Date,
    },
    Done {
        id: TransactionId,
        billed_paid: Amount,
        lines: Vec<OrderLine>,
        settlements: Vec<Settlement>,
        start: Date,
        end: Date,
    },
    OverPaying {
        id: TransactionId,
        billed: Amount,
        paid: Amount,
        lines: Vec<OrderLine>,
        settlements: Vec<Settlement>,
        start: Date,
    },
    CancelledEmpty {
        id: TransactionId,
        start: Date,
        cancelled: Date,
    },
    Cancelled {
        id: TransactionId,
        lines: Vec<OrderLine>,
        start: Date,
        cancelled: Date,
    },
}

#[derive(Clone, Debug, PartialEq)]
pub enum OrderLine {
    OrderItem(Item, Quantity),
}

#[derive(Clone, Debug, PartialEq)]
pub enum Settlement {
    Payment(PaymentMethod),
    Refund(RefundMethod),
}

#[derive(Clone, Debug, PartialEq)]
pub enum PaymentMethod {
    Cash { paid: Amount, change: Amount },
    Credit { paid: Amount, payment_id: String },
}

#[derive(Clone, Debug, PartialEq)]
pub enum RefundMethod {
    Cash { refunded: Amount },
}

#[derive(Clone, Debug, PartialEq)]
pub struct Item {
    item_id: ItemId,
    unit_price: Amount,
    method: Option<ItemSelectMethod>,
}

#[derive(Clone, Debug, PartialEq)]
pub enum ItemSelectMethod {
    Scan(Option<ItemScan>),
    EnterCode(Option<String>),
}

#[derive(Clone, Debug, PartialEq)]
pub enum ItemScan {
    Barcode(Option<String>),
}

#[derive(Clone, Debug, PartialEq)]
pub enum OrderError {
    InvalidState,
    InvalidPayment(Option<PaymentMethod>),
    InvalidRefund(Option<RefundMethod>),
    OverRefund { paid: Amount, refund: Amount },
}

impl OrderError {
    fn invalid_state<T>() -> Result<T> {
        Err(Self::InvalidState)
    }

    fn invalid_payment<T>(payment: Option<PaymentMethod>) -> Result<T> {
        Err(Self::InvalidPayment(payment))
    }

    fn invalid_refund<T>(refund: Option<RefundMethod>) -> Result<T> {
        Err(Self::InvalidRefund(refund))
    }

    fn over_refund<T>(paid: Amount, refund: Amount) -> Result<T> {
        Err(Self::OverRefund { paid, refund })
    }
}

impl Order {
    pub fn start(&self, id: TransactionId) -> Result<Self> {
        match self {
            Self::Nothing => Ok(Self::Empty { id, start: now() }),
            _ => OrderError::invalid_state(),
        }
    }

    pub fn order_item(&self, item: Item, qty: Quantity) -> Result<Self> {
        match self {
            Self::Empty { id, start } => Ok(Self::OnOrder {
                id: id.clone(),
                lines: vec![OrderLine::OrderItem(item, qty)],
                start: start.clone(),
            }),
            Self::OnOrder { id, lines, start } => {
                let mut new_lines = lines.clone();
                new_lines.push(OrderLine::OrderItem(item, qty));

                Ok(Self::OnOrder {
                    id: id.clone(),
                    lines: new_lines,
                    start: start.clone(),
                })
            }
            _ => OrderError::invalid_state(),
        }
    }

    pub fn checkout(&self) -> Result<Self> {
        match self {
            Self::OnOrder { id, lines, start } => {
                let billed = lines.iter().fold(Amount::ZERO, |acc, x| {
                    acc + x.subtotal().unwrap_or(Amount::ZERO)
                });

                if billed.is_zero() {
                    Ok(Self::Paid {
                        id: id.clone(),
                        billed_paid: Amount::ZERO,
                        lines: lines.clone(),
                        settlements: vec![],
                        start: start.clone(),
                    })
                } else {
                    Ok(Self::Checkout {
                        id: id.clone(),
                        billed,
                        lines: lines.clone(),
                        start: start.clone(),
                    })
                }
            }
            _ => OrderError::invalid_state(),
        }
    }

    pub fn settled(&self, payment: PaymentMethod) -> Result<Self> {
        if payment.validate() {
            match self {
                Self::Checkout {
                    id,
                    billed,
                    lines,
                    start,
                } => {
                    let new_paid = payment.actual_paid().unwrap();

                    if new_paid == *billed {
                        Ok(Self::Paid {
                            id: id.clone(),
                            billed_paid: billed.clone(),
                            lines: lines.clone(),
                            settlements: vec![payment.into()],
                            start: start.clone(),
                        })
                    } else if new_paid < *billed {
                        Ok(Self::Paying {
                            id: id.clone(),
                            billed: billed.clone(),
                            paid: new_paid,
                            lines: lines.clone(),
                            settlements: vec![payment.into()],
                            start: start.clone(),
                        })
                    } else {
                        Ok(Self::OverPaying {
                            id: id.clone(),
                            billed: billed.clone(),
                            paid: new_paid,
                            lines: lines.clone(),
                            settlements: vec![payment.into()],
                            start: start.clone(),
                        })
                    }
                }
                Self::Paying {
                    id,
                    billed,
                    paid,
                    lines,
                    settlements,
                    start,
                } => {
                    let balance = billed - paid;
                    let new_paid = payment.actual_paid().unwrap();

                    let mut new_settlements = settlements.clone();
                    new_settlements.push(payment.into());

                    if new_paid == balance {
                        Ok(Self::Paid {
                            id: id.clone(),
                            billed_paid: billed.clone(),
                            lines: lines.clone(),
                            settlements: new_settlements,
                            start: start.clone(),
                        })
                    } else if new_paid < balance {
                        Ok(Self::Paying {
                            id: id.clone(),
                            billed: billed.clone(),
                            paid: paid + new_paid,
                            lines: lines.clone(),
                            settlements: new_settlements,
                            start: start.clone(),
                        })
                    } else {
                        Ok(Self::OverPaying {
                            id: id.clone(),
                            billed: billed.clone(),
                            paid: paid + new_paid,
                            lines: lines.clone(),
                            settlements: new_settlements,
                            start: start.clone(),
                        })
                    }
                }
                _ => OrderError::invalid_state(),
            }
        } else {
            OrderError::invalid_payment(Some(payment))
        }
    }

    pub fn done(&self) -> Result<Self> {
        match self {
            Self::Paid {
                id,
                billed_paid,
                lines,
                settlements,
                start,
            } => Ok(Self::Done {
                id: id.clone(),
                billed_paid: *billed_paid,
                lines: lines.clone(),
                settlements: settlements.clone(),
                start: *start,
                end: now(),
            }),
            _ => OrderError::invalid_state(),
        }
    }

    pub fn refund(&self, refund: RefundMethod) -> Result<Self> {
        if refund.validate() {
            match self {
                Self::OverPaying {
                    id,
                    billed,
                    paid,
                    lines,
                    settlements,
                    start,
                } => {
                    let refunded = refund.actual_refund().unwrap();

                    if refunded > *paid {
                        return OrderError::over_refund(paid.clone(), refunded.clone());
                    }

                    let balance = paid - billed;

                    let mut new_settlements = settlements.clone();
                    new_settlements.push(refund.into());

                    if refunded == balance {
                        Ok(Self::Paid {
                            id: id.clone(),
                            billed_paid: billed.clone(),
                            lines: lines.clone(),
                            settlements: new_settlements,
                            start: start.clone(),
                        })
                    } else if refunded < balance {
                        Ok(Self::OverPaying {
                            id: id.clone(),
                            billed: billed.clone(),
                            paid: paid - refunded,
                            lines: lines.clone(),
                            settlements: new_settlements,
                            start: start.clone(),
                        })
                    } else {
                        Ok(Self::Paying {
                            id: id.clone(),
                            billed: billed.clone(),
                            paid: paid - refunded,
                            lines: lines.clone(),
                            settlements: new_settlements,
                            start: start.clone(),
                        })
                    }
                }
                _ => OrderError::invalid_state(),
            }
        } else {
            OrderError::invalid_refund(Some(refund))
        }
    }

    pub fn cancel(&self) -> Result<Self> {
        match self {
            Self::Empty { id, start } => Ok(Self::CancelledEmpty {
                id: id.clone(),
                start: start.clone(),
                cancelled: now(),
            }),
            Self::OnOrder { id, lines, start } => Ok(Self::Cancelled {
                id: id.clone(),
                lines: lines.clone(),
                start: start.clone(),
                cancelled: now(),
            }),
            _ => OrderError::invalid_state(),
        }
    }

    pub fn balance(&self) -> Option<Amount> {
        match self {
            Self::Checkout { billed, .. } => Some(billed.clone()),
            Self::Paying { billed, paid, .. } | Self::OverPaying { billed, paid, .. } => {
                Some(billed - paid)
            }
            Self::Paid { .. } | Self::Done { .. } => Some(Amount::ZERO),
            _ => None,
        }
    }
}

impl OrderLine {
    pub fn subtotal(&self) -> Option<Amount> {
        match self {
            Self::OrderItem(i, q) => Amount::from_usize(*q).map(|x| x * i.unit_price),
        }
    }
}

impl PaymentMethod {
    pub fn actual_paid(&self) -> Option<Amount> {
        if self.validate() {
            let p = match self {
                Self::Cash { paid, change } => paid - change,
                Self::Credit { paid, .. } => paid.clone(),
            };

            Some(p)
        } else {
            None
        }
    }

    pub fn validate(&self) -> bool {
        match self {
            Self::Cash { paid, change } => {
                *paid > Amount::ZERO && *change >= Amount::ZERO && *paid > *change
            }
            Self::Credit { paid, .. } => *paid > Amount::ZERO,
        }
    }
}

impl From<PaymentMethod> for Settlement {
    fn from(value: PaymentMethod) -> Self {
        Self::Payment(value)
    }
}

impl RefundMethod {
    pub fn actual_refund(&self) -> Option<Amount> {
        if self.validate() {
            let p = match self {
                Self::Cash { refunded } => refunded.clone(),
            };

            Some(p)
        } else {
            None
        }
    }

    pub fn validate(&self) -> bool {
        match self {
            Self::Cash { refunded } => *refunded > Amount::ZERO,
        }
    }
}

impl From<RefundMethod> for Settlement {
    fn from(value: RefundMethod) -> Self {
        Self::Refund(value)
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use core::assert;

    #[test]
    fn line_subtotal() {
        let line = OrderLine::OrderItem(
            Item {
                item_id: "item-1".into(),
                unit_price: dec!(1100),
                method: None,
            },
            2,
        );
        let r = line.subtotal();

        assert!(r.is_some());
        assert_eq!(dec!(2200), r.unwrap());
    }

    #[test]
    fn order_item_to_empty() {
        let item = Item {
            item_id: "item-1".into(),
            unit_price: dec!(500),
            method: None,
        };

        let tid = TransactionId("ord-1".into());

        let state = Order::Empty {
            id: tid.clone(),
            start: now(),
        };

        let r = state.order_item(item.clone(), 1);

        assert!(r.is_ok());

        if let Order::OnOrder { id, lines, .. } = r.unwrap() {
            assert_eq!(tid, id);
            assert_eq!(1, lines.len());

            if let Some(OrderLine::OrderItem(i, q)) = lines.first() {
                assert_eq!(item, *i);
                assert_eq!(1, *q);
            } else {
                assert!(false, "not found OrderItem")
            }
        } else {
            assert!(false, "not OnOrder")
        }
    }

    #[test]
    fn order_item_to_onorder() {
        let item = Item {
            item_id: "item-1".into(),
            unit_price: dec!(500),
            method: None,
        };

        let tid = TransactionId("ord-1".into());

        let state = Order::OnOrder {
            id: tid.clone(),
            lines: vec![OrderLine::OrderItem(
                Item {
                    item_id: "item-0".into(),
                    unit_price: dec!(1000),
                    method: None,
                },
                1,
            )],
            start: now(),
        };

        let r = state.order_item(item.clone(), 2);

        assert!(r.is_ok());

        if let Order::OnOrder { id, lines, .. } = r.unwrap() {
            assert_eq!(tid, id);
            assert_eq!(2, lines.len());

            if let Some(OrderLine::OrderItem(i, q)) = lines.last() {
                assert_eq!(item, *i);
                assert_eq!(2, *q);
            } else {
                assert!(false, "not found OrderItem")
            }
        } else {
            assert!(false, "not OnOrder")
        }
    }

    #[test]
    fn order_item_to_nothing() {
        let item = Item {
            item_id: "item-1".into(),
            unit_price: dec!(500),
            method: None,
        };
        let r = Order::Nothing.order_item(item, 1);

        assert!(r.is_err());
    }

    #[test]
    fn checkout_to_empty() {
        let state = Order::Empty {
            id: TransactionId("ord-1".into()),
            start: now(),
        };

        let r = state.checkout();

        assert!(r.is_err());
    }

    #[test]
    fn checkout_to_onorder() {
        let tid = TransactionId("t1".into());
        let lines = vec![
            OrderLine::OrderItem(
                Item {
                    item_id: "item-A".into(),
                    unit_price: dec!(1100),
                    method: None,
                },
                3,
            ),
            OrderLine::OrderItem(
                Item {
                    item_id: "item-B".into(),
                    unit_price: dec!(220),
                    method: None,
                },
                2,
            ),
        ];

        let state = Order::OnOrder {
            id: tid.clone(),
            lines: lines.clone(),
            start: now(),
        };

        let r = state.checkout();

        if let Ok(Order::Checkout {
            id,
            billed,
            lines: r_lines,
            ..
        }) = r
        {
            assert_eq!(tid, id);
            assert_eq!(dec!(3740), billed);
            assert_eq!(lines, r_lines);
        } else {
            assert!(false, "not checkout");
        }
    }

    #[test]
    fn checkout_to_onorder_with_zero_billed() {
        let tid = TransactionId("t1".into());
        let lines = vec![OrderLine::OrderItem(
            Item {
                item_id: "item-A".into(),
                unit_price: dec!(0),
                method: None,
            },
            1,
        )];

        let state = Order::OnOrder {
            id: tid.clone(),
            lines: lines.clone(),
            start: now(),
        };

        let r = state.checkout();

        if let Ok(Order::Paid {
            id,
            billed_paid,
            lines: r_lines,
            settlements,
            ..
        }) = r.clone()
        {
            assert_eq!(tid, id);
            assert_eq!(dec!(0), billed_paid);
            assert_eq!(lines, r_lines);
            assert!(settlements.is_empty());
        } else {
            assert!(false, "not paid");
        }
    }

    #[test]
    fn settled_to_onorder() {
        let state = Order::OnOrder {
            id: TransactionId("ord-1".into()),
            lines: vec![OrderLine::OrderItem(
                Item {
                    item_id: "item-0".into(),
                    unit_price: dec!(1000),
                    method: None,
                },
                1,
            )],
            start: now(),
        };

        let r = state.settled(PaymentMethod::Cash {
            paid: dec!(5000),
            change: dec!(4000),
        });

        assert!(r.is_err());
    }

    #[test]
    fn settled_to_checkout_under() {
        let tid = TransactionId("ord-1".into());
        let lines = vec![OrderLine::OrderItem(
            Item {
                item_id: "item-1".into(),
                unit_price: dec!(1100),
                method: None,
            },
            2,
        )];

        let start = now();

        let state = Order::Checkout {
            id: tid.clone(),
            billed: dec!(2200),
            lines: lines.clone(),
            start: start.clone(),
        };

        let payment = PaymentMethod::Cash {
            paid: dec!(1000),
            change: dec!(0),
        };

        let r = state.settled(payment.clone());

        assert!(r.is_ok());

        assert_eq!(Some(dec!(1200)), r.clone().unwrap().balance());

        if let Ok(Order::Paying {
            id,
            billed,
            paid,
            lines: r_lines,
            settlements,
            start: r_start,
        }) = r
        {
            assert_eq!(tid, id);
            assert_eq!(dec!(2200), billed);
            assert_eq!(dec!(1000), paid);
            assert_eq!(lines, r_lines);
            assert_eq!(vec![Settlement::Payment(payment)], settlements);
            assert_eq!(start, r_start);
        } else {
            assert!(false, "not paying");
        }
    }

    #[test]
    fn settled_to_checkout_over() {
        let tid = TransactionId("ord-1".into());
        let lines = vec![OrderLine::OrderItem(
            Item {
                item_id: "item-1".into(),
                unit_price: dec!(1100),
                method: None,
            },
            2,
        )];

        let state = Order::Checkout {
            id: tid.clone(),
            billed: dec!(2200),
            lines: lines.clone(),
            start: now(),
        };

        let payment = PaymentMethod::Cash {
            paid: dec!(2500),
            change: dec!(0),
        };

        let r = state.settled(payment.clone());

        assert_eq!(Some(dec!(-300)), r.clone().unwrap().balance());

        if let Ok(Order::OverPaying {
            id,
            billed,
            paid,
            lines: r_lines,
            settlements,
            ..
        }) = r
        {
            assert_eq!(tid, id);
            assert_eq!(dec!(2200), billed);
            assert_eq!(dec!(2500), paid);
            assert_eq!(lines, r_lines);
            assert_eq!(vec![Settlement::Payment(payment)], settlements);
        } else {
            assert!(false, "not overpaying");
        }
    }

    #[test]
    fn settled_to_checkout_just() {
        let tid = TransactionId("ord-1".into());

        let state = Order::Checkout {
            id: tid.clone(),
            billed: dec!(2200),
            lines: vec![OrderLine::OrderItem(
                Item {
                    item_id: "item-1".into(),
                    unit_price: dec!(1100),
                    method: None,
                },
                2,
            )],
            start: now(),
        };

        let payment = PaymentMethod::Credit {
            paid: dec!(2200),
            payment_id: "p1".into(),
        };
        let r = state.settled(payment.clone());

        if let Ok(Order::Paid {
            id,
            billed_paid,
            settlements,
            ..
        }) = r.clone()
        {
            assert_eq!(tid, id);
            assert_eq!(dec!(2200), billed_paid);
            assert_eq!(vec![Settlement::Payment(payment)], settlements);
        } else {
            assert!(false, "not paid");
        }

        assert_eq!(Some(dec!(0)), r.unwrap().balance());
    }

    #[test]
    fn settled_to_paying_under() {
        let tid = TransactionId("ord-1".into());
        let lines = vec![
            OrderLine::OrderItem(
                Item {
                    item_id: "item-1".into(),
                    unit_price: dec!(1100),
                    method: None,
                },
                2,
            ),
            OrderLine::OrderItem(
                Item {
                    item_id: "item-2".into(),
                    unit_price: dec!(330),
                    method: None,
                },
                1,
            ),
        ];

        let state = Order::Paying {
            id: tid.clone(),
            billed: dec!(2530),
            paid: dec!(1000),
            lines: lines.clone(),
            settlements: vec![
                PaymentMethod::Credit {
                    paid: dec!(1000),
                    payment_id: "p1".into(),
                }
                .into(),
            ],
            start: now(),
        };

        let payment = PaymentMethod::Cash {
            paid: dec!(1500),
            change: dec!(0),
        };

        let r = state.settled(payment.clone());

        assert!(r.is_ok());

        if let Ok(Order::Paying {
            id,
            billed,
            paid,
            lines: r_lines,
            settlements,
            ..
        }) = r.clone()
        {
            assert_eq!(tid, id);
            assert_eq!(dec!(2530), billed);
            assert_eq!(dec!(2500), paid);
            assert_eq!(lines, r_lines);
            assert_eq!(2, settlements.len());
            assert_eq!(Some(&Settlement::Payment(payment)), settlements.last());
        } else {
            assert!(false, "not paying");
        }

        assert_eq!(Some(dec!(30)), r.unwrap().balance());
    }

    #[test]
    fn settled_to_paying_just() {
        let tid = TransactionId("ord-1".into());
        let lines = vec![
            OrderLine::OrderItem(
                Item {
                    item_id: "item-1".into(),
                    unit_price: dec!(1100),
                    method: None,
                },
                2,
            ),
            OrderLine::OrderItem(
                Item {
                    item_id: "item-2".into(),
                    unit_price: dec!(330),
                    method: None,
                },
                1,
            ),
        ];

        let state = Order::Paying {
            id: tid.clone(),
            billed: dec!(2530),
            paid: dec!(1000),
            lines: lines.clone(),
            settlements: vec![
                PaymentMethod::Credit {
                    paid: dec!(1000),
                    payment_id: "p1".into(),
                }
                .into(),
            ],
            start: now(),
        };

        let payment = PaymentMethod::Cash {
            paid: dec!(1530),
            change: dec!(0),
        };

        let r = state.settled(payment.clone());

        assert!(r.is_ok());

        if let Ok(Order::Paid {
            id,
            billed_paid,
            lines: r_lines,
            settlements,
            ..
        }) = r.clone()
        {
            assert_eq!(tid, id);
            assert_eq!(dec!(2530), billed_paid);
            assert_eq!(lines, r_lines);
            assert_eq!(2, settlements.len());
            assert_eq!(Some(&Settlement::Payment(payment)), settlements.last());
        } else {
            assert!(false, "not paying");
        }

        assert_eq!(Some(dec!(0)), r.unwrap().balance());
    }

    #[test]
    fn settled_to_paying_over() {
        let tid = TransactionId("ord-1".into());
        let lines = vec![
            OrderLine::OrderItem(
                Item {
                    item_id: "item-1".into(),
                    unit_price: dec!(1100),
                    method: None,
                },
                2,
            ),
            OrderLine::OrderItem(
                Item {
                    item_id: "item-2".into(),
                    unit_price: dec!(330),
                    method: None,
                },
                1,
            ),
        ];

        let state = Order::Paying {
            id: tid.clone(),
            billed: dec!(2530),
            paid: dec!(1000),
            lines: lines.clone(),
            settlements: vec![
                PaymentMethod::Credit {
                    paid: dec!(1000),
                    payment_id: "p1".into(),
                }
                .into(),
            ],
            start: now(),
        };

        let payment = PaymentMethod::Cash {
            paid: dec!(1600),
            change: dec!(0),
        };

        let r = state.settled(payment.clone());

        assert_eq!(Some(dec!(-70)), r.clone().unwrap().balance());

        if let Ok(Order::OverPaying {
            id,
            billed,
            paid,
            lines: r_lines,
            settlements,
            ..
        }) = r
        {
            assert_eq!(tid, id);
            assert_eq!(dec!(2530), billed);
            assert_eq!(dec!(2600), paid);
            assert_eq!(lines, r_lines);
            assert_eq!(Some(&Settlement::Payment(payment)), settlements.last());
        } else {
            assert!(false, "not overpaying");
        }
    }

    #[test]
    fn settled_invalid_payment() {
        let tid = TransactionId("ord-1".into());
        let lines = vec![OrderLine::OrderItem(
            Item {
                item_id: "item-1".into(),
                unit_price: dec!(1100),
                method: None,
            },
            2,
        )];

        let state = Order::Checkout {
            id: tid.clone(),
            billed: dec!(2200),
            lines: lines.clone(),
            start: now(),
        };

        assert!(
            state
                .settled(PaymentMethod::Cash {
                    paid: dec!(500),
                    change: dec!(1000)
                })
                .is_err()
        );
        assert!(
            state
                .settled(PaymentMethod::Cash {
                    paid: dec!(500),
                    change: dec!(500)
                })
                .is_err()
        );
        assert!(
            state
                .settled(PaymentMethod::Credit {
                    paid: dec!(-100),
                    payment_id: "p123".into()
                })
                .is_err()
        );
        assert!(
            state
                .settled(PaymentMethod::Credit {
                    paid: dec!(0),
                    payment_id: "p123".into()
                })
                .is_err()
        );
    }

    #[test]
    fn done_to_paying() {
        let tid = TransactionId("ord-1".into());
        let lines = vec![
            OrderLine::OrderItem(
                Item {
                    item_id: "item-1".into(),
                    unit_price: dec!(1100),
                    method: None,
                },
                2,
            ),
            OrderLine::OrderItem(
                Item {
                    item_id: "item-2".into(),
                    unit_price: dec!(330),
                    method: None,
                },
                1,
            ),
        ];

        let state = Order::Paying {
            id: tid.clone(),
            billed: dec!(2530),
            paid: dec!(1000),
            lines: lines.clone(),
            settlements: vec![
                PaymentMethod::Credit {
                    paid: dec!(1000),
                    payment_id: "p1".into(),
                }
                .into(),
            ],
            start: now(),
        };

        assert!(state.done().is_err());
    }

    #[test]
    fn done_to_paid() {
        let tid = TransactionId("ord-1".into());
        let lines = vec![
            OrderLine::OrderItem(
                Item {
                    item_id: "item-1".into(),
                    unit_price: dec!(1100),
                    method: None,
                },
                2,
            ),
            OrderLine::OrderItem(
                Item {
                    item_id: "item-2".into(),
                    unit_price: dec!(330),
                    method: None,
                },
                1,
            ),
        ];

        let payments = vec![
            PaymentMethod::Cash {
                paid: dec!(3000),
                change: dec!(470),
            }
            .into(),
        ];

        let state = Order::Paid {
            id: tid.clone(),
            billed_paid: dec!(2530),
            lines: lines.clone(),
            settlements: payments.clone(),
            start: now(),
        };

        let r = state.done();

        if let Ok(Order::Done {
            id,
            billed_paid,
            lines: r_lines,
            settlements,
            start,
            end,
        }) = r
        {
            assert_eq!(tid, id);
            assert_eq!(dec!(2530), billed_paid);
            assert_eq!(lines, r_lines);
            assert_eq!(payments, settlements);
            assert!(start < end);
        } else {
            assert!(false, "not done");
        }
    }

    #[test]
    fn validate_payment() {
        assert!(
            PaymentMethod::Cash {
                paid: dec!(1000),
                change: dec!(0)
            }
            .validate()
        );
        assert!(
            PaymentMethod::Cash {
                paid: dec!(1050),
                change: dec!(50)
            }
            .validate()
        );
        assert!(
            PaymentMethod::Credit {
                paid: dec!(100),
                payment_id: "p1".into()
            }
            .validate()
        );
    }

    #[test]
    fn invalidate_payment() {
        assert_eq!(
            false,
            PaymentMethod::Cash {
                paid: dec!(0),
                change: dec!(0)
            }
            .validate()
        );
        assert_eq!(
            false,
            PaymentMethod::Cash {
                paid: dec!(100),
                change: dec!(200)
            }
            .validate()
        );
        assert_eq!(
            false,
            PaymentMethod::Cash {
                paid: dec!(-100),
                change: dec!(0)
            }
            .validate()
        );
        assert_eq!(
            false,
            PaymentMethod::Cash {
                paid: dec!(500),
                change: dec!(-100)
            }
            .validate()
        );
        assert_eq!(
            false,
            PaymentMethod::Cash {
                paid: dec!(100),
                change: dec!(100)
            }
            .validate()
        );

        assert_eq!(
            false,
            PaymentMethod::Credit {
                paid: dec!(0),
                payment_id: "p1".into()
            }
            .validate()
        );
        assert_eq!(
            false,
            PaymentMethod::Credit {
                paid: dec!(-100),
                payment_id: "p1".into()
            }
            .validate()
        );
    }

    #[test]
    fn refund_to_paying() {
        let tid = TransactionId("ord-1".into());
        let lines = vec![
            OrderLine::OrderItem(
                Item {
                    item_id: "item-1".into(),
                    unit_price: dec!(1100),
                    method: None,
                },
                2,
            ),
            OrderLine::OrderItem(
                Item {
                    item_id: "item-2".into(),
                    unit_price: dec!(330),
                    method: None,
                },
                1,
            ),
        ];

        let state = Order::Paying {
            id: tid.clone(),
            billed: dec!(2530),
            paid: dec!(1000),
            lines: lines.clone(),
            settlements: vec![
                PaymentMethod::Credit {
                    paid: dec!(1000),
                    payment_id: "p1".into(),
                }
                .into(),
            ],
            start: now(),
        };

        let refund = RefundMethod::Cash {
            refunded: dec!(100),
        };

        let r = state.refund(refund);

        assert!(r.is_err());
    }

    #[test]
    fn refund_under() {
        let tid = TransactionId("ord-1".into());
        let lines = vec![
            OrderLine::OrderItem(
                Item {
                    item_id: "item-1".into(),
                    unit_price: dec!(1100),
                    method: None,
                },
                2,
            ),
            OrderLine::OrderItem(
                Item {
                    item_id: "item-2".into(),
                    unit_price: dec!(330),
                    method: None,
                },
                1,
            ),
        ];

        let state = Order::OverPaying {
            id: tid.clone(),
            billed: dec!(2530),
            paid: dec!(3000),
            lines: lines.clone(),
            settlements: vec![
                PaymentMethod::Cash {
                    paid: dec!(3000),
                    change: dec!(0),
                }
                .into(),
            ],
            start: now(),
        };

        let refund = RefundMethod::Cash {
            refunded: dec!(300),
        };

        let r = state.refund(refund.clone());

        assert_eq!(Some(dec!(-170)), r.clone().unwrap().balance());

        if let Ok(Order::OverPaying {
            id,
            billed,
            paid,
            lines: r_lines,
            settlements,
            ..
        }) = r
        {
            assert_eq!(tid, id);
            assert_eq!(dec!(2530), billed);
            assert_eq!(dec!(2700), paid);
            assert_eq!(lines, r_lines);
            assert_eq!(Some(&Settlement::Refund(refund)), settlements.last());
        } else {
            assert!(false, "not overpaying");
        }
    }

    #[test]
    fn refund_just() {
        let tid = TransactionId("ord-1".into());
        let lines = vec![
            OrderLine::OrderItem(
                Item {
                    item_id: "item-1".into(),
                    unit_price: dec!(1100),
                    method: None,
                },
                2,
            ),
            OrderLine::OrderItem(
                Item {
                    item_id: "item-2".into(),
                    unit_price: dec!(330),
                    method: None,
                },
                1,
            ),
        ];

        let state = Order::OverPaying {
            id: tid.clone(),
            billed: dec!(2530),
            paid: dec!(3000),
            lines: lines.clone(),
            settlements: vec![
                PaymentMethod::Cash {
                    paid: dec!(3000),
                    change: dec!(0),
                }
                .into(),
            ],
            start: now(),
        };

        let refund = RefundMethod::Cash {
            refunded: dec!(470),
        };

        let r = state.refund(refund.clone());

        assert_eq!(Some(dec!(0)), r.clone().unwrap().balance());

        if let Ok(Order::Paid {
            id,
            billed_paid,
            lines: r_lines,
            settlements,
            ..
        }) = r
        {
            assert_eq!(tid, id);
            assert_eq!(dec!(2530), billed_paid);
            assert_eq!(lines, r_lines);
            assert_eq!(Some(&Settlement::Refund(refund)), settlements.last());
        } else {
            assert!(false, "not paid");
        }
    }

    #[test]
    fn refund_over() {
        let tid = TransactionId("ord-1".into());
        let lines = vec![
            OrderLine::OrderItem(
                Item {
                    item_id: "item-1".into(),
                    unit_price: dec!(1100),
                    method: None,
                },
                2,
            ),
            OrderLine::OrderItem(
                Item {
                    item_id: "item-2".into(),
                    unit_price: dec!(330),
                    method: None,
                },
                1,
            ),
        ];

        let state = Order::OverPaying {
            id: tid.clone(),
            billed: dec!(2530),
            paid: dec!(3000),
            lines: lines.clone(),
            settlements: vec![
                PaymentMethod::Cash {
                    paid: dec!(3000),
                    change: dec!(0),
                }
                .into(),
            ],
            start: now(),
        };

        let refund = RefundMethod::Cash {
            refunded: dec!(500),
        };

        let r = state.refund(refund.clone());

        assert_eq!(Some(dec!(30)), r.clone().unwrap().balance());

        if let Ok(Order::Paying {
            id,
            billed,
            paid,
            lines: r_lines,
            settlements,
            ..
        }) = r
        {
            assert_eq!(tid, id);
            assert_eq!(dec!(2530), billed);
            assert_eq!(dec!(2500), paid);
            assert_eq!(lines, r_lines);
            assert_eq!(Some(&Settlement::Refund(refund)), settlements.last());
        } else {
            assert!(false, "not paying");
        }
    }

    #[test]
    fn refund_over_paid() {
        let tid = TransactionId("ord-1".into());
        let lines = vec![
            OrderLine::OrderItem(
                Item {
                    item_id: "item-1".into(),
                    unit_price: dec!(1100),
                    method: None,
                },
                2,
            ),
            OrderLine::OrderItem(
                Item {
                    item_id: "item-2".into(),
                    unit_price: dec!(330),
                    method: None,
                },
                1,
            ),
        ];

        let state = Order::OverPaying {
            id: tid.clone(),
            billed: dec!(2530),
            paid: dec!(3000),
            lines: lines.clone(),
            settlements: vec![
                PaymentMethod::Cash {
                    paid: dec!(3000),
                    change: dec!(0),
                }
                .into(),
            ],
            start: now(),
        };

        let refund = RefundMethod::Cash {
            refunded: dec!(3500),
        };

        let r = state.refund(refund.clone());

        assert!(r.is_err());
    }

    #[test]
    fn refund_nagative() {
        let tid = TransactionId("ord-1".into());
        let lines = vec![
            OrderLine::OrderItem(
                Item {
                    item_id: "item-1".into(),
                    unit_price: dec!(1100),
                    method: None,
                },
                2,
            ),
            OrderLine::OrderItem(
                Item {
                    item_id: "item-2".into(),
                    unit_price: dec!(330),
                    method: None,
                },
                1,
            ),
        ];

        let state = Order::OverPaying {
            id: tid.clone(),
            billed: dec!(2530),
            paid: dec!(3000),
            lines: lines.clone(),
            settlements: vec![
                PaymentMethod::Cash {
                    paid: dec!(3000),
                    change: dec!(0),
                }
                .into(),
            ],
            start: now(),
        };

        let refund = RefundMethod::Cash {
            refunded: dec!(-100),
        };

        let r = state.refund(refund.clone());

        assert!(r.is_err());
    }

    #[test]
    fn refund_zero() {
        let tid = TransactionId("ord-1".into());
        let lines = vec![
            OrderLine::OrderItem(
                Item {
                    item_id: "item-1".into(),
                    unit_price: dec!(1100),
                    method: None,
                },
                2,
            ),
            OrderLine::OrderItem(
                Item {
                    item_id: "item-2".into(),
                    unit_price: dec!(330),
                    method: None,
                },
                1,
            ),
        ];

        let state = Order::OverPaying {
            id: tid.clone(),
            billed: dec!(2530),
            paid: dec!(3000),
            lines: lines.clone(),
            settlements: vec![
                PaymentMethod::Cash {
                    paid: dec!(3000),
                    change: dec!(0),
                }
                .into(),
            ],
            start: now(),
        };

        let refund = RefundMethod::Cash { refunded: dec!(0) };

        let r = state.refund(refund.clone());

        assert!(r.is_err());
    }

    #[test]
    fn cancel_empy() {
        let state = Order::Empty {
            id: TransactionId("t1".into()),
            start: now(),
        };

        let r = state.cancel();

        assert!(r.is_ok());

        if let Order::CancelledEmpty {
            id,
            start,
            cancelled,
        } = r.unwrap()
        {
            assert_eq!(TransactionId("t1".into()), id);
            assert!(cancelled > start);
        } else {
            assert!(false, "not cancelledempty");
        }
    }

    #[test]
    fn cancel_onorder() {
        let lines = vec![OrderLine::OrderItem(
            Item {
                item_id: "item-0".into(),
                unit_price: dec!(1000),
                method: None,
            },
            1,
        )];

        let state = Order::OnOrder {
            id: TransactionId("ord-1".into()),
            lines: lines.clone(),
            start: now(),
        };

        let r = state.cancel();

        assert!(r.is_ok());

        if let Order::Cancelled {
            id,
            lines: r_lines,
            start,
            cancelled,
        } = r.unwrap()
        {
            assert_eq!(TransactionId("ord-1".into()), id);
            assert_eq!(lines, r_lines);
            assert!(cancelled > start);
        } else {
            assert!(false, "not cancelled")
        }
    }

    #[test]
    fn cancel_other() {
        assert!(Order::Nothing.cancel().is_err());

        assert!(
            Order::Checkout {
                id: TransactionId("t1".into()),
                billed: dec!(100),
                lines: vec![OrderLine::OrderItem(
                    Item {
                        item_id: "item-1".into(),
                        unit_price: dec!(100),
                        method: None
                    },
                    1
                )],
                start: now()
            }
            .cancel()
            .is_err()
        );
    }
}
