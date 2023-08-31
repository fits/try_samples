use wasm_encoder::{
    Component, ComponentExportKind, ComponentExportSection, ComponentExternName,
    ComponentImportSection, ComponentTypeRef, ComponentTypeSection, PrimitiveValType,
};

use std::env;
use std::fs::File;
use std::io::Write;

fn main() -> std::io::Result<()> {
    let mut comp = Component::new();

    let mut types = ComponentTypeSection::new();

    types
        .function()
        .params([("a", PrimitiveValType::S32)])
        .result(PrimitiveValType::String);

    comp.section(&types);

    let mut imports = ComponentImportSection::new();

    imports.import(ComponentExternName::Kebab("f1"), ComponentTypeRef::Func(0));

    comp.section(&imports);

    let mut exports = ComponentExportSection::new();

    exports.export(
        ComponentExternName::Kebab("f2"),
        ComponentExportKind::Func,
        0,
        None,
    );

    comp.section(&exports);

    let wasm_b = comp.finish();

    let file_name = env::args().nth(1).unwrap_or("sample.wasm".to_string());

    let mut file = File::create(file_name)?;

    file.write_all(&wasm_b)?;

    Ok(())
}
