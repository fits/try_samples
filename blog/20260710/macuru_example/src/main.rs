use macuru::adt;

adt!(
    Stock = OutOfStock | InStock derive Clone, Debug with StockFunc {
        fn id(&self) -> String;
        fn restock(&self, qty: usize) -> Option<Self>;
    }
);

#[derive(Clone, Debug)]
pub struct OutOfStock(String);

#[derive(Clone, Debug)]
pub struct InStock {
    id: String,
    qty: usize,
}

impl StockFunc for OutOfStock {
    fn id(&self) -> String {
        self.0.clone()
    }

    fn restock(&self, qty: usize) -> Option<Stock> {
        if qty > 0 {
            Some(InStock { id: self.id(), qty }.into())
        } else {
            None
        }
    }
}

impl StockFunc for InStock {
    fn id(&self) -> String {
        self.id.clone()
    }

    fn restock(&self, qty: usize) -> Option<Stock> {
        if qty > 0 {
            Some(
                Self {
                    id: self.id(),
                    qty: self.qty + qty,
                }
                .into(),
            )
        } else {
            None
        }
    }
}

fn main() {
    let s1: Stock = OutOfStock("item-A".to_string()).into();

    println!("s1 = {:?}, id = {}", s1, s1.id());
    println!("s1.restock(0) = {:?}", s1.restock(0));

    let s2 = s1.restock(5).unwrap();

    println!("s2 = {:?}, id = {}", s2, s2.id());

    let s3 = s2.restock(2);

    println!("s3 = {:?}", s3);

    if let Some(InStock { qty, .. }) = s3.and_then(|x| InStock::try_from(x).ok()) {
        println!("s3 current qty = {}", qty);
    }
}
