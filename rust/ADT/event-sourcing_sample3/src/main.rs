
mod models;
use models::{Stock, StockCommand, StockEvent};

fn print_restore(events: Vec<&StockEvent>) {
    println!(
        "*** restore {}: {:?}", 
        events.len(),
        Stock::restore(Stock::Nothing, events)
    );
}

fn main() {
    let id1 = "s1";

    let cmd1 = StockCommand::create(id1.to_string());
    let (s1, e1) = Stock::action(Stock::Nothing, &cmd1).unwrap();
    println!("{:?}, {:?}", s1, e1);

    print_restore(vec![&e1]);

    let r1 = Stock::action(s1.clone(), &StockCommand::ship(id1.to_string(), 2));
    println!("{:?}", r1);
    assert_eq!(true, r1.is_none());

    let cmd2 = StockCommand::arrive(id1.to_string(), 5);
    let (s2, e2) = Stock::action(s1.clone(), &cmd2).unwrap();
    println!("{:?}, {:?}", s2, e2);

    if let Stock::InStock { qty, .. } = s2 {
        assert_eq!(5, qty);
    }

    print_restore(vec![&e1, &e2]);

    let cmd3 = StockCommand::ship(id1.to_string(), 2);
    let (s3, e3) = Stock::action(s2.clone(), &cmd3).unwrap();
    println!("{:?}, {:?}", s3, e3);

    if let Stock::InStock { qty, .. } = s3 {
        assert_eq!(3, qty);
    }

    print_restore(vec![&e1, &e2, &e3]);

    let cmd4 = StockCommand::ship(id1.to_string(), 3);
    let (s4, e4) = Stock::action(s3.clone(), &cmd4).unwrap();
    println!("{:?}, {:?}", s4, e4);

    print_restore(vec![&e1, &e2, &e3, &e4]);

    let cmd4b = StockCommand::ship(id1.to_string(), 4);
    let r2 = Stock::action(s3.clone(), &cmd4b);
    println!("{:?}", r2);
    assert_eq!(true, r2.is_none());
}
