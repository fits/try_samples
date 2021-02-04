
use juniper::{
    EmptySubscription, FieldResult, FieldError,
    ID, RootNode, http::GraphQLRequest
};
use tide::{Body, Request, Response, StatusCode};
use uuid::Uuid;
use bson::{Bson, doc};
use mongodb::{Client, options::ClientOptions, Collection};
use serde::{Deserialize, Serialize};

use std::sync::Arc;

#[derive(Clone)]
struct Store(Collection);

impl juniper::Context for Store {}

#[derive(Debug, Clone, juniper::GraphQLObject, Deserialize, Serialize)]
struct Item {
    #[serde(rename = "_id")]
    id: ID,
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

#[juniper::graphql_object(Context = Store)]
impl Query {
    async fn find(ctx: &Store, id: ID) -> FieldResult<Option<Item>> {
        let query = doc! {"_id": id.to_string()};

        ctx.0
            .find_one(query, None)
            .await
            .map_err(|e|
                error(format!("find error: {:?}", e).as_str())
            )
            .map(|r|
                r.and_then(|b| 
                    bson::from_document::<Item>(b).ok()
                )
            )
    }
}

#[derive(Debug)]
struct Mutation;

#[juniper::graphql_object(Context = Store)]
impl Mutation {
    async fn create(ctx: &Store, input: ItemInput) -> FieldResult<Item> {
        let item = Item {
            id: format!("item-{}", Uuid::new_v4()).into(),
            name: input.name,
            value: input.value,
        };

        if let Ok(Bson::Document(doc)) = bson::to_bson(&item) {
            println!("to_bson: {:?}", doc);

            ctx.0
                .insert_one(doc, None)
                .await
                .map(|_| item)
                .map_err(|e|
                    error(format!("insert error: {:?}", e).as_str())
                )
        } else {
            Err(error("failed create"))
        }
    }
}

type Schema = RootNode<'static, Query, Mutation, EmptySubscription<Store>>;

type State = (Store, Arc<Schema>);

#[async_std::main]
async fn main() -> tide::Result<()> {
    let opt = ClientOptions::parse("mongodb://localhost").await?;
    let mongo = Client::with_options(opt)?;

    let state = (
        Store(mongo.database("items").collection("data")),
        Arc::new(Schema::new(Query, Mutation, EmptySubscription::new())),
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

    let res = query.execute(&state.1, &state.0).await;

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