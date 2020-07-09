
mod models;
use models::{CartCommand, Cart};

fn action(state: &Cart, cmd: &CartCommand) -> Option<Cart> {
    let ev = state.action(cmd);
    let new_state = ev.clone().map(|e| e.apply(state));

    println!(
        "cart: {:?}, cmd: {:?} => cart: {:?}, event: {:?}", 
        state, cmd, new_state, ev
    );

    new_state
}

fn main() {
    let cart0 = Cart::Nothing;

    let cmd1 = CartCommand::Create;
    let cart1 = action(&cart0, &cmd1).unwrap();

    action(&cart1, &CartCommand::Cancel);
    action(&cart1, &CartCommand::CheckOut);

    let cmd2 = CartCommand::ChangeQty("item-A".to_string(), 2);
    let cart2 = action(&cart1, &cmd2).unwrap();

    action(&cart2, &CartCommand::Cancel);
    action(&cart2, &CartCommand::ChangeQty("item-A".to_string(), 2));
    action(&cart2, &CartCommand::ChangeQty("item-B".to_string(), 0));

    let cmd3 = CartCommand::ChangeQty("item-B".to_string(), 1);
    let cart3 = action(&cart2, &cmd3).unwrap();

    action(&cart3, &CartCommand::Cancel);

    let cmd4 = CartCommand::ChangeQty("item-A".to_string(), 0);
    let cart4 = action(&cart3, &cmd4).unwrap();

    let cmd5 = CartCommand::CheckOut;
    let cart5 = action(&cart4, &cmd5).unwrap();

    println!("{:?}", cart5);
}
