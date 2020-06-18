
macro_rules! to {
    ($f:ty => $t:ty , $b:expr) => {
        impl From<$f> for $t {
            fn from(it: $f) -> Self {
                $b(it)
            }
        }
    };
}

macro_rules! to2 {
    (($v:ident : $f:ty) -> $t:ty , $b:expr) => {
        impl From<$f> for $t {
            fn from($v: $f) -> Self {
                $b
            }
        }
    };
}

#[derive(Debug, Clone)]
struct A(i32);

#[derive(Debug, Clone)]
struct B(String);

#[derive(Debug, Clone)]
struct C(String);

to!( A => B, |a: A| B(a.0.to_string()) );

to2!( (a: A) -> C, C(a.0.to_string()) );

fn main() {
    let a = A(10);
    let b: B = a.clone().into();
    let c: C = a.clone().into();

    println!("{:?}", a);
    println!("{:?}", b);
    println!("{:?}", c);
}
