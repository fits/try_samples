
use quinn::{ServerConfig, Endpoint};
use std::str::from_utf8;

type Result<T> = std::result::Result<T, Box<dyn std::error::Error>>;

#[tokio::main]
async fn main() -> Result<()> {
    let addr = "127.0.0.1:5000".parse()?;
    let cert = rcgen::generate_simple_self_signed(vec!["localhost".to_string()])?;
    
    let key = rustls::PrivateKey(cert.serialize_private_key_der());
    let cert = rustls::Certificate(cert.serialize_der()?);

    let config = ServerConfig::with_single_cert(vec![cert], key)?;

    let endpoint = Endpoint::server(config, addr)?;

    println!("started");

    while let Some(c) = endpoint.accept().await {
        println!("accepted client");

        let con = c.await?;

        let recv = con.accept_uni().await?;

        let buf = recv.read_to_end(1024).await?;

        let msg = from_utf8(&buf)?;

        println!("received: {}", msg);
    }

    Ok(())
}
