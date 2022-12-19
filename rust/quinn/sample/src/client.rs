use quinn::{ClientConfig, Endpoint};

use std::env;
use std::sync::Arc;

type Result<T> = std::result::Result<T, Box<dyn std::error::Error>>;

#[tokio::main]
async fn main() -> Result<()> {
    let msg = env::args().skip(1).next().ok_or("no message")?;

    let addr = "127.0.0.1:5001".parse()?;
    let server_addr = "127.0.0.1:5000".parse()?;

    let client_crypto = rustls::ClientConfig::builder()
        .with_safe_defaults()
        .with_custom_certificate_verifier(Arc::new(DummyServerVerification))
        .with_no_client_auth();

    let mut endpoint = Endpoint::client(addr)?;

    let config = ClientConfig::new(Arc::new(client_crypto));
    endpoint.set_default_client_config(config);

    let con = endpoint.connect(server_addr, "localhost")?.await?;

    println!("connected: {:?}", con.stats());

    let mut send = con.open_uni().await?;
    
    send.write(msg.as_bytes()).await?;
    
    send.finish().await?;

    Ok(())
}

struct DummyServerVerification;

impl rustls::client::ServerCertVerifier for DummyServerVerification {
    fn verify_server_cert(
        &self,
        _end_entity: &rustls::Certificate,
        _intermediates: &[rustls::Certificate],
        server_name: &rustls::ServerName,
        _scts: &mut dyn Iterator<Item = &[u8]>,
        _ocsp_response: &[u8],
        _now: std::time::SystemTime,
    ) -> std::result::Result<rustls::client::ServerCertVerified, rustls::Error> {

        println!("*** verify: server_name={:?}", server_name);

        Ok(rustls::client::ServerCertVerified::assertion())
    }
}
