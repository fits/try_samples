
use juniper::{
    graphql_interface, graphql_object,
    EmptySubscription, FieldResult, FieldError,
    GraphQLObject, GraphQLInputObject,
    ID, RootNode, http::GraphQLRequest
};
use tide::{Body, Request, Response, StatusCode};
use uuid::Uuid;
use bson::{Bson, doc};
use mongodb::{Client, options::ClientOptions, Collection};
use serde::{Deserialize, Serialize};

use std::sync::Arc;

type StoreResult<T> = Result<T, Box<dyn std::error::Error>>;

#[derive(Clone)]
struct Store(Collection);

impl juniper::Context for Store {}

impl Store {
    async fn save(&self, event: &StoredEvent) -> StoreResult<()> {
        let doc = bson::to_bson(event)?;

        if let Bson::Document(d) = doc {
            println!("to_bson: {:?}", d);

            self.0.insert_one(d, None).await?;

            Ok(())

        } else {
            Err("invalid type".into())
        }
    }

    async fn load(&self, id: ID) -> StoreResult<Option<StoredEvent>> {
        let query = doc! {"_id": id.to_string()};

        let r = self.0
            .find_one(query, None)
            .await?;

        if let Some(d) = r {
            let s = bson::from_document::<StoredEvent>(d)?;

            Ok(Some(s))
        } else {
            Ok(None)
        }
    }
}

#[derive(Debug, Deserialize, Serialize)]
#[serde(tag = "type")]
enum StoredEvent {
    CreatedEvent(Created),
    DeletedEvent(Deleted),
}

impl StoredEvent {
    fn to_event(&self) -> EventValue {
        match self {
            Self::CreatedEvent(e) => e.clone().into(),
            Self::DeletedEvent(e) => e.clone().into(),
        }
    }
}

#[derive(Debug, Clone, GraphQLInputObject)]
struct CreateInput {
    target: ID,
    value: i32,
}

#[derive(Debug, Clone, GraphQLInputObject)]
struct DeleteInput {
    target: ID,
    reason: Option<String>,
}

#[graphql_interface(for = [Created, Deleted])]
trait Event {
    fn id(&self) -> ID;
    fn target(&self) -> ID;
}

#[derive(Debug, Clone, GraphQLObject, Deserialize, Serialize)]
#[graphql(impl = EventValue)]
struct Created {
    #[serde(rename = "_id")]
    id: ID,
    target: ID,
    value: i32,
}

#[graphql_interface]
impl Event for Created {
    fn id(&self) -> ID {
        self.id.clone()
    }

    fn target(&self) -> ID {
        self.target.clone()
    }
}

#[derive(Debug, Clone, GraphQLObject, Deserialize, Serialize)]
#[graphql(impl = EventValue)]
struct Deleted {
    #[serde(rename = "_id")]
    id: ID,
    target: ID,
    reason: Option<String>,
}

#[graphql_interface]
impl Event for Deleted {
    fn id(&self) -> ID {
        self.id.clone()
    }

    fn target(&self) -> ID {
        self.target.clone()
    }
}

#[derive(Debug)]
struct Query;

#[graphql_object(Context = Store)]
impl Query {
    async fn find(ctx: &Store, id: ID) -> FieldResult<Option<EventValue>> {
        let r = ctx.load(id)
            .await
            .map_err(|e|
                error(format!("{:?}", e).as_str())
            )?;

        Ok(r.map(|s| s.to_event()))
    }
}

#[derive(Debug)]
struct Mutation;

#[graphql_object(Context = Store)]
impl Mutation {
    async fn create(ctx: &Store, input: CreateInput) -> FieldResult<EventValue> {
        let event = Created {
            id: format!("event-{}", Uuid::new_v4()).into(),
            target: input.target,
            value: input.value,
        };

        ctx.save(&StoredEvent::CreatedEvent(event.clone()))
            .await
            .map(|_| event.into())
            .map_err(|e|
                error(format!("{:?}", e).as_str())
            )
    }

    async fn delete(ctx: &Store, input: DeleteInput) -> FieldResult<EventValue> {
        let event = Deleted {
            id: format!("event-{}", Uuid::new_v4()).into(),
            target: input.target,
            reason: input.reason,
        };

        ctx.save(&StoredEvent::DeletedEvent(event.clone()))
            .await
            .map(|_| event.into())
            .map_err(|e|
                error(format!("{:?}", e).as_str())
            )
    }
}

type Schema = RootNode<'static, Query, Mutation, EmptySubscription<Store>>;

type State = (Store, Arc<Schema>);

#[async_std::main]
async fn main() -> tide::Result<()> {
    let opt = ClientOptions::parse("mongodb://localhost").await?;
    let mongo = Client::with_options(opt)?;

    let state = (
        Store(mongo.database("events").collection("data")),
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