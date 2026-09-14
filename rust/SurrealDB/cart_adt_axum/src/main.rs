use axum::extract::Path;
use axum::http::StatusCode;
use axum::routing::{get, post, put};
use axum::{Extension, Json, Router};

use serde::Deserialize;

use surrealdb::Surreal;
use surrealdb::engine::local::{Db, SurrealKv};
use surrealdb::types::{RecordId, SerdeWrapper, SurrealValue};

use std::sync::Arc;

mod model;
use model::*;

const ITEMS_TABLE: &str = "items";
const CART_TABLE: &str = "cart";
const MAX_RETRY: u8 = 10;

#[derive(SurrealValue)]
struct CartData {
    cart: SerdeWrapper<Cart>,
}

#[derive(SurrealValue)]
struct ItemData {
    item: SerdeWrapper<StockedItem>,
}

#[derive(Debug, Clone, Deserialize)]
struct CreateItem {
    item_code: ItemCode,
    unit_price: Amount,
}

#[derive(Debug, Clone, Deserialize)]
struct AddItemToCart {
    item_code: ItemCode,
    qty: Quantity,
}

#[derive(Debug, Clone, Deserialize)]
struct CreateCart {
    cart_id: CartId,
}

struct Context(Surreal<Db>);

impl Context {
    async fn create_item(
        &self,
        input: StockedItem,
    ) -> Result<Option<StockedItem>, surrealdb::types::Error> {
        let id = (ITEMS_TABLE, input.item_code().as_str());

        let res: Option<ItemData> = self
            .0
            .create(id)
            .content(ItemData {
                item: SerdeWrapper(input),
            })
            .await?;

        Ok(res.map(|x| x.item.0))
    }

    async fn select_item(
        &self,
        code: ItemCode,
    ) -> Result<Option<StockedItem>, surrealdb::types::Error> {
        let id = (ITEMS_TABLE, code);

        let res: Option<ItemData> = self.0.select(id).await?;

        Ok(res.map(|x| x.item.0))
    }

    async fn charge_stock(
        &self,
        item_code: ItemCode,
        qty: Quantity,
    ) -> Result<Option<StockedItem>, surrealdb::types::Error> {
        let id = RecordId::new(ITEMS_TABLE, item_code);

        let tr = self.0.clone().begin().await?;

        if let Some(mut x) = tr.select::<Option<ItemData>>(&id).await? {
            x.item.0 = x
                .item
                .0
                .store_in(qty)
                .map_err(|e| surrealdb::types::Error::not_allowed(e.to_string(), None))?;

            let res: Option<ItemData> = tr.update(&id).content(x).await?;

            tr.commit().await?;

            Ok(res.map(|x| x.item.0))
        } else {
            Ok(None)
        }
    }

    async fn create_cart(&self, input: Cart) -> Result<Option<Cart>, surrealdb::types::Error> {
        let id = (CART_TABLE, input.cart_id().as_str());

        let res: Option<CartData> = self
            .0
            .create(id)
            .content(CartData {
                cart: SerdeWrapper(input),
            })
            .await?;

        Ok(res.map(|x| x.cart.0))
    }

    async fn select_cart(&self, cart_id: CartId) -> Result<Option<Cart>, surrealdb::types::Error> {
        let id = (CART_TABLE, cart_id);

        let res: Option<CartData> = self.0.select(id).await?;

        Ok(res.map(|x| x.cart.0))
    }

    async fn add_item_to_cart(
        &self,
        cart_id: CartId,
        input: AddItemToCart,
    ) -> Result<Option<Cart>, surrealdb::types::Error> {
        let tr = self.0.clone().begin().await?;

        let item_id = (ITEMS_TABLE, input.item_code.as_str());

        let mut item_data: ItemData =
            tr.select(item_id)
                .await?
                .ok_or(surrealdb::types::Error::not_found(
                    "not found item".into(),
                    None,
                ))?;

        item_data.item.0 = item_data
            .item
            .0
            .store_out(input.qty)
            .map_err(|e| surrealdb::types::Error::not_allowed(e.to_string(), None))?;

        let item_data: ItemData = tr.update(item_id).content(item_data).await?.ok_or(
            surrealdb::types::Error::internal("failed update item".into()),
        )?;

        let cart_id = (CART_TABLE, cart_id.as_str());

        let mut cart_data: CartData =
            tr.select(cart_id)
                .await?
                .ok_or(surrealdb::types::Error::not_found(
                    "not found cart".into(),
                    None,
                ))?;

        let item = item_data.item.0.item().clone();

        cart_data.cart.0 = cart_data
            .cart
            .0
            .add_item(item, input.qty)
            .map_err(|e| surrealdb::types::Error::not_allowed(e.to_string(), None))?;

        let res: Option<CartData> = tr.update(cart_id).content(cart_data).await?;

        tr.commit().await?;

        Ok(res.map(|x| x.cart.0))
    }
}

