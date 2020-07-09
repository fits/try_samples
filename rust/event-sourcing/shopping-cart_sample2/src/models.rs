
pub type CatalogId = String;
pub type Quantity = u32;

pub type CartLines = im::HashMap<CatalogId, Quantity>;
pub type CatalogIdList = im::Vector<CatalogId>;

#[allow(dead_code)]
#[derive(Debug, Clone)]
pub enum CartCommand {
    Create,
    Cancel,
    ChangeQty(CatalogId, Quantity),
    CheckOut,
}

#[allow(dead_code)]
#[derive(Debug, Clone)]
pub enum CartEvent {
    Created,
    Cancelled,
    ActiveCancelled(CatalogIdList),
    ChangedQty(CatalogId, Quantity),
    CheckedOut,
}

impl CartEvent {
    pub fn apply(&self, state: &Cart) -> Cart {
        match self {
            Self::Created => match state {
                Cart::Nothing => Cart::Empty,
                _ => state.clone(),
            },
            Self::Cancelled => match state {
                Cart::Empty => Cart::Cancelled,
                _ => state.clone(),
            },
            Self::ActiveCancelled(_) => match state {
                Cart::Active(_) => Cart::Cancelled,
                _ => state.clone(),
            },
            Self::ChangedQty(c, q) => match state {
                Cart::Empty => Cart::Active(CartLines::unit(c.clone(), q.clone())),
                Cart::Active(lines) => {
                    let lines = if *q > 0 {
                        lines.update(c.clone(), q.clone())
                    } else {
                        lines.without(c)
                    };

                    if lines.is_empty() {
                        Cart::Empty
                    } else {
                        Cart::Active(lines)
                    }
                },
                _ => state.clone(),
            },
            Self::CheckedOut => match state {
                Cart::Active(lines) => Cart::CheckedOut(lines.clone()),
                _ => state.clone(),
            },
        }
    }
}

#[allow(dead_code)]
#[derive(Debug, Clone, PartialEq)]
pub enum Cart {
    Nothing,
    Empty,
    Active(CartLines),
    CheckedOut(CartLines),
    Cancelled,
}

#[allow(dead_code)]
impl Cart {
    pub fn action(&self, cmd: &CartCommand) -> Option<CartEvent> {
        match cmd {
            CartCommand::Create => self.create(),
            CartCommand::Cancel => self.cancel(),
            CartCommand::ChangeQty(c, q) =>
                self.change_qty(c.clone(), q.clone()),
            CartCommand::CheckOut => self.check_out(),
        }
    }

    pub fn restore<'a, T>(self, events: T) -> Self
        where
            T: Iterator<Item = &'a CartEvent>
    {
        events.fold(self, |state, event| event.apply(&state))
    }

    fn create(&self) -> Option<CartEvent> {
        match self {
            Self::Nothing => Some(CartEvent::Created),
            _ => None,
        }
    }

    fn cancel(&self) -> Option<CartEvent> {
        match self {
            Self::Empty => Some(CartEvent::Cancelled),
            Self::Active(lines) => 
                Some(
                    CartEvent::ActiveCancelled(
                        lines.keys().cloned().collect()
                    )
                ), 
            _ => None,
        }
    }

    fn change_qty(&self, catalog_id: CatalogId, qty: Quantity) -> Option<CartEvent> {
        match self {
            Self::Empty if qty > 0 => 
                Some(CartEvent::ChangedQty(catalog_id, qty)),
            Self::Active(lines) => {
                let line = lines
                    .iter()
                    .find(|(k, _)| **k == catalog_id);

                match line {
                    Some((_, v)) if *v != qty  => {
                        Some(CartEvent::ChangedQty(catalog_id, qty))
                    },
                    None if qty > 0 => 
                        Some(CartEvent::ChangedQty(catalog_id, qty)),
                    _ => None,
                }
            },
            _ => None,
        }
    }

    fn check_out(&self) -> Option<CartEvent> {
        match self {
            Self::Active(_) => Some(CartEvent::CheckedOut),
            _ => None,
        }
    }
}
