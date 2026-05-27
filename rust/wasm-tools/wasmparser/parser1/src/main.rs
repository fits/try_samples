use std::env;

use wasmparser::{ElementItems, Parser, Payload};

fn main() -> Result<(), Box<dyn std::error::Error>> {
    let wasm_file = env::args().skip(1).next().ok_or("need wasm file")?;
    let buf = std::fs::read(&wasm_file)?;

    let parser = Parser::new(0);

    for payload in parser.parse_all(&buf) {
        let p = payload?;

        match p {
            Payload::Version {
                num,
                encoding,
                range,
            } => println!(
                "version: num={}, encoding={:?}, range={:?}",
                num, encoding, range
            ),
            Payload::CustomSection(r) => println!(
                "custom section: name={}, data={:?}",
                r.name(),
                str::from_utf8(r.data())
            ),
            Payload::ModuleSection { .. } => println!("module section: "),
            Payload::ImportSection(s) => {
                println!("import section:");

                for x in s {
                    println!("    {:?}", x?);
                }
            }
            Payload::ExportSection(s) => {
                println!("export section:");

                for x in s {
                    println!("    {:?}", x?);
                }
            }
            Payload::ElementSection(s) => {
                println!("element section:");

                for x in s {
                    match x?.items {
                        ElementItems::Functions(f) => {
                            for y in f {
                                println!("    {:?}", y);
                            }
                        }
                        ElementItems::Expressions(t, ..) => println!("    expression: {}", t),
                    }
                }
            }
            Payload::TableSection(s) => {
                println!("table section:");

                for x in s {
                    println!("    {:?}", x?);
                }
            }
            Payload::TypeSection(s) => {
                println!("type section:");

                for x in s {
                    for y in x?.types() {
                        println!("    {}", y);
                    }
                }
            }
            Payload::ComponentSection { .. } => println!("component section:"),
            Payload::ComponentTypeSection(s) => {
                println!("component type section:");

                for x in s {
                    println!("    {:?}", x?);
                }
            }
            Payload::ComponentImportSection(s) => {
                println!("component import section:");

                for x in s {
                    println!("    {:?}", x?);
                }
            }
            Payload::ComponentExportSection(s) => {
                println!("component export section:");

                for x in s {
                    println!("    {:?}", x?);
                }
            }
            Payload::End(v) => println!("end: {}", v),
            _ => (),
        }
    }

    Ok(())
}
