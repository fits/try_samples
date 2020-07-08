
use wasm_bindgen::prelude::*;
use wasm_bindgen_futures::JsFuture;
use js_sys::Promise;
use serde::{Deserialize, Serialize};

mod models;
use models::{Stock, StockMove, StockMoveAction, StockMoveEvent, 
    Action, Restore, Event, StockMoveId, ItemCode, LocationCode, Quantity};

type JsResult<T> = Result<T, JsValue>;
type Revision = usize;

#[derive(Debug, Deserialize, Serialize)]
struct Response<T> {
    state: T, 
    event: StockMoveEvent,
    revision: Revision,
}

#[derive(Debug, Deserialize, Serialize)]
struct StoredResponse<T> {
    snapshot: T,
    events: Vec<StockMoveEvent>,
}

#[wasm_bindgen]
extern {
    #[wasm_bindgen(js_namespace = console)]
    fn log(s: String);

    #[wasm_bindgen(js_namespace = sample)]
    fn events_for_move(id: StockMoveId) -> Promise;

    #[wasm_bindgen(js_namespace = sample)]
    fn events_for_stock(item: ItemCode, location: LocationCode) -> Promise;
}

#[wasm_bindgen]
pub async fn start(id: StockMoveId, item: ItemCode, qty: Quantity, 
    from: LocationCode, to: LocationCode) -> JsResult<JsValue> {

    let cmd = StockMoveAction::start(id.clone(), item, qty, from, to);

    action(id, cmd).await
}

#[wasm_bindgen]
pub async fn assign(id: StockMoveId) -> JsResult<JsValue> {
    action(id.clone(), StockMoveAction::assign(id)).await
}

#[wasm_bindgen]
pub async fn cancel(id: StockMoveId) -> JsResult<JsValue> {
    action(id.clone(), StockMoveAction::cancel(id)).await
}

#[wasm_bindgen]
pub async fn shipment(id: StockMoveId, qty: Quantity) -> JsResult<JsValue> {
    let cmd = if qty > 0 {
        StockMoveAction::shipment(id.clone(), qty)
    } else {
        StockMoveAction::shipment_failure(id.clone())
    };

    action(id, cmd).await
}

#[wasm_bindgen]
pub async fn arrival(id: StockMoveId, qty: Quantity) -> JsResult<JsValue> {
    action(id.clone(), StockMoveAction::arrival(id, qty)).await
}

#[wasm_bindgen]
pub async fn status(id: StockMoveId) -> JsResult<JsValue> {
    let mv = restore_move(id).await?;
    to_jsvalue(&mv)
}

#[wasm_bindgen]
pub async fn new_stock(item: ItemCode, location: LocationCode, managed: bool) -> JsResult<JsValue> {
    let r = match managed {
        true => Stock::managed_new(item, location),
        false => Stock::unmanaged_new(item, location),
    };

    to_jsvalue(&r)
}

#[wasm_bindgen]
pub async fn stock_status(item: ItemCode, location: LocationCode) -> JsResult<JsValue> {
    let stock = restore_stock(item, location).await?;
    to_jsvalue(&stock)
}


async fn action(id: StockMoveId, cmd: Option<StockMoveAction>) -> JsResult<JsValue> {
    let (state, rev) = restore_move(id)
        .await
        .unwrap_or( (StockMove::Nothing, 0) );

    let stock_func = find_stock(&state).await;

    let r = cmd
        .and_then(|c|
            Action::action(&(state.clone(), stock_func), &c)
        )
        .and_then(|e|
            e.apply(state).map(|s| 
                Response { state: s, event: e, revision: rev + 1 }
            )
        )
        .ok_or(JsValue::from_str("failed"))?;

    to_jsvalue(&r)
}

async fn restore_move(id: StockMoveId) -> JsResult<(StockMove, Revision)> {
    let v = JsFuture::from(events_for_move(id)).await?;
    let r = from_jsvalue::<StoredResponse<StockMove>>(&v)?;

    let rev = r.events.len();
    Ok( (r.snapshot.restore(r.events.iter()), rev) )
}

async fn restore_stock(item: ItemCode, location: LocationCode) -> JsResult<Stock> {
    let v = JsFuture::from(events_for_stock(item, location)).await?;
    let r = from_jsvalue::<StoredResponse<Stock>>(&v)?;

    Ok( r.snapshot.restore(r.events.iter()) )
}

type FindStock = Box<dyn Fn(ItemCode, LocationCode) -> Option<Stock>>;

async fn find_stock(state: &StockMove) -> FindStock {
    let stocks = match state.info() {
        Some(info) => {
            let f = restore_stock(info.item.clone(), info.from).await;
            let t = restore_stock(info.item, info.to).await;

            f.iter().chain(t.iter()).cloned().collect::<Vec<_>>()
        },
        None => vec![],
    };

    let f = move |item, location| {
        stocks
            .iter()
            .find(|s| s.eq_id(&item, &location))
            .map(Stock::clone)
    };

    Box::new(f)
}

fn to_jsvalue<T>(v: &T) -> JsResult<JsValue>
where
    T: serde::ser::Serialize,
{
    JsValue::from_serde(v)
        .map_err(|e| JsValue::from(e.to_string()))
}

fn from_jsvalue<T>(v: &JsValue) -> JsResult<T> 
where
    T: for<'a> serde::Deserialize<'a>,
{
    JsValue::into_serde::<T>(v)
        .map_err(|e| JsValue::from(e.to_string()))
}
