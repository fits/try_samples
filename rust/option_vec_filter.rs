
fn main() {
    let d1 = Some(1);
    let d2 = Some(2);
    let d3 = None;
    let d4 = Some(4);
    let d5 = None;

    let d = [d1, d2, d3, d4, d5];

    println!("{:?}", d);

    let r1 = d.iter()
        .filter(|s| s.is_some())
        .map(|s| s.unwrap())
        .collect::<Vec<_>>();

    println!("r1 = {:?}", r1);
    assert_eq!(vec![1, 2, 4], r1);

    let r2 = d.iter()
        .flat_map(|s| match s {
            Some(v) => vec![v],
            None => vec![],
        })
        .collect::<Vec<_>>();

    println!("r2 = {:?}", r2);
    assert_eq!(vec![&1, &2, &4], r2);

    let r2b = d.iter()
        .flat_map(|s| match s {
            Some(v) => vec![v],
            None => vec![],
        })
        .cloned()
        .collect::<Vec<_>>();

    println!("r2b = {:?}", r2b);
    assert_eq!(vec![1, 2, 4], r2b);

    let r3 = d.iter()
        .flat_map(|o| o.map_or(vec![], |s| vec![s]))
        .collect::<Vec<_>>();

    println!("r3 = {:?}", r3);
    assert_eq!(vec![1, 2, 4], r3);

    let r4 = d.iter()
        .flat_map(Option::iter)
        .collect::<Vec<_>>();

    println!("r4 = {:?}", r4);
    assert_eq!(vec![&1, &2, &4], r4);

    let r4b = d.iter()
        .flat_map(Option::iter)
        .cloned()
        .collect::<Vec<_>>();

    println!("r4b = {:?}", r4b);
    assert_eq!(vec![1, 2, 4], r4b);
}
