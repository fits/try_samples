use exports::wasi::cli::run::Guest;
use wasi::cli::stdout::get_stdout;
use wasi::filesystem::preopens::get_directories;

wit_bindgen::generate!({
    world: "proc",
    generate_all
});

struct Host;

impl Guest for Host {
    fn run() -> Result<(), ()> {
        let out = get_stdout();

        let ds = get_directories();

        if ds.is_empty() {
            out.write("get_directories is empty\n".as_bytes())
                .map_err(|_| ())?;
        } else {
            for (d, s) in get_directories() {
                let msg = format!("path={}, descriptor:{:?}\n", s, d);
                out.write(msg.as_bytes()).map_err(|_e| ())?;
            }
        }

        out.flush().map_err(|_e| ())
    }
}

export!(Host);
