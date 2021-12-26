use graphql_parser::query::{parse_query, ParseError};

fn main() -> Result<(), ParseError> {
    let ast = parse_query::<String>(r#"
        fragment ItemInfo on Item {
            id
            name
            price
        }

        query {
            items {
                ...ItemInfo
                color
            }
        }
    "#)?;

    println!("{}", ast);
    println!("{:?}", ast);

    Ok(())
}
