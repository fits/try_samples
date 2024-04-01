use burn_import::onnx::ModelGen;

fn main() {
    ModelGen::new()
        .input("sample.onnx")
        .out_dir("model/")
        .run_from_script();
}
