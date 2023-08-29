#![allow(dead_code)]

use num_bigint::BigInt;

use super::amount::{Amount, Value};

#[derive(Debug, Clone, PartialEq)]
struct TaxRate(Amount);

#[derive(Debug, Clone, PartialEq)]
pub struct SalesTax(TaxRate);

impl SalesTax {
    pub fn new(rate: u8) -> Option<Self> {
        if rate <= 100 {
            Some(Self(TaxRate(Amount::Rate(Value::new(
                rate.into(),
                100.into(),
            )))))
        } else {
            None
        }
    }

    pub fn tax(&self, price: &Value) -> Value {
        self.0 .0.amount(Some(price)).unwrap_or_default()
    }

    pub fn tax_from_including(&self, price_inc_tax: &Value) -> Value {
        match &self.0 .0 {
            Amount::Rate(r) => (r * price_inc_tax) / (r + BigInt::from(1)),
            _ => Value::default(),
        }
    }

    pub fn rate(&self) -> Value {
        match &self.0 .0 {
            Amount::Rate(r) => r.clone(),
            _ => Value::default(),
        }
    }
}

#[cfg(test)]
mod tests {
    use num_bigint::BigInt;

    use super::*;

    #[test]
    fn new() {
        let t = SalesTax::new(25);

        if let Some(SalesTax(TaxRate(rate))) = t {
            assert_eq!(rate, to_rate(25));
        } else {
            assert!(false);
        }
    }

    #[test]
    fn new_with_over100() {
        let t = SalesTax::new(101);

        assert!(t.is_none());
    }

    #[test]
    fn tax() {
        let t = SalesTax::new(8).unwrap();

        assert_eq!(t.tax(&to_value(100)), to_value(8));
    }

    #[test]
    fn tax_from_including() {
        let t = SalesTax::new(8).unwrap();

        assert_eq!(t.tax_from_including(&to_value(108)), to_value(8));
    }

    #[test]
    fn rate() {
        let t = SalesTax::new(8).unwrap();

        assert_eq!(t.rate(), to_value(8) / BigInt::from(100));
    }

    fn to_value(v: i32) -> Value {
        Value::from(BigInt::from(v))
    }

    fn to_rate(v: u8) -> Amount {
        Amount::Rate(Value::new(v.into(), 100.into()))
    }
}
