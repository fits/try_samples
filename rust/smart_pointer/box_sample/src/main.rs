
fn main() {
    let a = 1;
    let b = a; // copy

    println!("a = {}", a);
    println!("b = {}", b);

    let x = Box::new(2);
    let y = x;

    //println!("x = {}", x); // compile error : moved x value
    println!("y = {}", y);
}
