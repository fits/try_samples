
mod models;
use models::{Stock, CreateStock, ShipStock, ArriveStock, StockEvent};

fn print_restore(events: Vec<&StockEvent>) {
    println!(
        "*** restore {}: {:?}", 
        events.len(),
        Stock::restore(Stock::Nothing, events)
    );
}

fn main() {
    let id1 = "s1";

    let r1 = Stock::handle(Stock::Nothing, &CreateStock(id1.to_string()));
    println!("{:?}", r1);

    if let Some((s1, e1)) = r1 {
        print_restore(vec![&e1]);

        let r2 = Stock::handle(s1.clone(), &ShipStock(id1.to_string(), 2));
        println!("{:?}", r2);

        let r2b = Stock::handle(s1.clone(), &ArriveStock(id1.to_string(), 5));
        println!("{:?}", r2b);

        if let Some((s2, e2)) = r2b {
            print_restore(vec![&e1, &e2]);

            let r3 = Stock::handle(s2.clone(), &ShipStock(id1.to_string(), 2));
            println!("{:?}", r3);

            if let Some((s3, e3)) = r3 {
                print_restore(vec![&e1, &e2, &e3]);

                let r4 = Stock::handle(s3.clone(), &ShipStock(id1.to_string(), 3));
                println!("{:?}", r4);

                if let Some((_, e4)) = r4 {
                    print_restore(vec![&e1, &e2, &e3, &e4]);
                }
            }
        }
    }
}
