
mod models;

use models::{ Stock, StockMove, Restore };

fn main() {
    let item1 = "item-1".to_string();

    let loc1 = "maker-A".to_string();
    let loc2 = "store-B".to_string();
    let loc3 = "user-C".to_string();

    let stock1 = Stock::unmanaged_new(item1.clone(), loc1.clone());
    let stock2 = Stock::managed_new(item1.clone(), loc2.clone());
    let stock3 = Stock::unmanaged_new(item1.clone(), loc3.clone());

    let a1 = StockMove::start(
        item1.clone(), 
        15, 
        loc1.clone(), 
        loc2.clone()
    );

    println!("*** A - start: {:?}", a1);

    if let Some((a_s1, a_e1)) = a1 {
        let a2 = a_s1.arrive(15);

        println!("*** A - arrive: {:?}", a2);

        if let Some((a_s2, a_e2)) = a2 {
            let stock1 = stock1.restore(vec![&a_e1, &a_e2].iter());
            let stock2 = stock2.restore(vec![&a_e1, &a_e2].iter());

            println!("stock1: {:?}", stock1);
            println!("stock2: {:?}", stock2);

            let a3 = a_s2.complete();

            println!("*** A - complete: {:?}", a3);

            let b1 = StockMove::start(
                item1.clone(), 
                2, 
                loc2.clone(), 
                loc3.clone()
            );

            println!("*** B - start: {:?}", b1);

            if let Some((b_s1, b_e1)) = b1 {
                let b2 = b_s1.assign(|_, _| Some(stock2.clone()));

                println!("*** B - assign: {:?}", b2);

                if let Some((b_s2, b_e2)) = b2 {
                    let stock2 = stock2.restore(vec![&b_e1, &b_e2].iter());

                    println!("stock2: {:?}", stock2);

                    let b3 = b_s2.ship(2);

                    println!("*** B - ship: {:?}", b3);

                    if let Some((b_s3, b_e3)) = b3 {
                        let b4 = b_s3.arrive(2);

                        println!("*** B - arrive: {:?}", b4);

                        if let Some((b_s4, b_e4)) = b4 {
                            let stock2 = stock2.restore(vec![&b_e3, &b_e4].iter());
                            let stock3 = stock3.restore(vec![&b_e3, &b_e4].iter());

                            println!("stock2: {:?}", stock2);
                            println!("stock3: {:?}", stock3);

                            let b5 = b_s4.complete();

                            println!("*** B - complete: {:?}", b5);
                        }
                    }
                }
            }
        }
    }
}
