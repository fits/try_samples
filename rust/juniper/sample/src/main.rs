use juniper::{execute_sync, EmptySubscription, FieldError, FieldResult, Variables};
use uuid::Uuid;

use std::collections::HashMap;
use std::sync::{Arc, RwLock};

#[derive(Default)]
struct Store {
    list: Arc<RwLock<HashMap<String, Item>>>,
}

#[derive(Debug, Clone, juniper::GraphQLEnum)]
enum Category {
    Standard,
    Extra,
}

#[derive(Debug, Clone, juniper::GraphQLObject)]
struct Item {
    id: String,
    category: Category,
    value: i32,
}

#[derive(Debug, Clone, juniper::GraphQLInputObject)]
struct CreateItem {
    category: Category,
    value: i32,
}

impl juniper::Context for Store {}

fn error(msg: &str) -> FieldError {
    FieldError::new(msg, juniper::Value::Null)
}

#[derive(Debug)]
struct Query;

#[juniper::graphql_object(context = Store)]
impl Query {
    fn find(ctx: &Store, id: String) -> FieldResult<Item> {
        let s = ctx
            .list
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
            id: format!("data-{}", Uuid::new_v4()),
            category: input.category,
            value: input.value,
        };

        let mut s = ctx
            .list
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

fn main() {
    let ctx = Store::default();
    let schema = Schema::new(Query, Mutation, EmptySubscription::new());

    let m = r#"
        mutation {
            create(input: { category: STANDARD, value: 12 }) {
                id
            }
        }
    "#;

    let (r1, _) = execute_sync(m, None, &schema, &Variables::new(), &ctx).unwrap();
    println!("*** r1 = {:?}", r1);

    let id = r1
        .as_object_value()
        .and_then(|v| v.get_field_value("create"))
        .and_then(|v| v.as_object_value())
        .and_then(|v| v.get_field_value("id"))
        .and_then(|v| v.as_scalar())
        .unwrap();

    println!("*** id = {:?}", id);

    let q = r#"
        query FindItem($p: String!) {
            find(id: $p) {
                id
                category
                value
            }
        }
    "#;

    let mut vars = Variables::new();

    vars.insert("p".to_string(), juniper::InputValue::scalar(id.clone()));

    let (r2, _) = execute_sync(q, None, &schema, &vars, &ctx).unwrap();
    println!("*** r2 = {:?}", r2);
}
