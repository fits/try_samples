fn main() {
    let v1 = vec!["a1", "b2", "c3", "d4", "e5", "f6"];

    println!("{:?}", v1.get(0..3));
    println!("{:?}", v1.get(4..));

    let n = 2;
    println!("{:?}", v1.get(..n));

    let size = 3;

    println!("{:?}", v1.get((v1.len() - size)..));

    let v2 = vec!["a1", "b2", "c3", "d4", "e5", "f6", "c3", "d4", "g7", "c3", "f6"];

    for ts in v2.windows(3) {
        if ts.starts_with(&["c3", "d4"]) {
            println!("next: {}", ts.last().unwrap());
        }
    }
}
