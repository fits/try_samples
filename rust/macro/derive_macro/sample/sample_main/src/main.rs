
use sample_core::Sample;
use sample_macro::Sample;

#[derive(Debug, Sample)]
struct Data1(i32);

#[derive(Debug, Sample)]
struct Data2 {}

#[derive(Debug, Sample)]
enum Data3 {
    One,
    Two,
}

fn main() {
    println!("name = {}, kind = {}", Data1::type_name(), Data1::type_kind());
    println!("name = {}, kind = {}", Data2::type_name(), Data2::type_kind());
    println!("name = {}, kind = {}", Data3::type_name(), Data3::type_kind());

    let d1 = Data1(10);
    println!("{}", d1.note());

    let d2 = Data2 {};
    println!("{}", d2.note());

    println!("{}", Data3::One.note());
    println!("{}", Data3::Two.note());
}