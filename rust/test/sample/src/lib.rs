
pub fn add(x: i32, y: i32) -> i32 {
    x + y
}

#[cfg(test)]
mod unit_test {
    use super::*;

    #[test]
    fn test_add() {
        assert_eq!(3, add(1, 2));
    }
}