
use juniper::{
    graphql_interface, graphql_object,
    ID,
    EmptySubscription, FieldResult, FieldError, 
    GraphQLObject, GraphQLInputObject,
    RootNode, http::GraphQLRequest
};
use tide::{Body, Request, Response, StatusCode};
use mongodb::{Client, options::ClientOptions, Database, Collection, 
    options::UpdateOptions, options::FindOneAndUpdateOptions, 
    options::ReturnDocument, options::FindOptions};
use bson::{Bson, Document, doc};
use serde::{Deserialize, Serialize};
use uuid::Uuid;

use async_std::stream::StreamExt;

use std::sync::Arc;
use std::cmp;

use stockmove::Restore;

mod models;

use models::stockmove;

type StockId = String;
type EventId = i32;
type MoveId = String;
type Revision = i32;

type StoreResult<T> = Result<T, Box<dyn std::error::Error>>;

#[derive(Clone)]
struct Store(Database);

impl juniper::Context for Store {}

impl Store {
    async fn save_stock(&self, stock: &StoredStock) -> StoreResult<()> {
        let query = doc! {"_id": &stock.id};

        Self::set_on_insert(self.0.collection("stocks"), query, stock)
            .await
            .map(|_| ())
    }

    async fn load_stock(&self, item: String, location: String) -> StoreResult<Option<StoredStock>> {
        let query = doc! {"_id": stock_id(&item, &location)};

        let r = self.0
            .collection("stocks")
            .find_one(query, None)
            .await?;

        if let Some(d) = r {
            let s = bson::from_document::<StoredStock>(d)?;

            let query = doc! {
                "$and": [
                    {"item": item.clone()},
                    {"$or": [
                        {"from": location.clone()},
                        {"to": location.clone()},
                    ]},
                ]
            };

            let es = self
                .load_events(query, doc! {"_id": 1})
                .await?
                .into_iter()
                .map(|e| e.event);

            Ok(Some(s.stock.restore(es).into()))
        } else {
            Ok(None)
        }
    }

    async fn save_event(&self, move_id: MoveId, revision: Revision, 
        data: &(stockmove::StockMove, stockmove::MoveEvent)) -> StoreResult<RestoredStockMove> {

        let info = data.0.info().ok_or("invalid state")?;
        let id = self.next_event_id().await?;

        let event = StoredEvent {
            id,
            move_id: move_id.clone(),
            revision: revision.clone(),
            item: info.item,
            from: info.from,
            to: info.to,
            event: data.1.clone(),
        };

        let query = doc! {"move_id": move_id.clone(), "revision": revision};

        Self::set_on_insert(self.0.collection("events"), query, &event)
            .await
            .map(|_|
                RestoredStockMove {
                    move_id,
                    revision,
                    state: data.0.clone(),
                }
            )
    }

    async fn load_move(&self, move_id: MoveId) -> StoreResult<Option<RestoredStockMove>> {
        let es = self
            .load_events(
                doc! {"move_id": move_id.clone()}, 
                doc! {"revision": 1}
            )
            .await?;

        let revision = es.iter().fold(0, |acc, x| cmp::max(acc, x.revision));

        let fst = stockmove::StockMove::initial_state();

        let state = fst.clone().restore(
            es.into_iter().map(|e| e.event)
        );

        if state == fst {
            Ok(None)
        } else {
            Ok(Some(
                RestoredStockMove {
                    move_id,
                    revision,
                    state,
                }
            ))
        }
    }

    async fn load_events(&self, query: Document, sort: Document) -> StoreResult<Vec<StoredEvent>> {
        let opts = FindOptions::builder()
            .sort(Some(sort))
            .build();

        let es = self.0
            .collection("events")
            .find(query, opts)
            .await?
            .map(|r| 
                r.clone().and_then(|d| 
                    bson::from_document::<StoredEvent>(d)
                        .map_err(|e| e.into())
                )
            )
            .collect::<Vec<_>>()
            .await
            .iter()
            .cloned()
            .flat_map(|r| r.ok())
            .collect::<Vec<_>>();
        
        println!("debug: {:?}", es);

        Ok(es)
    }

