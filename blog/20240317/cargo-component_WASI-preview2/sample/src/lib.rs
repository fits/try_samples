#[allow(warnings)]
mod bindings;

use bindings::exports::wasi::cli::run::Guest;
use bindings::wasi::cli::stdout::get_stdout;
use bindings::wasi::clocks::wall_clock;

struct Component;

impl Guest for Component {
    fn run() -> Result<(), ()> {
        log("called run").map_err(|_| ())?;
        Ok(())
    }
}

fn log(msg: &str) -> Result<(), Box<dyn std::error::Error>> {
    let s = format!("msg={}, {:?}", msg, wall_clock::now());

    let stdout = get_stdout();

    stdout.write(s.as_bytes())?;
    stdout.flush()?;

    Ok(())
}

bindings::export!(Component with_types_in bindings);
