
fn func1(a: i32) -> impl Fn(i32) -> i32 {
    move |b| a + b
}

fn func2(a: i32, f: fn(i32) -> i32) -> impl Fn(i32) -> i32 {
    move |b| f(a) + b
}

fn func3<A, B>(f: &dyn Fn(A) -> B) -> impl Fn(A) -> B + '_ {
    move |a| f(a)
}

fn func4<'a, A, B, C>(f: &'a dyn Fn(A) -> B, g: &'a dyn Fn(B) -> C) -> impl Fn(A) -> C + 'a {
    move |a| g(f(a))
}

fn func4a<'a, A, B, C, F, G>(f: &'a F, g: &'a G) -> impl Fn(A) -> C + 'a 
    where
        F: Fn(A) -> B,
        G: Fn(B) -> C,
{
    move |a| g(f(a))
}

fn func5() -> impl Fn(i32) -> i32 {
    |a| a * 2
}

fn func6() -> impl Fn(i32) -> Box<dyn Fn(i32) -> i32> {
    |a| Box::new(move |b| a * b)
}

fn func6a() -> impl Fn(i32) -> fn(i32) -> i32 {
    |_| |b: i32| b
}

fn func7() -> impl Fn(i32) -> Box<dyn Fn(i32) -> Box<dyn Fn(i32) -> i32>> {
    |a: i32| Box::new(move |b: i32| Box::new(move |c: i32| a * b + c))
}

fn main() {
    let f1 = func1(1);

    println!("f1 = {}", f1(2));
    println!("f1 = {}", f1(3));

    let f2 = func2(2, |a| a * 5);

    println!("f2 = {}", f2(4));
    println!("f2 = {}", f2(5));

    let f3 = func3(&|a: i32| format!("-{}-", a));

    println!("f3 = {}", f3(6));
    println!("f3 = {}", f3(7));

    let f4 = func4(&|a: i32| a * 3, &|b| format!("**{}**", b));

    println!("f4 = {}", f4(8));
    println!("f4 = {}", f4(9));

    let f4a = func4a(&|a: i32| a * 3, &|b| format!("# {} #", b));

    println!("f4a = {}", f4a(10));
    println!("f4a = {}", f4a(11));

    let f5 = func5();

    println!("f5 = {}", f5(7));

    let c1 = |a: i32| move |b: i32| a * b;

    println!("c1 = {}", c1(2)(3));

    let f6 = func6();

    println!("f6 = {}", f6(2)(3));

    let f6a = func6a();

    println!("f6a = {}", f6a(2)(3));

    let c2 = |a: i32| move |b: i32| move |c: i32| a * b + c;

    println!("c2 = {}", c2(2)(3)(4));

    let f7 = func7();

    println!("f7 = {}", f7(2)(3)(4));
}
