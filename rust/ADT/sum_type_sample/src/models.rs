use std::ops::Not;

type StockId = String;

#[derive(Debug)]
pub enum Command {
    Create { id: StockId },
    Update { id: StockId, qty: u32 },
}

#[derive(Debug, Default, Clone)]
pub struct Stock {
    pub id: StockId,
    pub qty: u32,
}

impl Stock {
    pub fn handle(self, cmd: &Command) -> Stock {
        match cmd {
            Command::Create { id } => 
                if self.id.is_empty() && id.is_empty().not() {
                    Stock { id: id.clone(), ..self }
                } else {
                    self
                }
            Command::Update { id, qty } =>
                if id.is_empty().not() && self.id.eq(id) {
                    Stock { qty: *qty, ..self }
                } else {
                    self
                }
        }
    }
}