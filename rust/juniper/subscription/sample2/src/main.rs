use juniper::{
    graphql_object, graphql_subscription, Context, GraphQLInputObject,
    GraphQLObject, RootNode, Variables,
};

use juniper::futures::channel::mpsc;
use juniper::futures::executor;
use juniper::futures::{Stream, StreamExt};

use juniper_subscriptions::Connection;

use std::pin::Pin;
use std::sync::{Arc, RwLock};

#[derive(Debug, Clone, GraphQLInputObject)]
struct CreateItem {
    value: i32,
}

#[derive(Debug, Clone, GraphQLObject)]
struct Item {
    id: String,
    value: i32,
}

#[derive(Default)]
struct Store {
    subscribes: Arc<RwLock<Vec<mpsc::Sender<Item>>>>,
}

impl Store {
    fn subscribe(&self, s: mpsc::Sender<Item>) {
        if let Ok(mut vs) = self.subscribes.write() {
            vs.push(s);
        }
    }

    fn publish(&self, msg: Item) {
        if let Ok(vs) = self.subscribes.read() {
            for mut s in vs.clone() {
                let r = s.try_send(msg.clone());

                match r {
                    Ok(s) => println!("send success: {:?}", s),
                    Err(e) => println!("send ERROR: {:?}", e),
                }
            }
        }
    }
}

impl Context for Store {}

struct Query;

#[graphql_object(context = Store)]
impl Query {
    fn new() -> Self {
        Self
    }
}

struct Mutation;

#[graphql_object(context = Store)]
impl Mutation {
    fn create(ctx: &Store, input: CreateItem) -> Item {
        let item = Item {
            id: "".to_string(),
            value: input.value,
        };

        ctx.publish(item.clone());

        item
    }
}

type ItemStream = Pin<Box<dyn Stream<Item = Item> + Send>>;

struct Subscription;

#[graphql_subscription(context = Store)]
impl Subscription {
    async fn created(ctx: &Store) -> ItemStream {
        let (sender, receiver) = mpsc::channel::<Item>(5);

        ctx.subscribe(sender);

        Box::pin(receiver)
    }
}

type Schema = RootNode<'static, Query, Mutation, Subscription>;

async fn run() {
    let ctx = Store::default();
    let schema = Schema::new(Query, Mutation, Subscription);

    let s = r#"
        subscription {
            created {
                id
                value
            }
        }
    "#;

    let v = Variables::new();

    let (r, e) = juniper::resolve_into_stream(s, None, &schema, &v, &ctx)
        .await
        .unwrap();

    let mut con = Connection::from_stream(r, e);

    let m = r#"
        mutation {
            create(input: { value: 123 }) {
                id
                value
            }
        }
    "#;

    let (mr, _) = juniper::execute(m, None, &schema, &v, &ctx).await.unwrap();

    println!("{}", serde_json::to_string(&mr).unwrap());

    if let Some(res) = con.next().await {
        println!("*** received: {}", serde_json::to_string(&res).unwrap());
    }
}

fn main() {
    executor::block_on(run());
}
