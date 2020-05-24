
fn main() {
    println!("*** run build");

    tonic_build::compile_protos("proto/pubsub.proto").unwrap();
}
