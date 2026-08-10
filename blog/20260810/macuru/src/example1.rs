use macuru::{MonadLike, mdo};

fn main() {
    let v = Vec::from_iter(0..10);

    let r = mdo!(
        a <- vec!["a", "b"]
        b <- v.to_owned() where b % 2 == 0
        yield format!("{}-{}", a, b)
    );

    println!("{:?}", r);
}
