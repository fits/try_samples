#[allow(warnings)]
mod bindings;

use bindings::component::item::itemfind::find_item;
use bindings::exports::wasi::cli::run::Guest;
use bindings::wasi::cli::stdout::get_stdout;

struct Component;

impl Guest for Component {
    fn run() -> Result<(), ()> {
        let item = find_item(&"item-1".to_string()).ok_or(())?;

        log(&format!("id={}, price={}", item.id, item.price)).map_err(|_| ())?;

        Ok(())
    }
}

fn log(msg: &str) -> Result<(), Box<dyn std::error::Error>> {
    let stdout = get_stdout();

    stdout.write(msg.as_bytes())?;
    stdout.flush()?;

    Ok(())
}

bindings::export!(Component with_types_in bindings);
