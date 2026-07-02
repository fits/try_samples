#[derive(Clone, Debug)]
enum Item {
    ItemA(A),
    ItemB(B),
}

#[derive(Clone, Debug)]
struct A(isize);

#[derive(Clone, Debug)]
struct B {
    name: String,
    value: isize,
}

trait ItemFunc
where
    Self: Sized,
{
    fn show(&self) -> String;
    fn calc_1(&self, value: isize) -> Self;
    fn calc_2(&self, value: isize) -> Option<Self>;
    fn calc_3(&self, value: isize) -> (Self, bool);
    fn calc_4(&self, value: isize) -> Result<(Self, bool), ()>;
}

impl ItemFunc for Item {
    fn show(&self) -> String {
        match self {
            Self::ItemA(x) => x.show(),
            Self::ItemB(x) => x.show(),
        }
    }

    fn calc_1(&self, value: isize) -> Self {
        match self {
            Self::ItemA(x) => Self::to_adt(x.calc_1(value)),
            Self::ItemB(x) => Self::to_adt(x.calc_1(value)),
        }
    }

    fn calc_2(&self, value: isize) -> Option<Self> {
        match self {
            Self::ItemA(x) => Self::to_adt(x.calc_2(value)),
            Self::ItemB(x) => Self::to_adt(x.calc_2(value)),
        }
    }

    fn calc_3(&self, value: isize) -> (Self, bool) {
        match self {
            Self::ItemA(x) => Self::to_adt(x.calc_3(value)),
            Self::ItemB(x) => Self::to_adt(x.calc_3(value)),
        }
    }

    fn calc_4(&self, value: isize) -> Result<(Self, bool), ()> {
        match self {
            Self::ItemA(x) => Self::to_adt(x.calc_4(value)),
            Self::ItemB(x) => Self::to_adt(x.calc_4(value)),
        }
    }
}

impl ItemFunc for A {
    fn show(&self) -> String {
        format!("A value is {}", self.0)
    }

    fn calc_1(&self, value: isize) -> Self {
        Self(self.0 + value)
    }

    fn calc_2(&self, value: isize) -> Option<Self> {
        if value > 3 {
            Some(self.calc_1(value))
        } else {
            None
        }
    }

    fn calc_3(&self, value: isize) -> (Self, bool) {
        if let Some(x) = self.calc_2(value) {
            (x, true)
        } else {
            (self.clone(), false)
        }
    }

    fn calc_4(&self, value: isize) -> Result<(Self, bool), ()> {
        if let Some(x) = self.calc_2(value) {
            Ok((x, true))
        } else {
            Err(())
        }
    }
}

impl ItemFunc for B {
    fn show(&self) -> String {
        format!("B is (name={}, value={})", self.name, self.value)
    }

    fn calc_1(&self, value: isize) -> Self {
        Self {
            name: self.name.clone(),
            value: self.value + value,
        }
    }

    fn calc_2(&self, value: isize) -> Option<Self> {
        if value > 6 {
            Some(self.calc_1(value))
        } else {
            None
        }
    }

    fn calc_3(&self, value: isize) -> (Self, bool) {
        if let Some(x) = self.calc_2(value) {
            (x, true)
        } else {
            (self.clone(), false)
        }
    }

    fn calc_4(&self, value: isize) -> Result<(Self, bool), ()> {
        if let Some(x) = self.calc_2(value) {
            Ok((x, true))
        } else {
            Err(())
        }
    }
}

impl From<A> for Item {
    fn from(value: A) -> Self {
        Self::ItemA(value)
    }
}

impl From<B> for Item {
    fn from(value: B) -> Self {
        Self::ItemB(value)
    }
}

trait ToAdt<T> {
    type Data;
    fn to_adt(v: T) -> Self::Data;
}

impl<S> ToAdt<S> for Item
where
    S: Into<Self>,
{
    type Data = Self;

    fn to_adt(v: S) -> Self::Data {
        v.into()
    }
}

impl<S> ToAdt<Option<S>> for Item
where
    S: Into<Self>,
{
    type Data = Option<Self>;

    fn to_adt(v: Option<S>) -> Self::Data {
        v.map(S::into)
    }
}

impl<S, E> ToAdt<Result<S, E>> for Item
where
    S: Into<Self>,
{
    type Data = Result<Self, E>;

    fn to_adt(v: Result<S, E>) -> Self::Data {
        v.map(S::into)
    }
}

impl<S, T> ToAdt<(S, T)> for Item
where
    S: Into<Self>,
{
    type Data = (Self, T);

    fn to_adt(v: (S, T)) -> Self::Data {
        (v.0.into(), v.1)
    }
}

impl<S, T, E> ToAdt<Result<(S, T), E>> for Item
where
    S: Into<Self>,
{
    type Data = Result<(Self, T), E>;

    fn to_adt(v: Result<(S, T), E>) -> Self::Data {
        v.map(|x| (x.0.into(), x.1))
    }
}

impl<S, T> ToAdt<Option<(S, T)>> for Item
where
    S: Into<Self>,
{
    type Data = Option<(Self, T)>;

    fn to_adt(v: Option<(S, T)>) -> Self::Data {
        v.map(|x| (x.0.into(), x.1))
    }
}

fn main() {
    let a1: Item = A(12).into();

    println!("{}", a1.show());
    println!("{}", a1.calc_1(5).show());
    println!("calc_2 = {:?}", a1.calc_2(1));
    println!("calc_2 = {:?}", a1.calc_2(9));
    println!("calc_3 = {:?}", a1.calc_3(1));
    println!("calc_3 = {:?}", a1.calc_3(9));
    println!("calc_4 = {:?}", a1.calc_4(1));
    println!("calc_4 = {:?}", a1.calc_4(9));

    let b1: Item = B {
        name: "b1".into(),
        value: 3,
    }
    .into();

    println!("{}", b1.show());
    println!("{}", b1.calc_1(6).show());
    println!("calc_2 = {:?}", b1.calc_2(1));
    println!("calc_2 = {:?}", b1.calc_2(9));
    println!("calc_3 = {:?}", b1.calc_3(1));
    println!("calc_3 = {:?}", b1.calc_3(9));
    println!("calc_4 = {:?}", b1.calc_4(1));
    println!("calc_4 = {:?}", b1.calc_4(9));
}
