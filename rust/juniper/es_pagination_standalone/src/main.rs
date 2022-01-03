use juniper::{
    execute,
    EmptySubscription, EmptyMutation, 
    FieldError, FieldResult, 
    ID, InputValue, Variables, 
};
use elasticsearch::{Elasticsearch, SearchParts};
use serde_json::Value;
use std::collections::HashMap;

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

async fn query(q: &str, schema: &Schema, vars: &Variables, ctx: &Store) {
    let r = execute(q, None, &schema, &vars, &ctx).await;

    match r {
        Ok((v, e)) => println!("value = {}, errors = {:?}", v, e),
        Err(e) => println!("ERROR: {}", e),
    };
}

#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    let ctx = Store::default();
    let schema = Schema::new(Query, EmptyMutation::new(), EmptySubscription::new());

    let q1 = r#"
        fragment ItemData on Item {
            id
            name
            value
        }

        query {
            items {
                edges {
                    ...ItemData
                }
                pageInfo {
                    total
                    size
                    from
                }
            }
        }
    "#;

    query(q1, &schema, &Variables::new(), &ctx).await;

    let q2 = r#"
        fragment ItemData on Item {
            id
            name
            value
        }

        query Items($size: Int!, $from: Int!) {
            items(size: $size, from: $from) {
                edges {
                    ...ItemData
                }
                pageInfo {
                    total
                    size
                    from
                }
            }
        }
    "#;

    let v2 = HashMap::from([
        ("size".to_string(), InputValue::scalar(5)),
        ("from".to_string(), InputValue::scalar(10)),
    ]);

    query(q2, &schema, &v2, &ctx).await;

    Ok(())
}
