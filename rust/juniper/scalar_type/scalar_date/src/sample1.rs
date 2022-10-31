use chrono::{prelude::*, Duration};
use juniper::{execute_sync, EmptyMutation, EmptySubscription, Variables};

#[derive(juniper::GraphQLScalarValue)]
struct SampleDate(DateTime<Utc>);

struct Query;

#[juniper::graphql_object(context = Context)]
impl Query {
    fn now() -> SampleDate {
        SampleDate(Utc::now())
    }

    fn nextDay(d: SampleDate) -> SampleDate {
        SampleDate(d.0 + Duration::days(1))
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
        { nextDay(d: "2022-10-31T05:00:00Z") }
    "#;

    let (r2, _) = execute_sync(q2, None, &schema, &Variables::new(), &ctx).unwrap();

    println!("{:?}", r2);

    let s2 = serde_json::to_string_pretty(&r2).unwrap();

    println!("{}", s2);
}
