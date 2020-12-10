use paho_mqtt as mqtt;
use std::time::Duration;

const BROKER_URI: &str = "tcp://localhost:1883";
const TOPIC: &str = "sample";
const QOS: i32 = 1;

fn main() {
    let client = mqtt::AsyncClient::new(BROKER_URI).unwrap();

    let con_opts = mqtt::ConnectOptionsBuilder::new()
        //.mqtt_version(5) // ERROR: Wrong MQTT version
        .connect_timeout(Duration::from_secs(5))
        .finalize();

    println!("{:?}", con_opts);

    let con = client.connect(con_opts)
        .wait()
        .unwrap();

    println!("{:?}", con);

    let msg = mqtt::Message::new(TOPIC, "test123", QOS);

    let t = client.publish(msg);

    if let Err(e) = t.wait() {
        println!("ERROR: {:?}", e);
    }

    client.disconnect(mqtt::DisconnectOptions::new())
        .wait()
        .unwrap();
}
