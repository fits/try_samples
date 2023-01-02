use std::ops::Not;

pub type Id = String;
pub type Quantity = u32;

#[derive(Debug)]
pub enum Command {
    Create(Id),
    Update(Quantity),
}

#[derive(Debug, Clone)]
pub enum Stock {
    None,
    OutOfStock { id: Id },
    InStock { id: Id, qty: Quantity },
}

pub trait State<C> {
    type Output;

    fn action(&self, cmd: &C) -> Self::Output;
}

impl State<Command> for Stock {
    type Output = Option<Self>;

    fn action(&self, cmd: &Command) -> Self::Output {
        match self {
            Stock::None => match cmd {
                Command::Create(id) if id.is_empty().not() => 
                    Some(Stock::OutOfStock { id: id.clone() }),
                _ => None
            }
            Stock::OutOfStock { id } => match cmd {
                Command::Update(q) if q > &0 => 
                    Some(Stock::InStock { id: id.clone(), qty: *q }),
                _ => None
            }
            Stock::InStock { id, qty } => match cmd {
                Command::Update(q) =>
                    if q == &0 {
                        Some(Stock::OutOfStock { id: id.clone() })
                    } else if q != qty {
                        Some(Stock::InStock { id: id.clone(), qty: *q })
                    } else {
                        None
                    }
                _ => None
            }
        }
    }
}
