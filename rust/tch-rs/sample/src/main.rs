use tch::Tensor;

fn main() {
    let t = Tensor::from_slice(&[1, 2, 3, 4, 5]);
    let t = t * 3;

    t.print();
}
