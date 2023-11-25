use base64::{
    prelude::{BASE64_STANDARD_NO_PAD, BASE64_URL_SAFE_NO_PAD},
    Engine,
};
use uuid::Uuid;

fn main() {
    let uuid = Uuid::new_v4();

    println!("{}", uuid);

    let b = uuid.as_bytes();

    println!("{}", BASE64_STANDARD_NO_PAD.encode(b));
    println!("{}", BASE64_URL_SAFE_NO_PAD.encode(b));
}
