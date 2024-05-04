#![allow(dead_code)]

pub trait Item<Value, Amount> {
    fn attrs(&self, name: &String) -> Option<&Value>;
    fn amount(&self, name: &Option<String>) -> Option<&Amount>;
}

pub enum Cond<Value, Amount> {
    PropEq { name: String, value: Value },
    PropIn { name: String, values: Vec<Value> },
    AmountMin { name: Option<String>, value: Amount },
    AmountMax { name: Option<String>, value: Amount },
    Not(Box<Cond<Value, Amount>>),
    And(Box<Cond<Value, Amount>>, Box<Cond<Value, Amount>>),
    Or(Box<Cond<Value, Amount>>, Box<Cond<Value, Amount>>),
}

pub trait Predicate<'a, T> {
    fn check(&self, d: &'a T) -> bool;
}

impl<'a, V, P, T> Predicate<'a, T> for Cond<V, P>
where
    V: PartialEq,
    P: PartialOrd,
    T: Item<V, P>,
{
    fn check(&self, d: &'a T) -> bool {
        match self {
            Self::PropEq { name, value } => d.attrs(name).map(|v| v == value).unwrap_or(false),
            Self::PropIn { name, values } => {
                d.attrs(name).map(|v| values.contains(v)).unwrap_or(false)
            }
            Self::AmountMin { name, value } => d.amount(name).map(|v| v >= value).unwrap_or(false),
            Self::AmountMax { name, value } => d.amount(name).map(|v| v <= value).unwrap_or(false),
            Self::Not(c) => !c.check(d),
            Self::And(a, b) => a.check(d) && b.check(d),
            Self::Or(a, b) => a.check(d) || b.check(d),
        }
    }
}

#[cfg(test)]
mod tests {
    use std::collections::HashMap;

    use super::*;

    type TestPrice = u32;
    type TestCond = Cond<String, TestPrice>;

    struct TestItem(HashMap<String, String>, TestPrice);

    impl Item<String, TestPrice> for TestItem {
        fn attrs(&self, name: &String) -> Option<&String> {
            self.0.get(name)
        }

        fn amount(&self, _name: &Option<String>) -> Option<&TestPrice> {
            Some(&self.1)
        }
    }

    #[test]
    fn eq_match() {
        let d = test_item();

        let cond = TestCond::PropEq {
            name: "category".into(),
            value: "A".into(),
        };

        let r = cond.check(&d);

        assert!(r);
    }

    #[test]
    fn eq_unmatch() {
        let d = test_item();

        let cond = TestCond::PropEq {
            name: "category".into(),
            value: "B".into(),
        };

        let r = cond.check(&d);

        assert_eq!(false, r);
    }

    #[test]
    fn in_match() {
        let d = test_item();

        let cond = TestCond::PropIn {
            name: "category".into(),
            values: vec!["B".into(), "D".into(), "A".into()],
        };

        let r = cond.check(&d);

        assert!(r);
    }

    #[test]
    fn in_unmatch() {
        let d = test_item();

        let cond = TestCond::PropIn {
            name: "category".into(),
            values: vec!["B".into(), "D".into(), "C".into()],
        };

        let r = cond.check(&d);

        assert_eq!(false, r);
    }

    #[test]
    fn not_eq_match() {
        let d = test_item();

        let cond = TestCond::Not(
            TestCond::PropEq {
                name: "category".into(),
                value: "A".into(),
            }
            .into(),
        );

        let r = cond.check(&d);

        assert_eq!(false, r);
    }

    #[test]
    fn not_in_unmatch() {
        let d = test_item();

        let cond = TestCond::Not(
            TestCond::PropIn {
                name: "category".into(),
                values: vec!["B".into(), "D".into(), "C".into()],
            }
            .into(),
        );

        let r = cond.check(&d);

        assert!(r);
    }

    #[test]
    fn and_match() {
        let d = test_item2();

        let cond = TestCond::And(
            TestCond::PropEq {
                name: "category".into(),
                value: "A".into(),
            }
            .into(),
            TestCond::PropEq {
                name: "kind".into(),
                value: "item".into(),
            }
            .into(),
        );

        let r = cond.check(&d);

        assert!(r);
    }

    #[test]
    fn and_unmatch_first() {
        let d = test_item2();

        let cond = TestCond::And(
            TestCond::PropEq {
                name: "category".into(),
                value: "B".into(),
            }
            .into(),
            TestCond::PropEq {
                name: "kind".into(),
                value: "item".into(),
            }
            .into(),
        );

        let r = cond.check(&d);

        assert_eq!(false, r);
    }

    #[test]
    fn and_unmatch_second() {
        let d = test_item2();

        let cond = TestCond::And(
            TestCond::PropEq {
                name: "category".into(),
                value: "A".into(),
            }
            .into(),
            TestCond::PropEq {
                name: "kind".into(),
                value: "data".into(),
            }
            .into(),
        );

        let r = cond.check(&d);

        assert_eq!(false, r);
    }

    #[test]
    fn or_first_match() {
        let d = test_item2();

        let cond = TestCond::Or(
            TestCond::PropEq {
                name: "category".into(),
                value: "A".into(),
            }
            .into(),
            TestCond::PropEq {
                name: "kind".into(),
                value: "data".into(),
            }
            .into(),
        );

        let r = cond.check(&d);

        assert!(r);
    }

    #[test]
    fn or_second_match() {
        let d = test_item2();

        let cond = TestCond::Or(
            TestCond::PropEq {
                name: "category".into(),
                value: "B".into(),
            }
            .into(),
            TestCond::PropEq {
                name: "kind".into(),
                value: "item".into(),
            }
            .into(),
        );

        let r = cond.check(&d);

        assert!(r);
    }

    #[test]
    fn or_unmatch() {
        let d = test_item2();

        let cond = TestCond::Or(
            TestCond::PropEq {
                name: "category".into(),
                value: "B".into(),
            }
            .into(),
            TestCond::PropEq {
                name: "kind".into(),
                value: "data".into(),
            }
            .into(),
        );

        let r = cond.check(&d);

        assert_eq!(false, r);
    }

    #[test]
    fn min_same() {
        let d = test_item();

        let cond = TestCond::AmountMin {
            name: None,
            value: 100,
        };

        let r = cond.check(&d);

        assert!(r);
    }

    #[test]
    fn min_under() {
        let d = test_item();

        let cond = TestCond::AmountMin {
            name: None,
            value: 101,
        };

        let r = cond.check(&d);

        assert_eq!(false, r);
    }

    #[test]
    fn max_same() {
        let d = test_item();

        let cond = TestCond::AmountMax {
            name: None,
            value: 100,
        };

        let r = cond.check(&d);

        assert!(r);
    }

    #[test]
    fn max_over() {
        let d = test_item();

        let cond = TestCond::AmountMax {
            name: None,
            value: 99,
        };

        let r = cond.check(&d);

        assert_eq!(false, r);
    }

    fn test_item() -> TestItem {
        TestItem(HashMap::from([("category".into(), "A".into())]), 100)
    }

    fn test_item2() -> TestItem {
        TestItem(
            HashMap::from([
                ("category".into(), "A".into()),
                ("kind".into(), "item".into()),
            ]),
            100,
        )
    }
}
