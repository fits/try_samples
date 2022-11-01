use chrono::{DateTime, Utc, Duration};
use juniper::{execute_sync, EmptyMutation, EmptySubscription, Variables};

struct Query;

#[juniper::graphql_object(context = Context)]
impl Query {
    fn now() -> DateTime<Utc> {
        Utc::now()
    }

    fn next_day(d: DateTime<Utc>) -> DateTime<Utc> {
        d + Duration::days(1)
    }
}

#[derive(Clone, Copy, Debug)]
struct Context;
impl juniper::Context for Context {}

type Schema = juniper::RootNode<'static, Query, EmptyMutation<Context>, EmptySubscription<Context>>;

fn main() {
    let ctx = Context;
    let schema = Schema::new(
        Query, 
        EmptyMutation::new(), 
        EmptySubscription::new(),
    );

    let q1 = r#"
        { now }
    "#;

    let (r1, _) = execute_sync(q1, None, &schema, &Variables::new(), &ctx).unwrap();

    println!("{:?}", r1);

    let s1 = serde_json::to_string_pretty(&r1).unwrap();
    
    println!("{}", s1);

    let q2 = r#"
        { nextDay(d: "2022-11-01T05:00:00Z") }
    "#;

    let (r2, _) = execute_sync(q2, None, &schema, &Variables::new(), &ctx).unwrap();

    println!("{:?}", r2);

    let s2 = serde_json::to_string_pretty(&r2).unwrap();

    println!("{}", s2);
}
