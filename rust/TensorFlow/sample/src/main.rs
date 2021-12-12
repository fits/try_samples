use tensorflow::{Scope, Session, SessionOptions, SessionRunArgs, Tensor};
use tensorflow::ops;

fn main() -> Result<(), Box<dyn std::error::Error>> {
    let mut root = Scope::new_root_scope();

    let c1 = ops::constant(
        Tensor::new(&[1, 3]).with_values(&[1, 2, 3])?,
        &mut root
    )?;

    let g = root.graph_mut();

    let opts = SessionOptions::new();
    let session = Session::new(&opts, &g)?;

    let mut run_args = SessionRunArgs::new();
    let output_fetch = run_args.request_fetch(&c1, 0);

    session.run(&mut run_args)?;

    let res = run_args.fetch::<i32>(output_fetch)?;

    println!("{}", res);

    Ok(())
}
