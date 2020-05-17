
use func_macro::sample_func;
use func_macro2::sample_func2;

sample_func!(a1);
sample_func!(b1);

sample_func2!("a2");
sample_func2!("b2");


fn main() {
    println!("a1 = {}", a1(1));
    println!("b1 = {}", b1(2));

    println!("a2 = {}", a2(1));
    println!("b2 = {}", b2(2));
}