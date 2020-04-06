
mod models;

fn main() {
    let d1 = models::Stock::default();
    println!("d1 = {:?}", &d1);

    let cmd1 = models::Command::Create { id: "data1".to_string() };
    let d2 = d1.handle(&cmd1);

    println!("d2 = {:?}", &d2);
    assert_eq!(&d2.id, "data1");
    assert_eq!(d2.qty, 0);

    let cmd2 = models::Command::Update { id: "data1".to_string(), qty: 5 };
    let d3 = d2.handle(&cmd2);

    println!("d3 = {:?}", &d3);
    assert_eq!(&d3.id, "data1");
    assert_eq!(d3.qty, 5);

    let cmd3 = models::Command::Update { id: "data2".to_string(), qty: 10 };
    let d4 = d3.handle(&cmd3);

    println!("d4 = {:?}", &d4);
    assert_eq!(&d4.id, "data1");
    assert_eq!(d4.qty, 5);

    let d5 = d4.handle(&cmd1);

    println!("d5 = {:?}", &d5);
    assert_eq!(&d5.id, "data1");
    assert_eq!(d5.qty, 5);

    let d6 = models::Stock::default().handle(&cmd3);

    println!("d6 = {:?}", &d6);
    assert!(d6.id.is_empty());
    assert_eq!(d6.qty, 0);
}
