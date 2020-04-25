
use std::env;
use std::thread::sleep;
use std::time::{ Duration, SystemTime };

fn main() {
    let sleep_sec = env::var("SLEEP_TIME").ok()
                         .and_then(|v| v.parse::<u64>().ok())
                         .unwrap_or(1);

    let time = SystemTime::now();

    println!("start: sleep {}s", sleep_sec);

    sleep(Duration::from_secs(sleep_sec));

    match time.elapsed() {
        Ok(s) => println!("end: elapsed {}s", s.as_secs()),
        Err(e) => println!("error: {:?}", e),
    }
}
