
fn main() {
    let a = "a1".to_string();
    let b = "b1".to_string();

    println!("sample1 = {:?}", sample1(&a));
    println!("sample2 = {:?}", sample2(&a, &b));
    println!("sample3 = {:?}", sample3(&a, &b));
    println!("sample4 = {}", sample4(&a, &b));

    let r1;

    {
        let b2 = "b2".to_string();

        println!("nest sample1 = {:?}", sample1(&b2));
        println!("nest sample2 = {:?}", sample2(&a, &b2));
        println!("nest sample3 = {:?}", sample3(&a, &b2));
        println!("nest sample4 = {}", sample4(&a, &b2));

        r1 = sample4(&b2, &a);
    }

    println!("r1 = {}", r1);

    let r2;

    {
        // &'static str
        let b3 = "b3";

        r2 = sample4(&a, b3);
    }

    println!("r2 = {}", r2);
}

fn sample1(x: &str) -> (&str,) {
    (x,)
}

fn sample2<'a, 'b>(x: &'a str, y: &'b str) -> (&'a str, &'b str) {
    (x, y)
}

fn sample3<'a>(x: &'a str, y: &'a str) -> (&'a str, &'a str) {
    (x, y)
}

fn sample4<'a, 'b>(_: &'a str, y: &'b str) -> &'b str {
    y
}