
mod cargo;

use chrono::prelude::*;
use cargo::*;

fn main() {
    let deadline = Local::now().checked_add_days(chrono::Days::new(10)).unwrap();
    
    let rs = RouteSpec {
        origin: "loc1".to_string(), 
        destination: "loc2".to_string(), 
        deadline
    };

    let r = Cargo::Nothing.action(&Command::Create("t1".to_string(), rs));

    println!("{:?}", r);

    let (_, e) = r.unwrap();
    
    if let Event::Created { tracking_id, route_spec } = e {
        println!("tracking_id={}, route_spec={:?}", tracking_id, route_spec);
    }
}
