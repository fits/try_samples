
mod models;
use models::{Action, Event, Restore, Stock, StockMove, StockMoveAction};

fn sample1() {
    println!("----- sample1 -----");

    let item1 = "item-A";
    let loc1 = "location-1";
    let loc2 = "location-2";

    let stock1 = Stock::managed_new(item1.to_string(), loc1.to_string());
    let stock2 = Stock::managed_new(item1.to_string(), loc2.to_string());

    println!("{:?}, {:?}", stock1, stock2);

    let mv1 = "mv1";
    let state = StockMove::Nothing;

    let cmd1 = StockMoveAction::start(mv1.to_string(), item1.to_string(), 5, loc1.to_string(), loc2.to_string()).unwrap();

    let ev1 = Action::action(&(state.clone(), |_, _| Some(stock1.clone())), &cmd1).unwrap();
    println!("{:?}", ev1);

    let state = ev1.apply(state).unwrap();
    println!("*** state1 = {:?}", state);

    let cmd2 = StockMoveAction::arrival(mv1.to_string(), 5).unwrap();

    let ev2 = Action::action(&(state.clone(), |_, _| Some(stock2.clone())), &cmd2).unwrap();
    println!("{:?}", ev2);

    let state = ev2.apply(state).unwrap();
    println!("*** state2 = {:?}", state);

    let rm1 = Restore::restore(StockMove::Nothing, vec![&ev1, &ev2].iter());
    println!("*** restored move = {:?}", rm1);

    let stock1_1 = Restore::restore(stock1.clone(), vec![&ev1, &ev2].iter());
    println!("*** restored stock1 = {:?}", stock1_1);

    let stock2_1 = Restore::restore(stock2.clone(), vec![&ev1, &ev2].iter());
    println!("*** restored stock2 = {:?}", stock2_1);
}

fn sample2() {
    println!("----- sample2 -----");

    let item1 = "item-A";
    let loc1 = "location-1";
    let loc2 = "location-2";

    let stock1 = Stock::managed_new(item1.to_string(), loc1.to_string()).update_qty(5);
    let stock2 = Stock::managed_new(item1.to_string(), loc2.to_string());

    println!("{:?}, {:?}", stock1, stock2);

    let mv1 = "mv1";
    let state = StockMove::Nothing;

    let cmd1 = StockMoveAction::start(mv1.to_string(), item1.to_string(), 2, loc1.to_string(), loc2.to_string()).unwrap();

    let ev1 = Action::action(&(state.clone(), |_, _| Some(stock1.clone())), &cmd1).unwrap();
    println!("{:?}", ev1);

    let state = ev1.apply(state).unwrap();
    println!("*** state1 = {:?}", state);

    let stock1 = Restore::restore(stock1, vec![&ev1].iter());
    let stock2 = Restore::restore(stock2, vec![&ev1].iter());

    println!("{:?}, {:?}", stock1, stock2);

    let cmd2 = StockMoveAction::assign(mv1.to_string()).unwrap();

    let ev2 = Action::action(&(state.clone(), |_, _| Some(stock1.clone())), &cmd2).unwrap();
    println!("{:?}", ev2);

    let state = ev2.apply(state).unwrap();
    println!("*** state2 = {:?}", state);

    let stock1 = Restore::restore(stock1, vec![&ev2].iter());
    let stock2 = Restore::restore(stock2, vec![&ev2].iter());

    println!("{:?}, {:?}", stock1, stock2);

    let cmd3 = StockMoveAction::shipment(mv1.to_string(), 1).unwrap();

    let ev3 = Action::action(&(state.clone(), |_, _| Some(stock1.clone())), &cmd3).unwrap();
    println!("{:?}", ev3);

    let state = ev3.apply(state).unwrap();
    println!("*** state3 = {:?}", state);

    let stock1 = Restore::restore(stock1, vec![&ev3].iter());
    let stock2 = Restore::restore(stock2, vec![&ev3].iter());

    println!("{:?}, {:?}", stock1, stock2);

    let cmd4 = StockMoveAction::arrival(mv1.to_string(), 1).unwrap();

    let ev4 = Action::action(&(state.clone(), |_, _| Some(stock1.clone())), &cmd4).unwrap();
    println!("{:?}", ev4);

    let state = ev4.apply(state).unwrap();
    println!("*** state4 = {:?}", state);

    let stock1 = Restore::restore(stock1, vec![&ev4].iter());
    let stock2 = Restore::restore(stock2, vec![&ev4].iter());

    println!("{:?}, {:?}", stock1, stock2);
}

fn main() {
    sample1();
    sample2();
}
