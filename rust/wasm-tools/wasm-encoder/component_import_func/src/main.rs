use wasm_encoder::{
    Component, ComponentExternName, PrimitiveValType, ComponentTypeRef,
    ComponentTypeSection, ComponentImportSection,
};

use std::env;
use std::fs::File;
use std::io::Write;

fn main() -> std::io::Result<()> {
    let mut types = ComponentTypeSection::new();

    types
        .function()
        .params([("a", PrimitiveValType::S32)])
        .result(PrimitiveValType::String);

    let mut imports = ComponentImportSection::new();
    let name = ComponentExternName::Kebab("f");

    imports.import(name, ComponentTypeRef::Func(0));

    let mut comp = Component::new();

    comp.section(&types);
    comp.section(&imports);

    let wasm_b = comp.finish();

    let file_name = env::args().nth(1).unwrap_or("sample.wasm".to_string());

    let mut file = File::create(file_name)?;

    file.write_all(&wasm_b)?;

    Ok(())
}
