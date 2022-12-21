
mod delivery;

use axum::{
    Router, 
    extract::State,
    routing::post, 
    response::Json, 
};
use chrono::{DateTime, Utc};
use juniper::{
    EmptySubscription, FieldResult, GraphQLObject, ID, RootNode, 
    http::{GraphQLRequest, GraphQLResponse}
};
use mongodb::{
    Client,
    Collection,
    options::{ClientOptions, FindOneAndUpdateOptions, ReturnDocument}, 
    bson::{doc, to_bson}
};
use serde::{Deserialize, Serialize};

use std::env;
use std::fmt;
use std::sync::Arc;

use delivery::*;

type Revision = u32;

#[derive(Clone, Debug, Deserialize, Serialize)]
struct StoredEvent {
    event: delivery::Event,
    created_at: DateTime<Utc>
}

#[derive(Clone, Debug, Deserialize, Serialize)]
struct StoredState {
    _id: String,
    rev: Revision,
    state: Delivery,
    events: Vec<StoredEvent>
}

#[derive(Debug, Clone)]
pub enum AppError {
    InvalidRevision(Revision),
    Exists(ID),
}

impl fmt::Display for AppError {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::InvalidRevision(r) => write!(f, "invalid revision: {}", r),
            Self::Exists(t) => write!(f, "exists delivery: tracking_id={}", t),
        }
    }
}

impl std::error::Error for AppError {}

#[derive(Clone, Debug)]
pub struct Store(Collection<StoredState>);

impl juniper::Context for Store {}

#[juniper::graphql_interface(name = "Delivery", for = [NotReceivedDelivery, InPortDelivery, OnBoardCarrierDelivery, ClaimedDelivery])]
trait DeliveryInterface {
    fn tracking_id(&self) -> &ID;
}

#[juniper::graphql_interface(name = "Location", for = [InPortDelivery, OnBoardCarrierDelivery, ClaimedDelivery])]
trait LocationInterface {
    fn location(&self) -> &ID;
}

#[juniper::graphql_interface(name = "OnBoard", for = [OnBoardCarrierDelivery])]
trait OnBoardInterface {
    fn voyage_no(&self) -> &ID;
}

#[juniper::graphql_interface(name = "Claim", for = [ClaimedDelivery])]
trait ClaimInterface {
    fn claimed_time(&self) -> &DateTime<Utc>;
}

#[derive(GraphQLObject)]
#[graphql(impl = DeliveryInterfaceValue)]
struct NotReceivedDelivery {
    tracking_id: ID,
}

#[derive(GraphQLObject)]
#[graphql(impl = [DeliveryInterfaceValue, LocationInterfaceValue])]
struct InPortDelivery {
    tracking_id: ID,
    location: ID,
}

#[derive(GraphQLObject)]
#[graphql(impl = [DeliveryInterfaceValue, LocationInterfaceValue, OnBoardInterfaceValue])]
struct OnBoardCarrierDelivery {
    tracking_id: ID,
    location: ID,
    voyage_no: ID,
}

#[derive(GraphQLObject)]
#[graphql(impl = [DeliveryInterfaceValue, LocationInterfaceValue, ClaimInterfaceValue])]
struct ClaimedDelivery {
    tracking_id: ID,
    location: ID,
    claimed_time: DateTime<Utc>,
}

impl From<Delivery> for DeliveryInterfaceValue {
    fn from(state: Delivery) -> Self {
        match state {
            Delivery::Nothing =>
                NotReceivedDelivery { tracking_id: ID::new("") }.into(),
            Delivery::NotReceived { tracking_id } => 
                NotReceivedDelivery { tracking_id: ID::new(tracking_id) }.into(),
            Delivery::InPort { tracking_id, location } => 
                InPortDelivery { tracking_id: ID::new(tracking_id), location: ID::new(location) }.into(),
            Delivery::OnBoardCarrier { tracking_id, voyage_no, location } =>
                OnBoardCarrierDelivery { tracking_id: ID::new(tracking_id), location: ID::new(location), voyage_no: ID::new(voyage_no) }.into(),
            Delivery::Claimed { tracking_id, location, claimed_time } =>
                ClaimedDelivery { tracking_id: ID::new(tracking_id), location: ID::new(location), claimed_time: claimed_time.into() }.into(),
        }
    }
}

async fn load_state(ctx: &Store, tracking_id: String) -> FieldResult<Option<Delivery>> {
    let q = doc! { "_id": tracking_id };
    let d = ctx.0.find_one(q, None).await?;

    Ok(d.map(|s| s.state))    
}

