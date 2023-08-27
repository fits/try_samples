#![allow(dead_code)]

use num_rational::BigRational;

pub type Value = BigRational;

#[derive(Debug, Clone, PartialEq)]
pub enum Amount {
    Fixed(Value),
    Rate(Value),
    Diff(Value),
}

impl Amount {
    pub fn amount(&self, target: Option<Value>) -> Option<Value> {
        match self {
            Self::Fixed(v) => Some(v.clone()),
            Self::Rate(r) => target.map(|t| t * r),
            Self::Diff(v) => target.map(|t| v - t),
        }
    }
}

#[cfg(test)]
mod tests {
    use num_bigint::BigInt;

    use super::*;

    #[test]
    fn fixed_amount_without_target() {
        let a = Amount::Fixed(to_value(100));

        if let Some(v) = a.amount(None) {
            assert_eq!(v, to_value(100));
        } else {
            assert!(false);
        }
    }

    #[test]
    fn fixed_amount_with_target() {
        let a = Amount::Fixed(to_value(100));

        if let Some(v) = a.amount(Some(to_value(200))) {
            assert_eq!(v, to_value(100));
        } else {
            assert!(false);
        }
    }

    #[test]
    fn rate_amount_without_target() {
        let a = Amount::Rate(to_rate(30));

        assert!(a.amount(None).is_none());
    }

    #[test]
    fn rate_amount_with_target() {
        let a = Amount::Rate(to_rate(30));

        if let Some(v) = a.amount(Some(to_value(200))) {
            assert_eq!(v, to_value(60));
        } else {
            assert!(false);
        }
    }

    #[test]
    fn diff_amount_without_target() {
        let a = Amount::Diff(to_value(100));
        assert!(a.amount(None).is_none());
    }

    #[test]
    fn diff_amount_with_small_target() {
        let a = Amount::Diff(to_value(100));

        if let Some(v) = a.amount(Some(to_value(85))) {
            assert_eq!(v, to_value(15));
        } else {
            assert!(false);
        }
    }

    #[test]
    fn diff_amount_with_big_target() {
        let a = Amount::Diff(to_value(100));

        if let Some(v) = a.amount(Some(to_value(123))) {
            assert_eq!(v, to_value(-23));
        } else {
            assert!(false);
        }
    }

    fn to_value(v: i32) -> Value {
        Value::from(BigInt::from(v))
    }

    fn to_rate(v: u8) -> Value {
        Value::new(v.into(), 100.into())
    }
}
