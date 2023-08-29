#![allow(dead_code)]
#![allow(private_in_public)]

use super::amount::{Amount, Value};
use super::tax::SalesTax;

#[derive(Debug, Clone, PartialEq)]
struct PriceAmount(Amount);

#[derive(Debug, Clone, PartialEq)]
pub enum Price {
    ExcludeTax(PriceAmount),
    IncludeTax(PriceAmount, SalesTax),
}

impl Price {
    pub fn new(price: Value) -> Option<Self> {
        if price >= Value::default() {
            Some(Self::ExcludeTax(PriceAmount(Amount::Fixed(price))))
        } else {
            None
        }
    }

    pub fn new_with_tax(price: Value, tax_rate: u8) -> Option<Self> {
        if price >= Value::default() {
            SalesTax::new(tax_rate).map(|t| Self::IncludeTax(PriceAmount(Amount::Fixed(price)), t))
        } else {
            None
        }
    }

    pub fn price_total(&self) -> Value {
        self.price_without_tax() + self.tax().unwrap_or_default()
    }

    pub fn price_without_tax(&self) -> Value {
        match self {
            Self::ExcludeTax(p) | Self::IncludeTax(p, _) => p.0.amount(None).unwrap_or_default(),
        }
    }

    pub fn tax(&self) -> Option<Value> {
        match self {
            Self::ExcludeTax(_) => None,
            Self::IncludeTax(r, t) => r.0.amount(None).map(|a| t.tax(&a)),
        }
    }
}

#[cfg(test)]
mod tests {
    use num_bigint::BigInt;

    use super::*;

    #[test]
    fn new() {
        let r = Price::new(to_value(100));

        if let Some(Price::ExcludeTax(p)) = r {
            assert_eq!(p.0, Amount::Fixed(to_value(100)));
        } else {
            assert!(false);
        }
    }

    #[test]
    fn new_by_negative() {
        let r = Price::new(to_value(-1));

        assert!(r.is_none());
    }

    #[test]
    fn new_with_tax() {
        let r = Price::new_with_tax(to_value(100), 10);

        if let Some(Price::IncludeTax(p, t)) = r {
            assert_eq!(p.0, Amount::Fixed(to_value(100)));
            assert_eq!(*t.rate().numer(), BigInt::from(1));
        } else {
            assert!(false);
        }
    }

    #[test]
    fn new_with_tax_by_negative() {
        let r = Price::new_with_tax(to_value(-1), 10);

        assert!(r.is_none());
    }

    #[test]
    fn price_without_tax_exclude() {
        let p = Price::new(to_value(200)).unwrap();

        assert_eq!(p.price_without_tax(), to_value(200));
    }

    #[test]
    fn price_without_tax_include() {
        let p = Price::new_with_tax(to_value(200), 10).unwrap();

        assert_eq!(p.price_without_tax(), to_value(200));
    }

    #[test]
    fn price_total_exclude() {
        let p = Price::new(to_value(200)).unwrap();

        assert_eq!(p.price_total(), to_value(200));
    }

    #[test]
    fn price_total_include() {
        let p = Price::new_with_tax(to_value(200), 10).unwrap();

        assert_eq!(p.price_total(), to_value(220));
    }

    #[test]
    fn tax_exclude() {
        let p = Price::new(to_value(200)).unwrap();

        assert!(p.tax().is_none());
    }

    #[test]
    fn tax_include() {
        let p = Price::new_with_tax(to_value(200), 10).unwrap();

        assert_eq!(p.tax(), Some(to_value(20)));
    }

    fn to_value(v: i32) -> Value {
        Value::from(BigInt::from(v))
    }
}
