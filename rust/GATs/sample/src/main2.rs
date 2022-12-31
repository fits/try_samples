
trait Functor<A> {
    type Target<B>: Functor<B>;

    fn fmap<F, B>(self, f: F) -> Self::Target<B>
        where F: Fn(A) -> B;
}

impl<A> Functor<A> for Option<A> {
    type Target<B> = Option<B>;

    fn fmap<F, B>(self, f: F) -> Self::Target<B> 
        where F: Fn(A) -> B
    {
        self.map(f)
    }
}

fn main() {
    let f = |x| x * 3;

    let r = Option::fmap(Some(5), f);
    println!("{:?}", r);
    
    let r2 = None::<i32>.fmap(f);
    println!("{:?}", r2);

    let f2 = |x: i32| format!("value:{}", x);

    let r3 = Option::fmap(Some(5), f2);
    println!("{:?}", r3);
}
