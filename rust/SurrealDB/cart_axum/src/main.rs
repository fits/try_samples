use axum::extract::Path;
use axum::http::StatusCode;
use axum::routing::{get, post, put};
use axum::{Extension, Json, Router};

use serde::{Deserialize, Serialize};

use surrealdb::Surreal;
use surrealdb::engine::local::{Db, SurrealKv};
use surrealdb::types::{RecordId, SurrealValue};

use std::sync::Arc;

const ITEMS_TABLE: &str = "items";
const CART_TABLE: &str = "cart";

type CartId = String;
type ItemCode = String;
type Amount = i64;
type Quantity = i64;

#[derive(Debug, Clone, SurrealValue, Serialize, Deserialize)]
struct Item {
    code: ItemCode,
    unit_price: Amount,
    qty: Quantity,
}

#[derive(Debug, Clone, SurrealValue, Serialize)]
struct Cart {
    cart_id: CartId,
    items: Vec<CartItem>,
}

#[derive(Debug, Clone, SurrealValue, Serialize, Deserialize)]
struct CartItem {
    item_code: ItemCode,
    qty: Quantity,
}

#[derive(Debug, Clone, Deserialize)]
struct CreateCart {
    cart_id: CartId,
}

struct Context(Surreal<Db>);

impl Context {
    async fn create_item(&self, input: Item) -> Result<Option<Item>, surrealdb::types::Error> {
        let id = (ITEMS_TABLE, input.code.as_str());
        self.0.create(id).content(input).await
    }

    async fn select_item(&self, code: ItemCode) -> Result<Option<Item>, surrealdb::types::Error> {
        let id = (ITEMS_TABLE, code);
        self.0.select(id).await
    }

    async fn charge_qty(
        &self,
        code: ItemCode,
        qty: Quantity,
    ) -> Result<Option<Item>, surrealdb::types::Error> {
        let id = RecordId::new(ITEMS_TABLE, code);

        self.0
            .query("UPDATE $id SET qty += $qty")
            .bind(("id", id))
            .bind(("qty", qty))
            .await?
            .take(0)
    }

    async fn create_cart(
        &self,
        input: CreateCart,
    ) -> Result<Option<Cart>, surrealdb::types::Error> {
        let id = (CART_TABLE, input.cart_id.as_str());

        let cart = Cart {
            cart_id: input.cart_id.clone(),
            items: Vec::new(),
        };

        self.0.create(id).content(cart).await
    }

    async fn select_cart(&self, cart_id: CartId) -> Result<Option<Cart>, surrealdb::types::Error> {
        let id = (CART_TABLE, cart_id);
        self.0.select(id).await
    }

    #[allow(dead_code)]
    async fn add_item_to_cart(
        &self,
        cart_id: CartId,
        item: CartItem,
    ) -> Result<Option<Cart>, surrealdb::types::Error> {
        let q = r#"
            {
                IF !record::exists($cart_id) {
                    THROW "not foud cart"
                };

                UPDATE $item_id SET qty -= $cart_item.qty;

                IF $item_id.qty < 0 {
                    THROW "out of stock"
                };

                UPDATE $cart_id PATCH [
                    { op: 'add', path: '/items', value: $cart_item }
                ];
            };
        "#;

        self.0
            .query(q)
            .bind(("cart_id", RecordId::new(CART_TABLE, cart_id)))
            .bind((
                "item_id",
                RecordId::new(ITEMS_TABLE, item.item_code.as_str()),
            ))
            .bind(("cart_item", item))
            .await?
            .take(0)
    }

    async fn add_item_to_cart_alt(
        &self,
        cart_id: CartId,
        input: CartItem,
    ) -> Result<Option<Cart>, surrealdb::types::Error> {
        let tr = self.0.clone().begin().await?;

        let not_found = |msg: &str| surrealdb::types::Error::not_found(msg.into(), None);

        let item_id = (ITEMS_TABLE, input.item_code.as_str());
        let mut item: Item = tr.select(item_id).await?.ok_or(not_found("not found item"))?;

        if item.qty < input.qty {
            tr.cancel().await?;

            Err(surrealdb::types::Error::not_allowed(
                "out of stock".into(),
                None,
            ))
        } else {
            item.qty -= input.qty;

            let _: Item = tr
                .update(item_id)
                .content(item)
                .await?
                .ok_or(surrealdb::types::Error::internal("failed update item".into()))?;

            let cart_id = (CART_TABLE, cart_id.as_str());

            let mut cart: Cart = tr.select(cart_id).await?.ok_or(not_found("not found cart"))?;
            cart.items.push(input);

            let res: Option<Cart> = tr.update(cart_id).content(cart).await?;

            tr.commit().await?;

            Ok(res)
        }
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
        .route("/items/{code}/charge/{qty}", put(charge_qty))
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
    Json(input): Json<Item>,
) -> Result<Json<Item>, StatusCode> {
    if input.code.trim().is_empty() || input.unit_price < 0 || input.qty < 0 {
        Err(StatusCode::BAD_REQUEST)
    } else {
        let res = ctx.create_item(input).await.map_err(|e| {
            println!("create error: {}", e);

            if e.is_already_exists() {
                StatusCode::BAD_REQUEST
            } else {
                StatusCode::INTERNAL_SERVER_ERROR
            }
        })?;

        res.map(Json).ok_or(StatusCode::INTERNAL_SERVER_ERROR)
    }
}

async fn get_item(
    Path(code): Path<ItemCode>,
    Extension(ctx): Extension<Arc<Context>>,
) -> Result<Json<Item>, StatusCode> {
    let res = ctx.select_item(code).await.map_err(to_server_error)?;

    res.map(Json).ok_or(StatusCode::NOT_FOUND)
}

async fn charge_qty(
    Path((code, qty)): Path<(ItemCode, Quantity)>,
    Extension(ctx): Extension<Arc<Context>>,
) -> Result<Json<Item>, StatusCode> {
    if qty <= 0 {
        Err(StatusCode::BAD_REQUEST)
    } else {
        let res = ctx.charge_qty(code, qty).await.map_err(to_server_error)?;

        res.map(Json).ok_or(StatusCode::NOT_FOUND)
    }
}

async fn create_cart(
    Extension(ctx): Extension<Arc<Context>>,
    Json(input): Json<CreateCart>,
) -> Result<Json<Cart>, StatusCode> {
    if input.cart_id.trim().is_empty() {
        Err(StatusCode::BAD_REQUEST)
    } else {
        let res = ctx.create_cart(input).await.map_err(|e| {
            println!("create cart error: {}", e);

            if e.is_already_exists() {
                StatusCode::BAD_REQUEST
            } else {
                StatusCode::INTERNAL_SERVER_ERROR
            }
        })?;

        res.map(Json).ok_or(StatusCode::INTERNAL_SERVER_ERROR)
    }
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
    Json(input): Json<CartItem>,
) -> Result<Json<Cart>, StatusCode> {
    if cart_id.trim().is_empty() || input.item_code.trim().is_empty() || input.qty <= 0 {
        Err(StatusCode::BAD_REQUEST)
    } else {
        let res = ctx
            // .add_item_to_cart(cart_id, input)
            .add_item_to_cart_alt(cart_id, input)
            .await
            .map_err(to_server_error)?;

        res.map(Json).ok_or(StatusCode::INTERNAL_SERVER_ERROR)
    }
}

fn to_server_error(e: surrealdb::types::Error) -> StatusCode {
    println!("error: {}", e);
    StatusCode::INTERNAL_SERVER_ERROR
}
