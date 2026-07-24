use std::{fmt::Debug, ops::Add};

#[derive(Debug)]
enum Stock<ID, QTY> {
    Empty(EmptyStock<ID>),
    NonEmpty(NonEmptyStock<ID, QTY>),
}

#[derive(Debug)]
struct EmptyStock<ID>(ID);

#[derive(Debug)]
struct NonEmptyStock<ID, QTY> {
    id: ID,
    qty: QTY,
}

trait StockFunc<ID, QTY> {
    fn id(&self) -> ID;
    fn qty(&self) -> Option<QTY>;
    fn restock(&self, q: QTY) -> Option<Stock<ID, QTY>>;
}

impl<ID, QTY> StockFunc<ID, QTY> for Stock<ID, QTY>
where
    ID: Clone,
    QTY: Copy + Default + PartialOrd + Add<Output = QTY>,
{
    fn id(&self) -> ID {
        match self {
            Self::Empty(x) => StockFunc::<ID, QTY>::id(x),
            Self::NonEmpty(x) => StockFunc::<ID, QTY>::id(x),
        }
    }

    fn qty(&self) -> Option<QTY> {
        match self {
            Self::Empty(x) => StockFunc::<ID, QTY>::qty(x),
            Self::NonEmpty(x) => StockFunc::<ID, QTY>::qty(x),
        }
    }

    fn restock(&self, q: QTY) -> Option<Stock<ID, QTY>> {
        match self {
            Self::Empty(x) => StockFunc::<ID, QTY>::restock(x, q),
            Self::NonEmpty(x) => StockFunc::<ID, QTY>::restock(x, q),
        }
    }
}

impl<ID, QTY> StockFunc<ID, QTY> for EmptyStock<ID>
where
    ID: Clone,
    QTY: Copy + Default + PartialOrd + Add<Output = QTY>,
{
    fn id(&self) -> ID {
        self.0.clone()
    }

    fn qty(&self) -> Option<QTY> {
        None
    }

    fn restock(&self, q: QTY) -> Option<Stock<ID, QTY>> {
        if q > QTY::default() {
            Some(
                NonEmptyStock {
                    id: self.0.clone(),
                    qty: q,
                }
                .into(),
            )
        } else {
            None
        }
    }
}

impl<ID, QTY> StockFunc<ID, QTY> for NonEmptyStock<ID, QTY>
where
    ID: Clone,
    QTY: Copy + Default + PartialOrd + Add<Output = QTY>,
{
    fn id(&self) -> ID {
        self.id.clone()
    }

    fn qty(&self) -> Option<QTY> {
        Some(self.qty)
    }

    fn restock(&self, q: QTY) -> Option<Stock<ID, QTY>> {
        if q > QTY::default() {
            Some(
                Self {
                    id: self.id.clone(),
                    qty: self.qty + q,
                }
                .into(),
            )
        } else {
            None
        }
    }
}

impl<ID, QTY> From<EmptyStock<ID>> for Stock<ID, QTY> {
    fn from(value: EmptyStock<ID>) -> Self {
        Self::Empty(value)
    }
}

impl<ID, QTY> From<NonEmptyStock<ID, QTY>> for Stock<ID, QTY> {
    fn from(value: NonEmptyStock<ID, QTY>) -> Self {
        Self::NonEmpty(value)
    }
}

fn print_stock<ID, QTY>(s: &Stock<ID, QTY>)
where
    ID: Clone + Debug,
    QTY: Copy + Debug + Default + PartialOrd + Add<Output = QTY>,
{
    println!("stock: id={:?}, qty={:?}, {:?}", s.id(), s.qty(), s);
}

fn main() {
    let s1: Stock<&str, u32> = EmptyStock("item-A").into();
    print_stock(&s1);
    println!("s1 restock 0 = {:?}", s1.restock(0));

    let s2 = s1.restock(3).unwrap();
    print_stock(&s2);
    println!("s2 restock 0 = {:?}", s2.restock(0));

    let s3 = s2.restock(2).unwrap();
    print_stock(&s3);

    let t1: Stock<u32, isize> = EmptyStock(1).into();
    print_stock(&t1);

    t1.restock(15).map(|x| print_stock(&x));
}
