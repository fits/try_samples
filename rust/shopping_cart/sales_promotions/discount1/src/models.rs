#![allow(dead_code)]

pub type Amount = i32;

#[derive(Debug, Clone, PartialEq)]
pub enum Rounding {
    Ceil,
    Floor,
    Round,
}

#[derive(Debug, Clone, PartialEq)]
enum DiscountMethod {
    Fix(Amount),
    Diff(Amount),
    Rate(Amount, Rounding),
}

#[derive(Debug, Clone)]
pub struct Discount(DiscountMethod);

impl Discount {
    pub fn fix(value: Amount) -> Self {
        Self(DiscountMethod::Fix(value.max(0)))
    }

    pub fn diff(value: Amount) -> Self {
        Self(DiscountMethod::Diff(value.max(0)))
    }

    pub fn rate(value: Amount) -> Self {
        Self::rate_with(value, Rounding::Ceil)
    }

    pub fn rate_with(value: Amount, rounding: Rounding) -> Self {
        let v = value.max(0);

        Self(DiscountMethod::Rate(if v > 100 { 0 } else { v }, rounding))
    }

    pub fn discount(&self, price: &Amount) -> Amount {
        let p = price.max(&0);

        match &self.0 {
            DiscountMethod::Fix(v) => p.min(&v).clone(),
            DiscountMethod::Diff(v) => (p - v).max(0),
            DiscountMethod::Rate(v, r) => {
                let d = ((p * v) as f32) / 100.0;

                match r {
                    Rounding::Ceil => d.ceil() as i32,
                    Rounding::Round => d.round() as i32,
                    Rounding::Floor => d.floor() as i32,
                }
            }
        }
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn fix() {
        let d = Discount::fix(1000);

        if let DiscountMethod::Fix(v) = d.0 {
            assert_eq!(v, 1000);
        } else {
            assert!(false);
        }
    }

    #[test]
    fn fix_negative() {
        let d = Discount::fix(-1000);

        if let DiscountMethod::Fix(v) = d.0 {
            assert_eq!(v, 0);
        } else {
            assert!(false);
        }
    }

    #[test]
    fn diff() {
        let d = Discount::diff(1000);

        if let DiscountMethod::Diff(v) = d.0 {
            assert_eq!(v, 1000);
        } else {
            assert!(false);
        }
    }

    #[test]
    fn diff_negative() {
        let d = Discount::diff(-1000);

        if let DiscountMethod::Diff(v) = d.0 {
            assert_eq!(v, 0);
        } else {
            assert!(false);
        }
    }

    #[test]
    fn rate() {
        let d = Discount::rate(20);

        if let DiscountMethod::Rate(v, r) = d.0 {
            assert_eq!(v, 20);
            assert_eq!(r, Rounding::Ceil);
        } else {
            assert!(false);
        }
    }

    #[test]
    fn rate_negatrive() {
        let d = Discount::rate(-20);

        if let DiscountMethod::Rate(v, _) = d.0 {
            assert_eq!(v, 0);
        } else {
            assert!(false);
        }
    }

    #[test]
    fn rate_100() {
        let d = Discount::rate(100);

        if let DiscountMethod::Rate(v, _) = d.0 {
            assert_eq!(v, 100);
        } else {
            assert!(false);
        }
    }

    #[test]
    fn rate_over_100() {
        let d = Discount::rate(101);

        if let DiscountMethod::Rate(v, _) = d.0 {
            assert_eq!(v, 0);
        } else {
            assert!(false);
        }
    }

    #[test]
    fn discount_fix() {
        let p = 1000;

        let d = Discount::fix(100);
        let r = d.discount(&p);

        assert_eq!(r, 100);
    }

    #[test]
    fn discount_fix_over_price() {
        let p = 350;

        let d = Discount::fix(500);
        let r = d.discount(&p);

        assert_eq!(r, 350);
    }

    #[test]
    fn discount_fix_negative_price() {
        let p = -100;

        let d = Discount::fix(500);
        let r = d.discount(&p);

        assert_eq!(r, 0);
    }

    #[test]
    fn discount_diff() {
        let p = 1300;

        let d = Discount::diff(1000);
        let r = d.discount(&p);

        assert_eq!(r, 300);
    }

    #[test]
    fn discount_diff_lower_price() {
        let p = 900;

        let d = Discount::diff(1000);
        let r = d.discount(&p);

        assert_eq!(r, 0);
    }

    #[test]
    fn discount_rate_ceil() {
        let p = 1204;

        let d = Discount::rate(10);
        let r = d.discount(&p);

        assert_eq!(r, 121);
    }

    #[test]
    fn discount_rate_ceil2() {
        let p = 1204;

        let d = Discount::rate(2);
        let r = d.discount(&p);

        assert_eq!(r, 25);
    }

    #[test]
    fn discount_rate_floor() {
        let p = 1205;

        let d = Discount::rate_with(10, Rounding::Floor);
        let r = d.discount(&p);

        assert_eq!(r, 120);
    }

    #[test]
    fn discount_rate_round() {
        let p = 1205;

        let d = Discount::rate_with(10, Rounding::Round);
        let r = d.discount(&p);

        assert_eq!(r, 121);
    }

    #[test]
    fn discount_rate_zero() {
        let p = 1204;

        let d = Discount::rate(0);
        let r = d.discount(&p);

        assert_eq!(r, 0);
    }

}