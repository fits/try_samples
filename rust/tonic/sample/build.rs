
fn main() {
    println!("*** run topic_build");

    tonic_build::compile_protos("proto/item.proto").unwrap();
}
