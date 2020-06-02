
fn main() {
    prost_build::compile_protos(&["proto/item.proto"], &["proto/"]).unwrap();
}
