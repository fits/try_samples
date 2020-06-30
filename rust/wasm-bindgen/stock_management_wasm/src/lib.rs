
use wasm_bindgen::prelude::*;
use serde::{Deserialize, Serialize};

mod models;
use models::{Stock, StockMove, StockMoveAction, StockMoveEvent, 
    Action, Restore, Event, StockMoveId, ItemCode, LocationCode, Quantity};

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
    fn events_for_move(id: StockMoveId) -> JsValue;

    #[wasm_bindgen(js_namespace = sample)]
    fn events_for_stock(item: ItemCode, location: LocationCode) -> JsValue;
}

#[wasm_bindgen]
pub fn start(id: StockMoveId, item: ItemCode, qty: Quantity, 
    from: LocationCode, to: LocationCode) -> JsValue {

    let cmd = StockMoveAction::start(id.clone(), item, qty, from, to);

    action(id, cmd)
}

#[wasm_bindgen]
pub fn assign(id: StockMoveId) -> JsValue {
    action(id.clone(), StockMoveAction::assign(id))
}

#[wasm_bindgen]
pub fn cancel(id: StockMoveId) -> JsValue {
    action(id.clone(), StockMoveAction::cancel(id))
}

#[wasm_bindgen]
pub fn shipment(id: StockMoveId, qty: Quantity) -> JsValue {
    let cmd = if qty > 0 {
        StockMoveAction::shipment(id.clone(), qty)
    } else {
        StockMoveAction::shipment_failure(id.clone())
    };

    action(id, cmd)
}

#[wasm_bindgen]
pub fn arrival(id: StockMoveId, qty: Quantity) -> JsValue {
    action(id.clone(), StockMoveAction::arrival(id, qty))
}

#[wasm_bindgen]
pub fn status(id: StockMoveId) -> JsValue {
    to_jsvalue(&restore_move(id))
}

#[wasm_bindgen]
pub fn new_stock(item: ItemCode, location: LocationCode, managed: bool) -> JsValue {
    let r = match managed {
        true => Stock::managed_new(item, location),
        false => Stock::unmanaged_new(item, location),
    };

    to_jsvalue(&r)
}

#[wasm_bindgen]
pub fn stock_status(item: ItemCode, location: LocationCode) -> JsValue {
    to_jsvalue(&restore_stock(item, location))
}


fn action(id: StockMoveId, cmd: Option<StockMoveAction>) -> JsValue {
    let (state, rev) = restore_move(id)
        .unwrap_or( (StockMove::Nothing, 0) );

    let r = cmd
        .map(|c| (state, c))
        .and_then(|(s, c)|
            Action::action(&(s.clone(), restore_stock), &c)
                .and_then(|e|
                    e.apply(s).map(|s| 
                        Response { state: s, event: e, revision: rev + 1 }
                    )
                )
        );

    to_jsvalue(&r)
}

fn restore_move(id: StockMoveId) -> Option<(StockMove, Revision)> {
    events_for_move(id)
        .into_serde::<StoredResponse<StockMove>>()
        .ok()
        .map(|r| {
            let rev = r.events.len();
            (r.snapshot.restore(r.events.iter()), rev)
        })
}

fn restore_stock(item: ItemCode, location: LocationCode) -> Option<Stock> {
    events_for_stock(item, location)
        .into_serde::<StoredResponse<Stock>>()
        .ok()
        .map(|r| r.snapshot.restore(r.events.iter()))
}

fn to_jsvalue<T>(v: &T) -> JsValue
where
    T: serde::ser::Serialize,
{
    JsValue::from_serde(v).unwrap_or(JsValue::null())
}
