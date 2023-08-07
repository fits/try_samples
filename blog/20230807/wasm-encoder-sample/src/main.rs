
use wasm_encoder::{
    CodeSection, ExportKind, ExportSection, Function, FunctionSection,
    Instruction, Module, TypeSection, ValType,
};

use std::env;
use std::fs::File;
use std::io::Write;

type Result<T> = std::result::Result<T, Box<dyn std::error::Error>>;

fn main() -> Result<()> {
    let mut module = Module::new();

    let mut types = TypeSection::new();
    types.function(vec![ValType::I32, ValType::I32], vec![ValType::I32]);
    
    module.section(&types);

    let mut funcs = FunctionSection::new();
    funcs.function(0);

    module.section(&funcs);

    let mut exports = ExportSection::new();
    exports.export("calc", ExportKind::Func, 0);

    module.section(&exports);

    let mut codes = CodeSection::new();
    let mut calc = Function::new(vec![]);

    calc.instruction(&Instruction::LocalGet(0));
    calc.instruction(&Instruction::LocalGet(1));
    calc.instruction(&Instruction::I32Add);
    calc.instruction(&Instruction::I32Const(2));
    calc.instruction(&Instruction::I32Mul);
    calc.instruction(&Instruction::End);

    codes.function(&calc);
    module.section(&codes);

    let wasm_b = module.finish();

    wasmparser::validate(&wasm_b)?;

    let file_name = env::args().nth(1).unwrap_or("sample.wasm".to_string());

    let mut file = File::create(file_name)?;
    file.write_all(&wasm_b)?;

    Ok(())
}
