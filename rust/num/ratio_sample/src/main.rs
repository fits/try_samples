use num_rational::Ratio;
use num_traits::cast::ToPrimitive;

fn main() {
    let r = Ratio::new(8, 100);

    println!("r={:?}, to_f32={:?}", r, r.to_f32());

    let r2 = r * 1230;

    println!("r2={:?}, to_f32={:?}, round={}, ceil={}, floor={}", r2, r2.to_f32(), r2.round(), r2.ceil(), r2.floor());

    let r3 = r + 2;

    println!("r3={:?}, to_f32={:?}", r3, r3.to_f32());

}
