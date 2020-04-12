
fn main() {
    let mut n = 0;
    let mut update = |x: i32| n = x;

    update(1);
    update(2);

    println!("{}", n); // 2

    let mut update_mv = move |x: i32| n = x;

    update_mv(3);

    println!("{}", n); // 2
}
