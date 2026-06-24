use custom_attribute::custom;

#[custom(test1_dump)]
fn test1(a: isize, b: &str) {
    // print
    let c = b.len();
    println!("test1: {a}, {b}, {c}");
}

fn main() {
    test1(1, "abc");
    test1_dump();
    test1_dump_2();
}