    async fn set_on_insert<T>(col: Collection, 
        query: Document, data: &T) -> StoreResult<Bson> 
    where
        T: Serialize,
    {
        let doc = bson::to_bson(data)?;

        if let Bson::Document(d) = doc {
            println!("to_bson: {:?}", d);

            let opts = UpdateOptions::builder().upsert(true).build();

            col
                .update_one(
                    query,
                    doc! {"$setOnInsert": d},
                    opts,
                )
                .await?
                .upserted_id
                .ok_or("conflict".into())

        } else {
            Err("invalid type".into())
        }
    }

    async fn next_event_id(&self) -> StoreResult<EventId> {
        let opts = FindOneAndUpdateOptions::builder()
            .upsert(true)
            .return_document(Some(ReturnDocument::After))
            .build();

        let doc = self.0.collection("events_seq")
            .find_one_and_update(
                doc! {"_id": "seq_no"},
                doc! {"$inc": {"seq_no": 1}},
                opts,
            )
            .await?
            .ok_or("no document")?;
        
        println!("{:?}", doc);

        doc.get_i32("seq_no")
            .map_err(|e|
                format!("{:?}", e).into()
            )
    }
}

#[derive(Debug, Deserialize, Serialize)]
struct StoredStock {
    #[serde(rename = "_id")]
    id: StockId,
    stock: stockmove::Stock,
}

impl From<stockmove::Stock> for StoredStock {
    fn from(stock: stockmove::Stock) -> Self {
        Self {
            id: stock_id(stock.item().as_str(), stock.location().as_str()),
            stock: stock.clone(),
        }
    }
}

#[derive(Debug, Clone)]
struct RestoredStockMove {
    move_id: MoveId,
    revision: Revision,
    state: stockmove::StockMove,
}

#[derive(Debug, Clone, Deserialize, Serialize)]
struct StoredEvent {
    #[serde(rename = "_id")]
    id: EventId,
    move_id: MoveId,
    revision: Revision,
    item: stockmove::ItemCode,
    from: stockmove::LocationCode,
    to: stockmove::LocationCode,
    event: stockmove::MoveEvent,
}

#[graphql_interface(for = [ManagedStock, UnmanagedStock])]
trait Stock {
    fn item(&self) -> ID;
    fn location(&self) -> ID;
}

#[derive(Debug, Clone, GraphQLObject, Deserialize, Serialize)]
#[graphql(impl = StockValue)]
struct ManagedStock {
    #[serde(rename = "_id")]
    id: String,
    item: ID,
    location: ID,
    qty: i32,
    assigned: i32,
}

#[graphql_interface]
impl Stock for ManagedStock {
    fn item(&self) -> ID {
        self.item.clone()
    }

    fn location(&self) -> ID {
        self.location.clone()
    }
}

#[derive(Debug, Clone, GraphQLObject, Deserialize, Serialize)]
#[graphql(impl = StockValue)]
struct UnmanagedStock {
    #[serde(rename = "_id")]
    id: String,
    item: ID,
    location: ID,
}

#[graphql_interface]
impl Stock for UnmanagedStock {
    fn item(&self) -> ID {
        self.item.clone()
    }

    fn location(&self) -> ID {
        self.location.clone()
    }
}

impl From<StoredStock> for StockValue {
    fn from(s: StoredStock) -> Self {
        match s.stock {
            stockmove::Stock::Managed { item, location, qty, assigned } => 
                ManagedStock {
                    id: stock_id(&item, &location),
                    item: item.into(),
                    location: location.into(),
                    qty,
                    assigned,
                }.into(), 
            stockmove::Stock::Unmanaged { item, location } => 
                UnmanagedStock {
                    id: stock_id(&item, &location),
                    item: item.into(),
                    location: location.into(),
                }.into(),
        }
    }
}

