
fn sample1() {
    fn a1() -> fn(i32) -> i32 {
        |x| x * 2
    }
    println!("a1 = {}", a1()(11));

    fn b1() -> impl Fn(i32) -> i32 {
        |x| x * 2
    }
    println!("b1 = {}", b1()(12));
    
    fn c1() -> Box<dyn Fn(i32) -> i32> {
        Box::new(|x| x * 2)
    }
    println!("c1 = {}", c1()(13));
}

fn sample2() {
    /* コンパイルエラー: closures can only be coerced to `fn` types if they do not capture any variables
    
    fn a2(y: i32) -> fn(i32) -> i32 {
        move |x| x * y
    }
    println!("a2 = {}", a2(2)(21));
    */

    fn b2(y: i32) -> impl Fn(i32) -> i32 {
        move |x| x * y
    }
    println!("b2 = {}", b2(2)(22));
    
    fn c2(y: i32) -> Box<dyn Fn(i32) -> i32> {
        Box::new(move |x| x * y)
    }
    println!("c2 = {}", c2(2)(23));
}

fn sample3() {
    fn bc() -> impl Fn(i32) -> Box<dyn Fn(i32) -> i32> {
        |x| Box::new(move |y| x * y)
    }
    println!("bc = {}", bc()(3)(32));

    /* コンパイルエラー: `impl Trait` only allowed in function and inherent method return types, not in `Fn` trait return

    fn bb() -> impl Fn(i32) -> impl Fn(i32) -> i32 {
        |x| move |y| x * y
    }
    */

    fn cc() -> Box<dyn Fn(i32) -> Box<dyn Fn(i32) -> i32>> {
        Box::new(|x| Box::new(move |y| x * y))
    }
    println!("cc = {}", cc()(3)(33));

    fn ba() -> impl Fn(i32) -> fn(i32) -> i32 {
        |_x||y| y
    }
    println!("ba = {}", ba()(3)(31));
}

fn sample4() {
    fn bcc(d: i32) -> impl Fn(i32) -> Box<dyn Fn(i32) -> Box<dyn Fn(i32) -> i32>> {
        move |a| Box::new(move |b| Box::new(move |c| a * b * c + d))
    }
    println!("bcc = {}", bcc(2)(3)(4)(5));
}

fn main() {
    sample1();
    sample2();
    sample3();
    sample4();

    let c = |x: i32| move |y: i32| x * y;
    println!("c = {}", c(10)(20));
}
