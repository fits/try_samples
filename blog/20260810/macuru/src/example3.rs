use macuru::{MonadLike, adt, mdo};

use std::fmt::Debug;
use std::ops::Add;

adt!(
    Stock<ID, QTY> = OutOfStock<ID> | InStock<ID, QTY> derive Clone, Debug
    with StockFunc<ID, QTY>
    where
        ID: Debug + Clone,
        QTY: Copy + Default + PartialOrd + Add<Output = QTY>,
    {
        fn id(&self) -> ID;
        fn qty(&self) -> QTY;
        fn restock(&self, q: QTY) -> Option<Self>;
    }
);

#[derive(Clone, Debug)]
pub struct OutOfStock<ID>(ID);

#[derive(Clone, Debug)]
pub struct InStock<ID, QTY> {
    id: ID,
    qty: QTY,
}

impl<ID, QTY> StockFunc<ID, QTY> for OutOfStock<ID>
where
    ID: Debug + Clone,
    QTY: Copy + Default + PartialOrd + Add<Output = QTY>,
{
    fn id(&self) -> ID {
        self.0.clone()
    }

    fn qty(&self) -> QTY {
        QTY::default()
    }

    fn restock(&self, q: QTY) -> Option<Stock<ID, QTY>> {
        if q > QTY::default() {
            Some(
                InStock {
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

impl<ID, QTY> StockFunc<ID, QTY> for InStock<ID, QTY>
where
    ID: Debug + Clone,
    QTY: Copy + Default + PartialOrd + Add<Output = QTY>,
{
    fn id(&self) -> ID {
        self.id.clone()
    }

    fn qty(&self) -> QTY {
        self.qty
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

fn main() -> Result<(), ()> {
    type StockA = Stock<&'static str, usize>;

    let print_stock = |s: &StockA| {
        println!("id={}, qty={}, {:?}", s.id(), s.qty(), s);
    };

    let s1: StockA = OutOfStock("stock-1").into();
    print_stock(&s1);

    if let Some(s2) = s1.restock(1) {
        print_stock(&s2);

        if let Some(s3) = s2.restock(2) {
            print_stock(&s3);
        }
    }

    let s2 = mdo!(
        a <- s1.restock(1)
        b <- a.restock(20)
        c <- b.restock(3)
        d <- c.restock(40)
        yield d
    );

    println!("{:?}", s2);

    Ok(())
}
