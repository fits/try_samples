
use wasm_bindgen::prelude::*;
use serde::{Deserialize, Serialize};

mod models;
use models::{Stock, StockMove, StockMoveAction, StockMoveEvent, Action, Restore, Event, StockMoveId, ItemCode, LocationCode, Quantity};

#[derive(Debug, Deserialize, Serialize)]
struct Response<T> {
    state: T, 
    event: StockMoveEvent,
}

#[derive(Debug, Deserialize, Serialize)]
struct FindResponse<T> {
    snapshot: T,
    events: Vec<StockMoveEvent>,
}

#[wasm_bindgen]
extern {
    #[wasm_bindgen(js_namespace = console)]
    fn log(s: String);

    #[wasm_bindgen(js_namespace = sample)]
    fn find_events_for_move(id: StockMoveId) -> JsValue;

    #[wasm_bindgen(js_namespace = sample)]
    fn find_events_for_stock(item: ItemCode, location: LocationCode) -> JsValue;
}

#[wasm_bindgen]
pub fn start(id: StockMoveId, item: ItemCode, qty: Quantity, from: LocationCode, to: LocationCode) -> JsValue {

    let res = if restore_move(id.clone()).is_none() {
        StockMoveAction::start(id, item, qty, from, to)
            .and_then(|c|
                Action::action(&(StockMove::Nothing, restore_stock), &c)
            )
            .and_then(|e| {
                e.apply(StockMove::Nothing).map(|s| Response { state: s, event: e })
            })
            .and_then(|r| JsValue::from_serde(&r).ok())
    } else {
        None
    };

    res.unwrap_or(JsValue::null())
}

#[wasm_bindgen]
pub fn assign(id: StockMoveId) -> JsValue {
    action(StockMoveAction::assign(id))
}

#[wasm_bindgen]
pub fn cancel(id: StockMoveId) -> JsValue {
    action(StockMoveAction::cancel(id))
}

#[wasm_bindgen]
pub fn shipment(id: StockMoveId, qty: Quantity) -> JsValue {
    action(StockMoveAction::shipment(id, qty))
}

#[wasm_bindgen]
pub fn shipment_failure(id: StockMoveId) -> JsValue {
    action(StockMoveAction::shipment_failure(id))
}

#[wasm_bindgen]
pub fn arrival(id: StockMoveId, qty: Quantity) -> JsValue {
    action(StockMoveAction::arrival(id, qty))
}

#[wasm_bindgen]
pub fn new_stock(item: ItemCode, location: LocationCode, managed: bool) -> JsValue {
    let r = match managed {
        true => Stock::managed_new(item, location),
        false => Stock::unmanaged_new(item, location),
    };

    JsValue::from_serde(&r).unwrap_or(JsValue::null())
}

type MoveFind = FindResponse<StockMove>;
type StockFind = FindResponse<Stock>;

fn action(cmd: Option<StockMoveAction>) -> JsValue {
    cmd.and_then(|c| 
        restore_move(c.move_id())
            .and_then(|s|
                Action::action(&(s.clone(), restore_stock), &c)
                    .and_then(|e|
                        e.apply(s).map(|s| Response { state: s, event: e})
                    )
            )
    )
    .and_then(|r| JsValue::from_serde(&r).ok())
    .unwrap_or(JsValue::null())
}

fn restore_move(id: StockMoveId) -> Option<StockMove> {
    find_events_for_move(id)
    .into_serde::<MoveFind>()
    .ok()
    .map(|r| r.snapshot.restore(r.events.iter()))
}

fn restore_stock(item: ItemCode, location: LocationCode) -> Option<Stock> {
    find_events_for_stock(item, location)
    .into_serde::<StockFind>()
    .ok()
    .map(|r| r.snapshot.restore(r.events.iter()))
}
