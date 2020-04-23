
fn add(x: i32, y: i32) -> i32 {
    x + y
}

fn add2() -> fn(i32, i32) -> i32 {
    |x, y| x + y
}

fn log(s: String) {
    println!("log: {}", s);
}

fn main() {
    log("sample".to_string());

    println!("5 + 6 = {}", add(5, 6));

    log( format!("7 + 10 = {}", add2()(7, 10)) );
}
