
use juniper::{
    graphql_object, graphql_subscription, 
    Context, EmptyMutation, GraphQLObject,
    RootNode, SubscriptionCoordinator, Variables,
};

use juniper::futures::executor;
use juniper::futures::stream::{self, Stream, StreamExt};
use juniper::http::GraphQLRequest;

use juniper_subscriptions::{Connection, Coordinator};

use std::pin::Pin;

#[derive(Debug, Clone, GraphQLObject)]
struct Data {
    id: String,
    value: i32,
}

struct Store;

impl Context for Store {}

struct Query;

#[graphql_object(context = Store)]
impl Query {
    fn new() -> Self {
        Self
    }
}

type DataStream = Pin<Box<dyn Stream<Item = Data> + Send>>;

struct Subscription;

#[graphql_subscription(context = Store)]
impl Subscription {
    async fn sample() -> DataStream {
        let data = Data {
            id: "sample-1".to_string(),
            value: 1,
        };

        Box::pin(stream::once(async { data }))
    }
}

type Schema = RootNode<'static, Query, EmptyMutation<Store>, Subscription>;

async fn run1() {
    let ctx = Store;
    let schema = Schema::new(Query, EmptyMutation::new(), Subscription);

    let q = r#"{
        "query": "subscription { sample { id value } }"
    }"#;

    let req: GraphQLRequest = serde_json::from_str(q).unwrap();

    let crd = Coordinator::new(schema);

    let con = crd.subscribe(&req, &ctx).await.unwrap();

    let items = con.collect::<Vec<_>>().await;

    println!("{:?}", items);
    println!("{}", serde_json::to_string(&items).unwrap());
}

async fn run2() {
    let ctx = Store;
    let schema = Schema::new(Query, EmptyMutation::new(), Subscription);

    let q = r#"
        subscription {
            sample {
                id
                value
            }
        }
    "#;

    let v = Variables::new();

    let (s, e) = juniper::resolve_into_stream(q, None, &schema, &v, &ctx)
        .await
        .unwrap();

    let con = Connection::from_stream(s, e);

    let items = con.collect::<Vec<_>>().await;

    println!("{:?}", items);
    println!("{}", serde_json::to_string(&items).unwrap());
}

fn main() {
    executor::block_on(run1());
    
    println!("-----");

    executor::block_on(run2());
}
