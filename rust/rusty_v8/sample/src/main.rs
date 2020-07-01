
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

    let code = v8::String::new(scope, src).unwrap();

    println!("code: {}", code.to_rust_string_lossy(scope));

    let script = v8::Script::compile(scope, code, None).unwrap();

    let r = script.run(scope).unwrap();
    let v = r.to_string(scope).unwrap();

    println!("result: {}", v.to_rust_string_lossy(scope));
}
