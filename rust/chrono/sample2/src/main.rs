use chrono::prelude::*;
use chrono::Duration;

fn main() {
    let d = "2022-10-24T15:25:00+09:00".parse::<DateTime<Local>>();

    println!("{:?}", d);

    let d2 = d.unwrap() + Duration::days(1);
    
    println!("next day: {}", d2.to_rfc3339());
}
