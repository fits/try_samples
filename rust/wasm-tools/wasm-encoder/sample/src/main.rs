
use wasm_encoder::{
    CodeSection, ExportKind, ExportSection, Function, FunctionSection,
    Instruction, Module, TypeSection, ValType,
};

use std::env;
use std::fs::File;
use std::io::Write;

fn main() -> std::io::Result<()> {
    let mut module = Module::new();

    let mut types = TypeSection::new();
    types.function(vec![ValType::I32], vec![ValType::I32]);

    let mut functions = FunctionSection::new();
    functions.function(0);

    let mut exports = ExportSection::new();
    exports.export("sample", ExportKind::Func, 0);

    let mut codes = CodeSection::new();
    let mut sample = Function::new(vec![]);

    sample.instruction(&Instruction::LocalGet(0));
    sample.instruction(&Instruction::I32Const(2));
    sample.instruction(&Instruction::I32Mul);
    sample.instruction(&Instruction::End);

    codes.function(&sample);

    module.section(&types);
    module.section(&functions);
    module.section(&exports);
    module.section(&codes);

    let wasm_b = module.finish();

    let file_name = env::args().nth(1).unwrap_or_default();

    let mut file = File::create(file_name)?;

    file.write_all(&wasm_b)?;

    Ok(())
}
