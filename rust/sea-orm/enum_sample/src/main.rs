
mod task;

use sea_orm::*;
use std::env;

use task::Entity as Task;

type Error = Box<dyn std::error::Error>;

#[async_std::main]
async fn main() -> Result<(), Error> {
    let db_url = env::var("DB_URL")?;

    let db = Database::connect(db_url).await?;

    let rows = Task::find().all(&db).await?;

    println!("{:?}", rows);

    Ok(())
}
