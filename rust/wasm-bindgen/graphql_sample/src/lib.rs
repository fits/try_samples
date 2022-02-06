use juniper::{execute_sync, EmptySubscription, FieldError, FieldResult, Variables};
use wasm_bindgen::prelude::*;

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

#[wasm_bindgen]
pub struct Context {
    context: Store,
    schema: Schema,
}

#[wasm_bindgen]
impl Context {
    #[wasm_bindgen(constructor)]
    pub fn new() -> Self {
        let context = Store::default();
        let schema = Schema::new(Query, Mutation, EmptySubscription::new());
    
        Self{ context, schema }
    }

    pub fn query(&self, q: String) -> String {
        let r = execute_sync(&q, None, &self.schema, &Variables::new(), &self.context);

        match r {
            Ok((v, _)) => format!("{}", v),
            Err(e) => format!("{}", e),
        }
    }
}
