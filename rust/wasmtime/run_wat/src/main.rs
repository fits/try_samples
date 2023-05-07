use anyhow::Result;
use wasmtime::*;

fn main() -> Result<()> {
    let wat = r#"
        (module
            (func (export "calc") (param i32) (result i32)
                local.get 0
                i32.const 2
                i32.mul
            )
        )
    "#;

    let module = Module::new(&Engine::default(), wat)?;

    let mut store = Store::new(module.engine(), ());
    let instance = Instance::new(&mut store, &module, &[])?;

    let func = instance.get_typed_func::<i32, i32>(&mut store, "calc").unwrap();

    let res = func.call(&mut store, 15)?;

    println!("result: {}", res);

    Ok(())
}
