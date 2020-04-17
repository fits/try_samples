
#[derive(Debug, Default, Clone)]
struct Data {
    id: String,
    value: i32,
}

fn main() {
    let ds1 = vec![
        Data { id: "d1".to_string(), value: 1},
        Data { id: "d2".to_string(), value: 2},
    ];

    let ds2 = [
        ds1.clone(), 
        vec![Data { id: "d3".to_string(), value: 3 }]
    ].concat();

    println!("{:?}", ds2);

    let ds3 = ds2.iter().cloned().chain(
        std::iter::once(Data { id: "d4".to_string(), value: 4 })
    ).collect::<Vec<_>>();

    println!("{:?}", ds3);
}