#[derive(Debug, Clone, GraphQLInputObject)]
struct CreateStockInput {
    item: ID,
    location: ID,
}

#[derive(Debug, Clone, GraphQLInputObject)]
struct StartMoveInput {
    item: ID,
    qty: i32,
    from: ID,
    to: ID,
}

#[derive(Debug, Clone, GraphQLObject)]
struct StockMoveInfo {
    item: ID,
    qty: i32,
    from: ID,
    to: ID,
}

impl From<stockmove::StockMoveInfo> for StockMoveInfo {
    fn from(s: stockmove::StockMoveInfo) -> Self {
        Self {
            item: s.item.into(),
            qty: s.qty,
            from: s.from.into(),
            to: s.to.into(),
        }
    }
}

#[graphql_interface(for = [
    DraftStockMove, CompletedStockMove, CancelledStockMove, 
    AssignedStockMove, ShippedStockMove, ArrivedStockMove,
    AssignFailedStockMove, ShipmentFailedStockMove,
])]
trait StockMove {
    fn id(&self) -> ID;
    fn info(&self) -> &StockMoveInfo;
}

#[derive(Debug, Clone, GraphQLObject)]
#[graphql(impl = StockMoveValue)]
struct DraftStockMove {
    id: ID,
    info: StockMoveInfo,
}

#[graphql_interface]
impl StockMove for DraftStockMove {
    fn id(&self) -> ID {
        self.id.clone()
    }

    fn info(&self) -> &StockMoveInfo {
        &self.info
    }
}

#[derive(Debug, Clone, GraphQLObject)]
#[graphql(impl = StockMoveValue)]
struct CompletedStockMove {
    id: ID,
    info: StockMoveInfo,
    outgoing: i32,
    incoming: i32,
}

#[graphql_interface]
impl StockMove for CompletedStockMove {
    fn id(&self) -> ID {
        self.id.clone()
    }

    fn info(&self) -> &StockMoveInfo {
        &self.info
    }
}

#[derive(Debug, Clone, GraphQLObject)]
#[graphql(impl = StockMoveValue)]
struct CancelledStockMove {
    id: ID,
    info: StockMoveInfo,
}

#[graphql_interface]
impl StockMove for CancelledStockMove {
    fn id(&self) -> ID {
        self.id.clone()
    }

    fn info(&self) -> &StockMoveInfo {
        &self.info
    }
}

#[derive(Debug, Clone, GraphQLObject)]
#[graphql(impl = StockMoveValue)]
struct AssignedStockMove {
    id: ID,
    info: StockMoveInfo,
    assigned: i32,
}

#[graphql_interface]
impl StockMove for AssignedStockMove {
    fn id(&self) -> ID {
        self.id.clone()
    }

    fn info(&self) -> &StockMoveInfo {
        &self.info
    }
}

#[derive(Debug, Clone, GraphQLObject)]
#[graphql(impl = StockMoveValue)]
struct ShippedStockMove {
    id: ID,
    info: StockMoveInfo,
    outgoing: i32,
}

#[graphql_interface]
impl StockMove for ShippedStockMove {
    fn id(&self) -> ID {
        self.id.clone()
    }

    fn info(&self) -> &StockMoveInfo {
        &self.info
    }
}

#[derive(Debug, Clone, GraphQLObject)]
#[graphql(impl = StockMoveValue)]
struct ArrivedStockMove {
    id: ID,
    info: StockMoveInfo,
    outgoing: i32,
    incoming: i32,
}

#[graphql_interface]
impl StockMove for ArrivedStockMove {
    fn id(&self) -> ID {
        self.id.clone()
    }

    fn info(&self) -> &StockMoveInfo {
        &self.info
    }
}

#[derive(Debug, Clone, GraphQLObject)]
#[graphql(impl = StockMoveValue)]
struct AssignFailedStockMove {
    id: ID,
    info: StockMoveInfo,
}

