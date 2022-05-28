
mod task;

use sea_orm::*;
use serde_json::json;
use std::env;

use task::Entity as Task;

type Error = Box<dyn std::error::Error>;

#[async_std::main]
async fn main() -> Result<(), Error> {
    let db_uri = env::var("DB_URI")?;
    let db = Database::connect(db_uri).await?;

    let backend = db.get_database_backend();
    let schema = Schema::new(backend);

    let st = backend.build(&schema.create_table_from_entity(Task));

    db.execute(st).await?;

    let t1 = task::ActiveModel {
        id: ActiveValue::Set(1),
        subject: ActiveValue::Set("task1".to_owned()),
        status: ActiveValue::Set(task::Status::Ready),
    };

    let r1 = t1.insert(&db).await?;
    println!("{:?}", r1);

    let t2 = task::ActiveModel::from_json(json!({
        "id": 2,
        "subject": "task2",
        "status": "Completed",
    }))?;

    let r2 = t2.insert(&db).await?;
    println!("{:?}", r2);

    let rows = Task::find().all(&db).await?;

    println!("{:?}", rows);

    Ok(())
}
