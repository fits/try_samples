
fn main() {
    let d1 = Some(1);
    let d2 = Some(2);
    let d3 = None;
    let d4 = Some(4);
    let d5 = None;

    let d = vec![d1, d2, d3, d4, d5];

    println!("{:?}", d);

    let r = d.iter()
        .filter(|s| s.is_some())
        .map(|s| s.unwrap())
        .collect::<Vec<_>>();

    println!("{:?}", r);

    let r2 = d.iter()
        .flat_map(|s| match s {
            Some(v) => vec![v],
            None => vec![],
        })
        .collect::<Vec<_>>();

    println!("{:?}", r2);

    let r3 = d.iter()
        .flat_map(|o| o.map_or(vec![], |s| vec![s]))
        .collect::<Vec<_>>();

    println!("{:?}", r3);
}
