use juniper::{
    execute_sync, graphql_interface, graphql_object, 
    EmptyMutation, EmptySubscription,
    GraphQLObject, Variables,
};

struct Context;

#[graphql_interface(for = [EmptyCart, ActiveCart])]
trait Cart {
    fn id(&self) -> &str;
}

#[derive(Debug, Clone, GraphQLObject)]
struct CartItem {
    item: String,
    qty: i32,
}

#[derive(Debug, Clone, GraphQLObject)]
#[graphql(impl = CartValue)]
struct EmptyCart {
    id: String,
}

#[graphql_interface]
impl Cart for EmptyCart {
    fn id(&self) -> &str {
        &self.id
    }
}

#[derive(Debug, Clone, GraphQLObject)]
#[graphql(impl = CartValue)]
struct ActiveCart {
    id: String,
    items: Vec<CartItem>,
}

#[graphql_interface]
impl Cart for ActiveCart {
    fn id(&self) -> &str {
        &self.id
    }
}

impl juniper::Context for Context {}

struct Query;

#[graphql_object(context = Context)]
impl Query {
    fn findAll() -> Vec<CartValue> {
        vec![
            EmptyCart {
                id: "cart-1".to_string(),
            }
            .into(),
            ActiveCart {
                id: "cart-2".to_string(),
                items: vec![
                    CartItem {
                        item: "item-A".to_string(),
                        qty: 1,
                    },
                    CartItem {
                        item: "item-B".to_string(),
                        qty: 2,
                    },
                ],
            }
            .into(),
            EmptyCart {
                id: "cart-3".to_string(),
            }
            .into(),
        ]
    }
}

type Schema = juniper::RootNode<'static, Query, EmptyMutation<Context>, EmptySubscription<Context>>;

fn main() {
    let ctx = Context;
    let schema = Schema::new(Query, EmptyMutation::new(), EmptySubscription::new());

    let q = r#"
        {
            findAll {
                __typename
                id
                ... on ActiveCart {
                    items {
                        item
                        qty
                    }
                }
            }
        }
    "#;

    let (r, _) = execute_sync(q, None, &schema, &Variables::new(), &ctx).unwrap();

    println!("{:?}", r);
    println!("----------");
    println!("{}", r);
}
