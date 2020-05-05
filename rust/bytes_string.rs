
fn main() {
    let s1 = "abc";
    let s2 = b"abc";

    println!("{}", s1);
    println!("{:?}", s2);

    let s3 = "テスト";

    println!("{}", s3);
    println!("{:?}", "テスト".as_bytes());
}
