
trait HKT<B> {
    type Current;
    type Target;
}

impl<A, B> HKT<B> for Option<A> {
    type Current = A;
    type Target = Option<B>;
}

trait Functor<B> : HKT<B> {
    fn fmap<F>(self, f: F) -> Self::Target
        where F: Fn(Self::Current) -> B;
}

impl<A, B> Functor<B> for Option<A> {
    fn fmap<F>(self, f: F) -> Self::Target 
        where F: Fn(Self::Current) -> B
    {
        self.map(f)
    }
}

impl<A, B> HKT<B> for Vec<A> {
    type Current = A;
    type Target = Vec<B>;
}
/*
impl<A, B> Functor<B> for Vec<A> {
    fn fmap<F>(self, f: F) -> Self::Target 
        where 
            F: Fn(Self::Current) -> B
    {
        let mut res = vec![];

        for r in self {
            res.push(f(r));
        }

        res
    }
}
*/

impl<A, B> Functor<B> for Vec<A> 
where
    A: Clone
{
    fn fmap<F>(self, f: F) -> Self::Target 
        where 
            F: Fn(Self::Current) -> B
    {
        self.iter().cloned().map(f).collect()
    }
}

fn sample() -> fn(i32) -> i32 {
    |x| x * 3
}

fn sample2(x: i32) -> i32 {
    x * 4
}

fn main() {
    let f = |x| x * 2;

    let r = Option::fmap(Some(5), f);
    println!("{:?}", r);

    let r2 = Some(10).fmap(f);
    println!("{:?}", r2);
    
    let r3 = None::<i32>.fmap(f);
    println!("{:?}", r3);

    let r4 = Some(10).fmap(sample());
    println!("{:?}", r4);

    let r5 = Some(10).fmap(sample2);
    println!("{:?}", r5);

    let a = 10;

    let f2 = move |b: i32| a * b;

    let r6 = Option::fmap(Some(5), f2);
    println!("{:?}", r6);

    let d1 = vec![1, 2, 3, 4, 5];

    println!("{:?}", d1.fmap(sample()));
}
