
use std::io;
use std::time::Duration;
use nats::connect;

fn main() -> io::Result<()> {
    let nc = connect("localhost")?;
    let sub = nc.subscribe("sample")?;

    let res = nc.publish("sample", "test-message-1")?;
    println!("*** published: {:?}", res);

    if let Some(msg) = sub.next() {
        println!("*** received message: {:?}", msg);

        if let Ok(s) = String::from_utf8(msg.data) {
            println!("*** message body: {}", s);
        }
    }

    if let Ok(msg) = sub.next_timeout(Duration::from_secs(1)) {
        println!("*** received message2: {:?}", msg);
    }
    else {
        println!("*** timeout");
    }

    sub.unsubscribe()
}