#[graphql_interface]
impl StockMove for AssignFailedStockMove {
    fn id(&self) -> ID {
        self.id.clone()
    }

    fn info(&self) -> &StockMoveInfo {
        &self.info
    }
}

#[derive(Debug, Clone, GraphQLObject)]
#[graphql(impl = StockMoveValue)]
struct ShipmentFailedStockMove {
    id: ID,
    info: StockMoveInfo,
}

#[graphql_interface]
impl StockMove for ShipmentFailedStockMove {
    fn id(&self) -> ID {
        self.id.clone()
    }

    fn info(&self) -> &StockMoveInfo {
        &self.info
    }
}

impl From<RestoredStockMove> for Option<StockMoveValue> {
    fn from(s: RestoredStockMove) -> Self {
        match s.state {
            stockmove::StockMove::Nothing => None,
            stockmove::StockMove::Draft { info } => 
                Some(
                    DraftStockMove { id: s.move_id.into(), info: info.into() }.into()
                ),
            stockmove::StockMove::Completed { info, outgoing, incoming } => 
                Some(
                    CompletedStockMove {
                        id: s.move_id.into(), info: info.into(), outgoing, incoming
                    }.into()
                ),
            stockmove::StockMove::Cancelled { info } => 
                Some(
                    CancelledStockMove { id: s.move_id.into(), info: info.into() }.into()
                ),
            stockmove::StockMove::Assigned { info, assigned } => 
                Some(
                    AssignedStockMove {
                        id: s.move_id.into(), info: info.into(), assigned
                    }.into()
                ),
            stockmove::StockMove::Shipped { info, outgoing } => 
                Some(
                    ShippedStockMove {
                        id: s.move_id.into(), info: info.into(), outgoing
                    }.into()
                ),
            stockmove::StockMove::Arrived { info, outgoing, incoming } => 
                Some(
                    ArrivedStockMove {
                        id: s.move_id.into(), info: info.into(), outgoing, incoming
                    }.into()
                ),
            stockmove::StockMove::AssignFailed { info } => 
                Some(
                    AssignFailedStockMove {
                        id: s.move_id.into(), info: info.into()
                    }.into()
                ),
            stockmove::StockMove::ShipmentFailed { info } => 
                Some(
                    ShipmentFailedStockMove {
                        id: s.move_id.into(), info: info.into()
                    }.into()
                ),
        }
    }
}

fn stock_id(item: &str, location: &str) -> String {
    format!("{}/{}", item, location)
}

async fn action(ctx: &Store, rs: RestoredStockMove, 
    act: stockmove::StockMoveAction) -> FieldResult<Option<StockMoveValue>> {
    
    let mr = rs.state.action(act);

    if let Some(t) = mr {
        let rev = rs.revision + 1;

        ctx.save_event(rs.move_id.clone(), rev, &t)
            .await
            .map(|s| s.into())
            .map_err(|e| e.into())
    } else {
        Ok(None)
    }
}

async fn find_and_action(ctx: &Store, move_id: MoveId, 
    act: stockmove::StockMoveAction) -> FieldResult<Option<StockMoveValue>> {

    let rs = ctx.load_move(move_id).await?;

    if let Some(r) = rs {
        action(ctx, r, act).await
    } else {
        Ok(None)
    }
}


#[derive(Debug)]
struct Query;

#[graphql_object(Context = Store)]
impl Query {
    async fn find_stock(ctx: &Store, item: String, location: String) -> FieldResult<Option<StockValue>> {
        let r = ctx.load_stock(item, location)
            .await
            .map_err(|e|
                error(format!("{:?}", e).as_str())
            )?;

        Ok(r.map(|s| s.into()))
    }

    async fn find_move(ctx: &Store, id: ID) -> FieldResult<Option<StockMoveValue>> {
        let r = ctx.load_move(id.to_string())
            .await
            .map_err(|e|
                error(format!("{:?}", e).as_str())
            )?;

        Ok(r.and_then(|s| s.into()))
    }
}

