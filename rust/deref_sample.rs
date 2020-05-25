
use std::ops::Deref;

struct Sample<T> {
    data: T,
}

impl<T> Deref for Sample<T> {
    type Target = T;

    fn deref(&self) -> &T {
        &self.data
    }
}

fn print_int(v: &i32) {
    println!("print_int: {}", v);
}

fn main() {
    let d = Sample { data: 123 };

    assert_eq!(123, *d);
    assert_eq!(123, *(d.deref()));

    print_int(&d);
    print_int(d.deref());
}