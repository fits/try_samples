use tch::CModule;

use std::env;

fn main() -> Result<(), Box<dyn std::error::Error>> {
    let file = env::args().skip(1).next().ok_or("model file")?;

    let m = CModule::load(file)?;

    let ps = m.named_parameters()?;

    for (n, t) in ps {
        println!("name={}, tensor={:?}", n, t);
    }

    Ok(())
}
