
mod cargo;

use axum::{
    Router, 
    extract::State,
    routing::post, 
    response::Json, 
};
use chrono::{DateTime, Utc};
use juniper::{
    EmptySubscription, FieldResult, GraphQLObject, GraphQLInputObject, ID, RootNode, 
    http::{GraphQLRequest, GraphQLResponse}
};
use mongodb::{
    Client,
    Collection,
    options::{ClientOptions, FindOneAndUpdateOptions, ReturnDocument}, 
    bson::{doc, to_bson}
};
use serde::{Deserialize, Serialize};
use uuid::Uuid;

use std::fmt;
use std::net::SocketAddr;
use std::sync::Arc;

use cargo::*;

type Revision = u32;

#[derive(Clone, Debug, Deserialize, Serialize)]
struct StoredState {
    _id: String,
    rev: Revision,
    state: Cargo,
    events: Vec<cargo::Event>,
}

#[derive(Debug, Clone)]
pub enum AppError {
    InvalidRevision(Revision),
}

impl fmt::Display for AppError {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::InvalidRevision(r) => write!(f, "invalid revision: {}", r),
        }
    }
}

impl std::error::Error for AppError {}

#[derive(Clone, Debug)]
pub struct Store(Collection<StoredState>);

impl juniper::Context for Store {}

#[juniper::graphql_object]
impl LocationTime {
    fn location(&self) -> ID {
        self.location.clone().into()
    }

    fn time(&self) -> DateTime<Utc> {
        self.time.into()
    }
}

#[juniper::graphql_object]
impl Leg {
    fn voyage_no(&self) -> ID {
        self.voyage_no.clone().into()
    }

    fn load(&self) -> &LocationTime {
        &self.load
    }

    fn unload(&self) -> &LocationTime {
        &self.unload
    }
}

#[juniper::graphql_object]
impl RouteSpec {
    fn origin(&self) -> ID {
        self.origin.clone().into()
    }

    fn destination(&self) -> ID {
        self.destination.clone().into()
    }

    fn deadline(&self) -> DateTime<Utc> {
        self.deadline.into()
    }
}

#[juniper::graphql_object]
impl Itinerary {
    fn legs(&self) -> &Vec<Leg> {
        &self.0
    }
}

#[juniper::graphql_interface(name = "Cargo", for = [UnroutedCargo, RoutedCargo, MisroutedCargo, ClosedCargo])]
trait CargoInterface {
    fn tracking_id(&self) -> &ID;
    fn route_spec(&self) -> &RouteSpec;
}

#[juniper::graphql_interface(name = "Routing", for = [RoutedCargo, MisroutedCargo, ClosedCargo])]
trait RoutingInterface {
    fn itinerary(&self) -> &Itinerary;
}

#[derive(GraphQLObject)]
#[graphql(impl = CargoInterfaceValue)]
struct UnroutedCargo {
    tracking_id: ID,
    route_spec: RouteSpec,
}

#[derive(GraphQLObject)]
#[graphql(impl = [CargoInterfaceValue, RoutingInterfaceValue])]
struct RoutedCargo {
    tracking_id: ID,
    route_spec: RouteSpec,
    itinerary: Itinerary,
}

#[derive(GraphQLObject)]
#[graphql(impl = [CargoInterfaceValue, RoutingInterfaceValue])]
struct MisroutedCargo {
    tracking_id: ID,
    route_spec: RouteSpec,
    itinerary: Itinerary,
}

#[derive(GraphQLObject)]
#[graphql(impl = [CargoInterfaceValue, RoutingInterfaceValue])]
struct ClosedCargo {
    tracking_id: ID,
    route_spec: RouteSpec,
    itinerary: Itinerary,
}

#[derive(GraphQLInputObject)]
struct LegInput {
    voyage_no: ID,
    load_location: ID,
    load_time: DateTime<Utc>,
    unload_location: ID,
    unload_time: DateTime<Utc>,
}

impl From<&LegInput> for Leg {
    fn from(input: &LegInput) -> Self {
        Self { 
            voyage_no: input.voyage_no.to_string(), 
            load: LocationTime {
                location: input.load_location.to_string(), 
                time: input.load_time.into()
            }, 
            unload: LocationTime { 
                location: input.unload_location.to_string(), 
                time: input.unload_time.into()
            }
        }
    }
}

impl From<Cargo> for CargoInterfaceValue {
    fn from(state: Cargo) -> Self {
        match state {
            Cargo::Nothing => 
                UnroutedCargo { tracking_id: ID::new(""), route_spec: RouteSpec::default() }.into(),
            Cargo::Unrouted { tracking_id, route_spec } => 
                UnroutedCargo { tracking_id: ID::new(tracking_id), route_spec }.into(),
            Cargo::Routed { tracking_id, route_spec, itinerary } =>
                RoutedCargo { tracking_id: ID::new(tracking_id), route_spec, itinerary }.into(),
            Cargo::Misrouted { tracking_id, route_spec, itinerary } =>
                MisroutedCargo { tracking_id: ID::new(tracking_id), route_spec, itinerary }.into(),
            Cargo::Closed { tracking_id, route_spec, itinerary } =>
                ClosedCargo { tracking_id: ID::new(tracking_id), route_spec, itinerary }.into(),
        }
    }
}

