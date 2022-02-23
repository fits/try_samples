use juniper::{execute_sync, EmptySubscription, FieldError, FieldResult, Variables};

use std::collections::HashMap;
use std::sync::RwLock;

#[derive(Default, Debug)]
struct Store {
    store: RwLock<HashMap<String, Item>>,
}

impl juniper::Context for Store {}

#[derive(Debug, Clone, juniper::GraphQLObject)]
struct Item {
    id: String,
    value: i32,
}

#[derive(Debug, Clone, juniper::GraphQLInputObject)]
struct CreateItem {
    id: String,
    value: i32,
}

fn error(msg: &str) -> FieldError {
    FieldError::new(msg, juniper::Value::Null)
}

#[derive(Debug)]
struct Query;

#[juniper::graphql_object(context = Store)]
impl Query {
    fn find(ctx: &Store, id: String) -> FieldResult<Item> {
        let s = ctx
            .store
            .read()
            .map_err(|e| error(format!("read lock: {:?}", e).as_str()))?;

        s.get(&id).cloned().ok_or(error("not found"))
    }
}

#[derive(Debug)]
struct Mutation;

#[juniper::graphql_object(context = Store)]
impl Mutation {
    fn create(ctx: &Store, input: CreateItem) -> FieldResult<Item> {
        let data = Item {
            id: input.id,
            value: input.value,
        };

        let mut s = ctx
            .store
            .write()
            .map_err(|e| error(format!("write lock: {:?}", e).as_str()))?;

        if s.insert(data.id.clone(), data.clone()).is_none() {
            Ok(data)
        } else {
            Err(error("duplicated id"))
        }
    }
}

type Schema = juniper::RootNode<'static, Query, Mutation, EmptySubscription<Store>>;

struct Context {
    context: Store,
    schema: Schema,
}

#[no_mangle]
extern fn _new_string(size: usize) -> *mut u8 {
    let v = vec![0; size];

    Box::into_raw(v.into_boxed_slice()) as *mut u8
}

#[no_mangle]
extern fn _drop_string(ptr: *mut u8) {
    unsafe {
        Box::from_raw(ptr);
    }
}

#[no_mangle]
extern fn _drop_result(ptr: *mut String) {
    unsafe {
        Box::from_raw(ptr);
    }
}

#[no_mangle]
extern fn _result_ptr(ptr: *mut String) -> *const u8 {
    unsafe {
        let s = Box::from_raw(ptr);
        let r = s.as_ptr();

        Box::into_raw(s);

        r
    }
}

#[no_mangle]
extern fn _result_size(ptr: *mut String) -> usize {
    unsafe {
        let s = Box::from_raw(ptr);
        let r = s.len();

        Box::into_raw(s);

        r
    }
}

#[no_mangle]
extern fn open() -> *mut Context {
    let context = Store::default();
    let schema = Schema::new(Query, Mutation, EmptySubscription::new());

    let d = Box::new(Context{ context, schema });
    Box::into_raw(d)
}

#[no_mangle]
extern fn close(ptr: *mut Context) {
    unsafe {
        Box::from_raw(ptr);
    }
}

#[no_mangle]
extern fn query(ptr: *mut Context, sptr: *const u8, len: usize) -> *mut String {
    unsafe {
        let ctx = Box::from_raw(ptr);

        let slice = std::slice::from_raw_parts(sptr, len);
        let q = std::str::from_utf8_unchecked(slice);

        let r = execute_sync(q, None, &ctx.schema, &Variables::new(), &ctx.context);

        let msg = match r {
            Ok((v, _)) => format!("{}", v),
            Err(e) => format!("{}", e),
        };

        Box::into_raw(ctx);

        Box::into_raw(Box::new(msg))
    }
}
