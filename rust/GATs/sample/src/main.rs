
trait Functor {
    type Current;
    type Target<V>: Functor;

    fn fmap<F, V>(self, f: F) -> Self::Target<V>
        where F: Fn(Self::Current) -> V;
}

impl<A> Functor for Option<A> {
    type Current = A;
    type Target<B> = Option<B>;

    fn fmap<F, B>(self, f: F) -> Self::Target<B> 
        where F: Fn(Self::Current) -> B
    {
        self.map(f)
    }
}

fn main() {
    let f = |x| x * 2;

    let r = Option::fmap(Some(5), f);
    println!("{:?}", r);
    
    let r2 = None::<i32>.fmap(f);
    println!("{:?}", r2);

    let f2 = |x: i32| format!("value-{}", x);

    let r3 = Option::fmap(Some(5), f2);
    println!("{:?}", r3);
}