async fn load_state(ctx: &Store, tracking_id: String) -> FieldResult<Option<Cargo>> {
    let q = doc! { "_id": tracking_id };
    let d = ctx.0.find_one(q, None).await?;

    Ok(d.map(|s| s.state))    
}

async fn save_with_action(ctx: &Store, tracking_id: &str, cmd: &Command) -> FieldResult<Option<Cargo>> {
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
                "events": to_bson(&e)?
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
    async fn find(ctx: &Store, tracking_id: ID) -> FieldResult<Option<CargoInterfaceValue>> {
        let s = load_state(ctx, tracking_id.to_string()).await?;

        Ok(s.map(|c| c.into()))
    }

    async fn is_destination(ctx: &Store, tracking_id: ID, location: ID) -> FieldResult<Option<bool>> {
        let s = load_state(ctx, tracking_id.to_string()).await?;

        Ok(s.map(|c| c.is_destination(location.to_string())))
    }

    async fn is_on_route(ctx: &Store, tracking_id: ID, location: ID, voyage_no: Option<ID>) -> FieldResult<Option<bool>> {
        let s = load_state(ctx, tracking_id.to_string()).await?;

        let v = voyage_no.map(|n| n.to_string());

        Ok(s.and_then(|c| c.is_on_route(location.to_string(), v)))
    }
}

struct Mutation;

#[juniper::graphql_object(context = Store)]
impl Mutation {
    async fn create(ctx: &Store, origin: ID, destination: ID, deadline: DateTime<Utc>) -> FieldResult<CargoInterfaceValue> {
        let rs = RouteSpec {
            origin: origin.to_string(), 
            destination: destination.to_string(), 
            deadline: deadline.into()
        };

        let tracking_id = Uuid::new_v4().to_string();

        let cmd = Command::Create(tracking_id.clone(), rs);
        let (s, e) = Cargo::Nothing.action(&cmd)?;

        let d = StoredState {
            _id: tracking_id,
            rev: 1,
            state: s.clone(),
            events: vec![e],
        };

        ctx.0.insert_one(d, None).await?;

        Ok(s.into())
    }

    async fn assign_to_route(ctx: &Store, tracking_id: ID, legs: Vec<LegInput>) -> FieldResult<Option<CargoInterfaceValue>> {
        let cmd = Command::AssignRoute(
            Itinerary(legs.iter().map(|l| l.into()).collect())
        );

        let r = save_with_action(ctx, tracking_id.to_string().as_str(), &cmd).await?;

        Ok(r.map(|c| c.into()))
    }

    async fn close(ctx: &Store, tracking_id: ID) -> FieldResult<Option<CargoInterfaceValue>> {
        let cmd = Command::Close;

        let r = save_with_action(ctx, tracking_id.to_string().as_str(), &cmd).await?;

        Ok(r.map(|c| c.into()))
    }

    async fn change_destination(ctx: &Store, tracking_id: ID, location: ID) -> FieldResult<Option<CargoInterfaceValue>> {
        let cmd = Command::ChangedDestination(location.to_string());

        let r = save_with_action(ctx, tracking_id.to_string().as_str(), &cmd).await?;

        Ok(r.map(|c| c.into()))
    }

    async fn change_deadline(ctx: &Store, tracking_id: ID, deadline: DateTime<Utc>) -> FieldResult<Option<CargoInterfaceValue>> {
        let cmd = Command::ChangedDeadline(deadline.into());

        let r = save_with_action(ctx, tracking_id.to_string().as_str(), &cmd).await?;

        Ok(r.map(|c| c.into()))
    }
}

type Schema = RootNode<'static, Query, Mutation, EmptySubscription<Store>>;

type Context = Arc<(Store, Schema)>;

#[tokio::main]
async fn main() {
    let addr: SocketAddr = "127.0.0.1:8080".parse().unwrap();
    let schema = Schema::new(Query, Mutation, EmptySubscription::new());

    let opt = ClientOptions::parse("mongodb://127.0.0.1").await.unwrap();
    let mongo = Client::with_options(opt).unwrap();

    let ctx: Context = Arc::new((
        Store(mongo.database("cargo").collection("data")), 
        schema
    ));

    let app = Router::new()
        .route("/", post(graphql_handler))
        .with_state(ctx);
    
    axum::Server::bind(&addr)
        .serve(app.into_make_service())
        .await
        .unwrap();
}

async fn graphql_handler(State(ctx): State<Context>, Json(input): Json<GraphQLRequest>) -> Json<GraphQLResponse> {
    let res = input.execute(&ctx.1, &ctx.0).await;
    res.into()
}