async fn save_with_action(ctx: &Store, tracking_id: &str, cmd: &Command) -> FieldResult<Option<Delivery>> {
    let r = ctx.0.find_one(doc! { "_id": tracking_id }, None).await?;

    if let Some(s) = r {
        let (c, e) = s.state.action(&cmd)?;

        let flt = doc! { "$and": [ { "_id": tracking_id }, { "rev": s.rev } ] };

        let upd = doc! {
            "$inc": { "rev": 1 },
            "$set": {
                "state": to_bson(&c)?
            },
            "$push": {
                "events": to_bson(&StoredEvent { event: e, created_at: Utc::now() })?
            }
        };

        let opts = FindOneAndUpdateOptions::builder()
            .return_document(ReturnDocument::After)
            .build();

        let r = ctx.0.find_one_and_update(flt, upd, Some(opts)).await?;

        if let Some(s) = r {
            Ok(Some(s.state))
        } else {
            Err(AppError::InvalidRevision(s.rev).into())
        }
    } else {
        Ok(None)
    }
}

struct Query;

#[juniper::graphql_object(context = Store)]
impl Query {
    async fn find(ctx: &Store, tracking_id: ID) -> FieldResult<Option<DeliveryInterfaceValue>> {
        let s = load_state(ctx, tracking_id.to_string()).await?;

        Ok(s.map(|s| s.into()))
    }
}

struct Mutation;

#[juniper::graphql_object(context = Store)]
impl Mutation {
    async fn create(ctx: &Store, tracking_id: ID) -> FieldResult<DeliveryInterfaceValue> {
        let cmd = Command::Create(tracking_id.to_string());

        let (s, e) = Delivery::Nothing.action(&cmd)?;

        let d = StoredState {
            _id: tracking_id.to_string(),
            rev: 1,
            state: s.clone(),
            events: vec![StoredEvent { event: e, created_at: Utc::now() }]
        };

        ctx.0.insert_one(d, None).await?;

        Ok(s.into())
    }

    async fn receive(ctx: &Store, tracking_id: ID, location: ID, completion_time: DateTime<Utc>) -> FieldResult<Option<DeliveryInterfaceValue>> {
        let cmd = Command::Receive(location.to_string(), completion_time.into());

        let r = save_with_action(ctx, tracking_id.to_string().as_str(), &cmd).await?;

        Ok(r.map(|s| s.into()))
    }

    async fn load(ctx: &Store, tracking_id: ID, voyage_no: ID, completion_time: DateTime<Utc>) -> FieldResult<Option<DeliveryInterfaceValue>> {
        let cmd = Command::Load(voyage_no.to_string(), completion_time.into());

        let r = save_with_action(ctx, tracking_id.to_string().as_str(), &cmd).await?;

        Ok(r.map(|s| s.into()))
    }

    async fn unload(ctx: &Store, tracking_id: ID, location: ID, completion_time: DateTime<Utc>) -> FieldResult<Option<DeliveryInterfaceValue>> {
        let cmd = Command::Unload(location.to_string(), completion_time.into());

        let r = save_with_action(ctx, tracking_id.to_string().as_str(), &cmd).await?;

        Ok(r.map(|s| s.into()))
    }

    async fn claim(ctx: &Store, tracking_id: ID, completion_time: DateTime<Utc>) -> FieldResult<Option<DeliveryInterfaceValue>> {
        let cmd = Command::Claim(completion_time.into());

        let r = save_with_action(ctx, tracking_id.to_string().as_str(), &cmd).await?;

        Ok(r.map(|s| s.into()))
    }
}

type Schema = RootNode<'static, Query, Mutation, EmptySubscription<Store>>;

type Context = Arc<(Store, Schema)>;

#[tokio::main]
async fn main() {
    let addr = env::var("ADDRESS").unwrap_or("127.0.0.1:8081".to_string()).parse().unwrap();
    let mongo_endpoint = env::var("MONGO_ENDPOINT").unwrap_or("mongodb://127.0.0.1".to_string());

    let db_name = env::var("DB_NAME").unwrap_or("delivery".to_string());
    let col_name = env::var("COLLECTION_NAME").unwrap_or("data".to_string());

    let schema = Schema::new(Query, Mutation, EmptySubscription::new());

    let opt = ClientOptions::parse(mongo_endpoint).await.unwrap();
    let mongo = Client::with_options(opt).unwrap();

    let ctx: Context = Arc::new((
        Store(mongo.database(&db_name).collection(&col_name)), 
        schema
    ));

    let app = Router::new()
        .route("/", post(graphql_handler))
        .with_state(ctx);
    
    println!("server start: {}", addr);

    axum::Server::bind(&addr)
        .serve(app.into_make_service())
        .await
        .unwrap();
}

async fn graphql_handler(State(ctx): State<Context>, Json(input): Json<GraphQLRequest>) -> Json<GraphQLResponse> {
    let res = input.execute(&ctx.1, &ctx.0).await;
    res.into()
}