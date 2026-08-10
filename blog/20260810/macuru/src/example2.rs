use macuru::adt;

adt!( Data = A | B derive Debug );

#[derive(Debug)]
pub struct A(usize);

#[derive(Debug)]
pub struct B {
    value: isize,
}

fn main() -> Result<(), ()> {
    let d1: Data = A(1).into();

    println!("d1 = {:?}", d1);
    println!("A.0 = {}", A::try_from(d1)?.0);

    let d2: Data = B { value: 2 }.into();

    println!("d2 = {:?}", d2);
    println!("B.value = {}", B::try_from(d2)?.value);

    Ok(())
}