type AppError = Box<dyn std::error::Error>;

#[tokio::main]
async fn main() -> Result<(), AppError> {
    let db = Surreal::new::<SurrealKv>("./db").await?;
    db.use_ns("example").use_db("catalog").await?;

    let ctx = Arc::new(Context(db));

    let app = Router::new()
        .route("/items", post(create_item))
        .route("/items/{code}", get(get_item))
        .route("/items/{code}/charge/{qty}", put(charge_stock))
        .route("/cart", post(create_cart))
        .route("/cart/{cart_id}", get(get_cart))
        .route("/cart/{cart_id}/items", put(add_item_to_cart))
        .layer(Extension(ctx));

    let listener = tokio::net::TcpListener::bind("127.0.0.1:8080").await?;

    axum::serve(listener, app).await?;

    Ok(())
}

async fn create_item(
    Extension(ctx): Extension<Arc<Context>>,
    Json(input): Json<CreateItem>,
) -> Result<Json<StockedItem>, StatusCode> {
    let item = Item::new(input.item_code, input.unit_price).map_err(|_| StatusCode::BAD_REQUEST)?;
    let stock_item = StockedItem::new(item);

    let res = ctx.create_item(stock_item).await.map_err(|e| {
        println!("create item error: {}", e);

        if e.is_already_exists() {
            StatusCode::BAD_REQUEST
        } else {
            StatusCode::INTERNAL_SERVER_ERROR
        }
    })?;

    res.map(Json).ok_or(StatusCode::INTERNAL_SERVER_ERROR)
}

async fn get_item(
    Path(code): Path<ItemCode>,
    Extension(ctx): Extension<Arc<Context>>,
) -> Result<Json<StockedItem>, StatusCode> {
    let res = ctx.select_item(code).await.map_err(to_server_error)?;

    res.map(Json).ok_or(StatusCode::NOT_FOUND)
}

async fn charge_stock(
    Path((code, qty)): Path<(ItemCode, Quantity)>,
    Extension(ctx): Extension<Arc<Context>>,
) -> Result<Json<StockedItem>, StatusCode> {
    let res = ctx.charge_stock(code, qty).await.map_err(|e| {
        println!("charge error: {}", e);

        if e.is_not_allowed() {
            StatusCode::BAD_REQUEST
        } else {
            StatusCode::INTERNAL_SERVER_ERROR
        }
    })?;

    res.map(Json).ok_or(StatusCode::NOT_FOUND)
}

async fn create_cart(
    Extension(ctx): Extension<Arc<Context>>,
    Json(input): Json<CreateCart>,
) -> Result<Json<Cart>, StatusCode> {
    let cart = Cart::new(input.cart_id).map_err(|_| StatusCode::BAD_REQUEST)?;

    let res = ctx.create_cart(cart).await.map_err(|e| {
        println!("create cart error: {}", e);

        if e.is_already_exists() {
            StatusCode::BAD_REQUEST
        } else {
            StatusCode::INTERNAL_SERVER_ERROR
        }
    })?;

    res.map(Json).ok_or(StatusCode::INTERNAL_SERVER_ERROR)
}

async fn get_cart(
    Path(cart_id): Path<CartId>,
    Extension(ctx): Extension<Arc<Context>>,
) -> Result<Json<Cart>, StatusCode> {
    let res = ctx.select_cart(cart_id).await.map_err(to_server_error)?;

    res.map(Json).ok_or(StatusCode::NOT_FOUND)
}

async fn add_item_to_cart(
    Path(cart_id): Path<CartId>,
    Extension(ctx): Extension<Arc<Context>>,
    Json(input): Json<AddItemToCart>,
) -> Result<Json<Cart>, StatusCode> {
    let mut retry = 0;
    let mut res = Ok(None);

    while retry < MAX_RETRY {
        if retry > 0 {
            println!("trancation conflict retry: times={}, cart={}, input={:?}", retry, cart_id, input);
        }

        res = ctx.add_item_to_cart(cart_id.clone(), input.clone()).await;

        if let Err(e) = &res && is_conflict_transaction(e) {
            retry += 1;
        } else {
            break;
        }
    }

    match res {
        Ok(x) => {
            x.map(Json).ok_or(StatusCode::INTERNAL_SERVER_ERROR)
        }
        Err(e) => {
            println!("cart add_item error: {}", e);

            let r = if e.is_not_allowed() {
                StatusCode::BAD_REQUEST
            } else if e.is_not_found() {
                StatusCode::NOT_FOUND
            } else {
                StatusCode::INTERNAL_SERVER_ERROR
            };

            Err(r)
        }
    }
}

fn is_conflict_transaction(e: &surrealdb::types::Error) -> bool {
    e.to_string().starts_with("Transaction conflict:")
}

fn to_server_error(e: surrealdb::types::Error) -> StatusCode {
    println!("error: {}", e);
    StatusCode::INTERNAL_SERVER_ERROR
}

