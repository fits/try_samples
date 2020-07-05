
use rusty_v8 as v8;
use rusty_v8::inspector::*;

struct InspectorClient(V8InspectorClientBase);

impl InspectorClient {
    fn new() -> Self {
        Self(V8InspectorClientBase::new::<Self>())
    }
}

impl V8InspectorClientImpl for InspectorClient {
    fn base(&self) -> &V8InspectorClientBase {
        &self.0
    }

    fn base_mut(&mut self) -> &mut V8InspectorClientBase {
        &mut self.0
    }

    fn console_api_message(&mut self, context_group_id: i32, 
        level: i32, message: &StringView, _url: &StringView, 
        _line_number: u32, _column_number: u32, _stack_trace: &mut V8StackTrace) {

            println!(
                "*** context_group_id={}, level={}, message={}", 
                context_group_id, 
                level, 
                message
            );
    }
}

fn main() {
    let platform = v8::new_default_platform().unwrap();
    v8::V8::initialize_platform(platform);
    v8::V8::initialize();

    let isolate = &mut v8::Isolate::new(Default::default());

    let mut client = InspectorClient::new();
    let mut inspector = V8Inspector::create(isolate, &mut client);

    let scope = &mut v8::HandleScope::new(isolate);
    let context = v8::Context::new(scope);
    let scope = &mut v8::ContextScope::new(scope, context);

    inspector.context_created(context, 123, StringView::empty());

    let src = r#"
        console.log('log')
        console.debug('debug')
        console.info('info')
        console.error('error')
        console.warn('warn')
    "#;

    v8::String::new(scope, src)
        .and_then(|code| v8::Script::compile(scope, code, None))
        .and_then(|script| script.run(scope))
        .and_then(|value| value.to_string(scope))
        .iter()
        .for_each(|s| println!("result: {}", s.to_rust_string_lossy(scope)));
}
