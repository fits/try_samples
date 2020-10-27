
use juniper::{FieldResult, FieldError, RootNode, http::GraphQLRequest};
use tide::{Body, Request, Response, StatusCode};
use uuid::Uuid;

use std::collections::HashMap;
use std::sync::{Arc, RwLock};

#[derive(Default, Clone)]
struct Store(Arc<RwLock<HashMap<String, Item>>>);

impl juniper::Context for Store {}

#[derive(Debug, Clone, juniper::GraphQLObject)]
struct Item {
    id: String,
    name: String,
    value: i32,
}

#[derive(Debug, Clone, juniper::GraphQLInputObject)]
struct ItemInput {
    name: String,
    value: i32,
}

#[derive(Debug)]
struct Query;

#[juniper::object(Context = Store)]
impl Query {
    fn find(ctx: &Store, id: String) -> FieldResult<Item> {
        let s = ctx.0
            .read()
            .map_err(|e| 
                error(format!("read lock error: {:?}", e).as_str())
            )?;

        s.get(&id).cloned().ok_or(error("not found"))
    }
}

#[derive(Debug)]
struct Mutation;

#[juniper::object(Context = Store)]
impl Mutation {
    fn create(ctx: &Store, input: ItemInput) -> FieldResult<Item> {
        let data = Item {
            id: format!("item-{}", Uuid::new_v4()),
            name: input.name,
            value: input.value,
        };

        let mut s = ctx.0
            .write()
            .map_err(|e|
                error(format!("write lock error: {:?}", e).as_str())
            )?;

        if s.insert(data.id.clone(), data.clone()).is_none() {
            Ok(data)
        } else {
            Err(error("duplicated id"))
        }
    }
}

type Schema = RootNode<'static, Query, Mutation>;

type State = (Store, Arc<Schema>);

#[async_std::main]
async fn main() -> tide::Result<()> {
    let state = (
        Store::default(),
        Arc::new(Schema::new(Query, Mutation)),
    );

    let mut app = tide::with_state(state);

    app.at("/graphql").post(handle_graphql);

    app.listen("127.0.0.1:8080").await?;

    Ok(())
}

fn error(msg: &str) -> FieldError {
    FieldError::new(msg, juniper::Value::Null)
}

async fn handle_graphql(mut req: Request<State>) -> tide::Result {
    let query: GraphQLRequest = req.body_json().await?;
    let state = req.state();

    println!("{:?}", query);

    let res = query.execute(&state.1, &state.0);

    let status = if res.is_ok() {
        StatusCode::Ok
    } else {
        StatusCode::BadRequest
    };

    Body::from_json(&res)
        .map(|b| 
            Response::builder(status)
                .body(b)
                .build()
        )
}