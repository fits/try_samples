use wasm_encoder::{
    Component, Module, Function, Instruction, ValType,
    CodeSection, ModuleSection, FunctionSection, TypeSection,
};

use std::env;
use std::fs::File;
use std::io::Write;

fn main() -> std::io::Result<()> {
    let mut comp = Component::new();
    let mut module = Module::new();

    let mut types = TypeSection::new();
    types.function(vec![], vec![ValType::I32]);

    let mut funcs = FunctionSection::new();
    funcs.function(0);

    let mut codes = CodeSection::new();
    let mut f = Function::new(vec![]);

    f.instruction(&Instruction::I32Const(123));
    f.instruction(&Instruction::End);

    codes.function(&f);

    module.section(&types);
    module.section(&funcs);
    module.section(&codes);

    comp.section(&ModuleSection(&module));    

    let wasm_b = comp.finish();

    let file_name = env::args().nth(1).unwrap_or("sample.wasm".to_string());

    let mut file = File::create(file_name)?;

    file.write_all(&wasm_b)?;

    Ok(())
}