#[derive(Debug)]
struct Mutation;

#[graphql_object(Context = Store)]
impl Mutation {
    async fn create_managed(ctx: &Store, input: CreateStockInput) -> FieldResult<StockValue> {
        let stock = stockmove::Stock::managed_new(
            input.item.to_string(), 
            input.location.to_string(),
        );

        let s: StoredStock = stock.into();

        ctx.save_stock(&s)
            .await
            .map(|_| s.into())
            .map_err(|e|
                error(format!("{:?}", e).as_str())
            )
    }

    async fn create_unmanaged(ctx: &Store, input: CreateStockInput) -> FieldResult<StockValue> {
        let stock = stockmove::Stock::unmanaged_new(
            input.item.to_string(), 
            input.location.to_string(),
        );

        let s: StoredStock = stock.into();

        ctx.save_stock(&s)
            .await
            .map(|_| s.into())
            .map_err(|e|
                error(format!("{:?}", e).as_str())
            )
    }

    async fn start(ctx: &Store, input: StartMoveInput) -> FieldResult<Option<StockMoveValue>> {
        let act = stockmove::StockMoveAction::Start {
            item: input.item.to_string(),
            qty: input.qty,
            from: input.from.to_string(),
            to: input.to.to_string(),
        };

        let rs = RestoredStockMove {
            move_id: format!("move-{}", Uuid::new_v4()),
            revision: 0,
            state: stockmove::StockMove::initial_state(), 
        };

        action(ctx, rs, act).await
    }

    async fn complete(ctx: &Store, id: ID) -> FieldResult<Option<StockMoveValue>> {
        find_and_action(
            ctx, id.to_string(), 
            stockmove::StockMoveAction::Complete,
        ).await
    }

    async fn cancel(ctx: &Store, id: ID) -> FieldResult<Option<StockMoveValue>> {
        find_and_action(
            ctx, id.to_string(), 
            stockmove::StockMoveAction::Cancel,
        ).await
    }

    async fn assign(ctx: &Store, id: ID) -> FieldResult<Option<StockMoveValue>> {
        let rs = ctx.load_move(id.to_string()).await?;

        if let Some(r) = rs {
            let info = r.state
                .info()
                .ok_or("not found info")?;

            let st = ctx.load_stock(info.item.clone(), info.from.clone())
                .await?
                .unwrap_or(
                    stockmove::Stock::managed_new(info.item, info.from).into()
                );

            let act = stockmove::StockMoveAction::Assign { stock: st.stock };

            action(ctx, r, act).await
        } else {
            Ok(None)
        }
    }

    async fn ship(ctx: &Store, id: ID, outgoing: i32) -> FieldResult<Option<StockMoveValue>> {
        find_and_action(
            ctx, id.to_string(), 
            stockmove::StockMoveAction::Ship { outgoing },
        ).await
    }

    async fn arrive(ctx: &Store, id: ID, incoming: i32) -> FieldResult<Option<StockMoveValue>> {
        find_and_action(
            ctx, id.to_string(), 
            stockmove::StockMoveAction::Arrive { incoming },
        ).await
    }
}

type Schema = RootNode<'static, Query, Mutation, EmptySubscription<Store>>;

type State = (Store, Arc<Schema>);

#[async_std::main]
async fn main() -> tide::Result<()> {
    let addr = "127.0.0.1:4000";

    let opt = ClientOptions::parse("mongodb://localhost").await?;
    let mongo = Client::with_options(opt)?;

    let state = (
        Store(mongo.database("stockmoves")),
        Arc::new(Schema::new(Query, Mutation, EmptySubscription::new())),
    );

    let mut app = tide::with_state(state);

    app.at("/graphql").post(handle_graphql);

    app.listen(addr).await?;

    Ok(())
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

fn error(msg: &str) -> FieldError {
    FieldError::new(msg, juniper::Value::Null)
}