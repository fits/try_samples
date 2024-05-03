#![allow(dead_code)]

pub trait Item {
    fn attrs(&self, name: &String) -> Option<&String>;
}

pub enum Condition {
    Eq { name: String, value: String },
    In { name: String, values: Vec<String> },
    Not(Box<Condition>),
    And(Box<Condition>, Box<Condition>),
    Or(Box<Condition>, Box<Condition>),
}

pub trait Predicate<'a, T> {
    fn check(&self, d: &'a T) -> bool;
}

impl<'a, T: Item> Predicate<'a, T> for Condition {
    fn check(&self, d: &'a T) -> bool {
        match self {
            Self::Eq { name, value } => d.attrs(name).map(|v| v == value).unwrap_or(false),
            Self::In { name, values } => d.attrs(name).map(|v| values.contains(v)).unwrap_or(false),
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

    struct TestItem(HashMap<String, String>);

    impl Item for TestItem {
        fn attrs(&self, name: &String) -> Option<&String> {
            self.0.get(name)
        }
    }

    #[test]
    fn eq_match() {
        let d = TestItem(HashMap::from([("category".into(), "A".into())]));

        let cond = Condition::Eq {
            name: "category".into(),
            value: "A".into(),
        };

        let r = cond.check(&d);

        assert!(r);
    }

    #[test]
    fn eq_unmatch() {
        let d = TestItem(HashMap::from([("category".into(), "A".into())]));

        let cond = Condition::Eq {
            name: "category".into(),
            value: "B".into(),
        };

        let r = cond.check(&d);

        assert_eq!(false, r);
    }

    #[test]
    fn in_match() {
        let d = TestItem(HashMap::from([("category".into(), "A".into())]));

        let cond = Condition::In {
            name: "category".into(),
            values: vec!["B".into(), "D".into(), "A".into()],
        };

        let r = cond.check(&d);

        assert!(r);
    }

    #[test]
    fn in_unmatch() {
        let d = TestItem(HashMap::from([("category".into(), "A".into())]));

        let cond = Condition::In {
            name: "category".into(),
            values: vec!["B".into(), "D".into(), "C".into()],
        };

        let r = cond.check(&d);

        assert_eq!(false, r);
    }

    #[test]
    fn not_eq_match() {
        let d = TestItem(HashMap::from([("category".into(), "A".into())]));

        let cond = Condition::Not(
            Condition::Eq {
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
        let d = TestItem(HashMap::from([("category".into(), "A".into())]));

        let cond = Condition::Not(
            Condition::In {
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
        let d = TestItem(HashMap::from([
            ("category".into(), "A".into()),
            ("kind".into(), "item".into()),
        ]));

        let cond = Condition::And(
            Condition::Eq {
                name: "category".into(),
                value: "A".into(),
            }
            .into(),
            Condition::Eq {
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
        let d = TestItem(HashMap::from([
            ("category".into(), "A".into()),
            ("kind".into(), "item".into()),
        ]));

        let cond = Condition::And(
            Condition::Eq {
                name: "category".into(),
                value: "B".into(),
            }
            .into(),
            Condition::Eq {
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
        let d = TestItem(HashMap::from([
            ("category".into(), "A".into()),
            ("kind".into(), "item".into()),
        ]));

        let cond = Condition::And(
            Condition::Eq {
                name: "category".into(),
                value: "A".into(),
            }
            .into(),
            Condition::Eq {
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
        let d = TestItem(HashMap::from([
            ("category".into(), "A".into()),
            ("kind".into(), "item".into()),
        ]));

        let cond = Condition::Or(
            Condition::Eq {
                name: "category".into(),
                value: "A".into(),
            }
            .into(),
            Condition::Eq {
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
        let d = TestItem(HashMap::from([
            ("category".into(), "A".into()),
            ("kind".into(), "item".into()),
        ]));

        let cond = Condition::Or(
            Condition::Eq {
                name: "category".into(),
                value: "B".into(),
            }
            .into(),
            Condition::Eq {
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
        let d = TestItem(HashMap::from([
            ("category".into(), "A".into()),
            ("kind".into(), "item".into()),
        ]));

        let cond = Condition::Or(
            Condition::Eq {
                name: "category".into(),
                value: "B".into(),
            }
            .into(),
            Condition::Eq {
                name: "kind".into(),
                value: "data".into(),
            }
            .into(),
        );

        let r = cond.check(&d);

        assert_eq!(false, r);
    }
}
