
fn sample1<T: Fn(i32) -> i32>(v: i32, f: T) -> i32 {
    f(v)
}

fn sample2(v: i32, f: fn(i32) -> i32) -> i32 {
    f(v)
}

fn double(n: i32) -> i32 {
    n * 2
}

fn sample3(v: i32) -> Box<dyn Fn(i32) -> i32> {
    Box::new( move |x| x * v )
}

fn sample4() -> Box<fn(i32) -> i32> {
    Box::new(double)
}

fn main() {
    let r1 = sample1(1, |x| x + 10);
    println!("{}", r1);

    let r1b = sample1(1, double);
    println!("{}", r1b);

    let r2 = sample2(10, |x| x + 10);
    println!("{}", r2);

    let r2b = sample2(10, double);
    println!("{}", r2b);

    let f1 = sample3(3);
    println!("{}", f1(10));

    let f2 = sample4();
    println!("{}", f2(40));
}
