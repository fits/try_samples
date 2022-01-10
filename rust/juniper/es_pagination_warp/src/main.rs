use juniper::{
    EmptySubscription, EmptyMutation, 
    FieldError, FieldResult, ID
};
use elasticsearch::{Elasticsearch, SearchParts};
use serde_json::Value;
use warp::{Filter};

#[derive(Debug, Clone, juniper::GraphQLObject)]
struct PageInfo {
    total: i32,
    size: i32,
    from: i32,
}

#[derive(Debug, Clone, juniper::GraphQLObject)]
struct Item {
    id: ID,
    name: String,
    value: i32,
}

#[derive(Debug, Clone, juniper::GraphQLObject)]
struct ItemConnection {
    edges: Vec<Item>,
    page_info: PageInfo,
}

impl From<&Value> for Item {
    fn from(v: &Value) -> Self {
        let id = v["_id"].as_str().unwrap_or("");
        let name = v["_source"]["name"].as_str().unwrap_or("").to_string();
        let value = v["_source"]["value"].as_i64().unwrap_or(0) as i32;

        Item {
            id: ID::new(id),
            name,
            value,
        }
    }
}

#[derive(Default)]
struct Store {
    db: Elasticsearch,
}

impl juniper::Context for Store {}

fn error(msg: String) -> FieldError {
    FieldError::new(msg, juniper::Value::Null)
}

#[derive(Debug)]
struct Query;

#[juniper::graphql_object(context = Store)]
impl Query {
    async fn items(ctx: &Store, size: Option<i32>, from: Option<i32>) -> FieldResult<ItemConnection> {
        let size = size.unwrap_or(10);
        let from = from.unwrap_or(0);

        let res = ctx.db.search(SearchParts::Index(&["items"]))
            .size(size.into())
            .from(from.into())
            .send()
            .await
            .map_err(|e| error(format!("{:?}", e)))?;

        let body = res.json::<Value>().await?;

        let total = body["hits"]["total"]["value"].as_i64().unwrap_or(0);

        let edges = body["hits"]["hits"]
            .as_array()
            .map(|rs| 
                rs.iter().map(Into::<Item>::into).collect::<Vec<_>>()
            )
            .unwrap_or(vec![]);

        Ok(
            ItemConnection {
                edges, 
                page_info: PageInfo {
                    total: total as i32,
                    size,
                    from,
                },
            }
        )
    }
}

type Schema = juniper::RootNode<'static, Query, EmptyMutation<Store>, EmptySubscription<Store>>;

#[tokio::main]
async fn main() {
    let schema = Schema::new(Query, EmptyMutation::new(), EmptySubscription::new());

    let state = warp::any().map(move || Store::default());
    let graphql_filter = juniper_warp::make_graphql_filter(schema, state.boxed());

    warp::serve(
        warp::path("graphql").and(graphql_filter)
    )
    .run(([127, 0, 0, 1], 4000))
    .await
}
