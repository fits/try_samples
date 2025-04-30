use std::cell::OnceCell;

#[derive(Debug, Default)]
struct Data {
    name: OnceCell<String>,
}

fn main() {
    let d = Data::default();

    println!("{:?}", d);
    println!("get = {:?}", d.name.get());

    d.name.get_or_init(|| "d1".to_string());

    println!("{:?}", d);
    println!("get = {:?}", d.name.get());

    println!("set = {:?}", d.name.set("invalid".to_string()));
}
