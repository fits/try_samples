
use rusty_v8 as v8;

fn main() {
    let platform = v8::new_default_platform().unwrap();
    v8::V8::initialize_platform(platform);
    v8::V8::initialize();

    let isolate = &mut v8::Isolate::new(Default::default());

    let scope = &mut v8::HandleScope::new(isolate);
    let context = v8::Context::new(scope);
    let scope = &mut v8::ContextScope::new(scope, context);

    let src = r#"
        const vs = [1, 2, 3, 4, 5]
        console.log(vs)
        vs.reduce((acc, v) => acc + v, 0)
    "#;

    v8::String::new(scope, src)
        .map(|code| {
            println!("code: {}", code.to_rust_string_lossy(scope));
            code
        })
        .and_then(|code| v8::Script::compile(scope, code, None))
        .and_then(|script| script.run(scope))
        .and_then(|value| value.to_string(scope))
        .iter()
        .for_each(|s| println!("result: {}", s.to_rust_string_lossy(scope)));
}
