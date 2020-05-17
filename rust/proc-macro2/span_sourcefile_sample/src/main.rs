
use proc_macro2::Span;

fn main() {
    let span = Span::call_site();

    println!("span: {:?}", span);

    #[cfg(procmacro2_semver_exempt)]
    println!("source file: {:?}", span.source_file());
}
