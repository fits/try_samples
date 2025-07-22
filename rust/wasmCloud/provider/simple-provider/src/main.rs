use anyhow::Context as _;
use wasmcloud_provider_sdk::{
    Context, Provider, get_connection, run_provider, serve_provider_exports,
};

use crate::exports::simple::greeting::types::Handler;

wit_bindgen_wrpc::generate!();

#[derive(Clone, Default)]
pub struct SimpleProvider {}

impl SimpleProvider {
    fn name() -> &'static str {
        "simple-provider"
    }

    pub async fn run() -> anyhow::Result<()> {
        let provider = Self::default();

        let shutdown = run_provider(provider.clone(), Self::name())
            .await
            .context("failed run_provider")?;

        let con = get_connection();

        let client = con.get_wrpc_client(con.provider_key()).await?;

        serve_provider_exports(&client, provider, shutdown, serve)
            .await
            .context("failed serve_provider_exports")
    }
}

impl Provider for SimpleProvider {}

impl Handler<Option<Context>> for SimpleProvider {
    fn hello(
        &self,
        _cx: Option<Context>,
        name: String,
    ) -> impl ::core::future::Future<
        Output = wit_bindgen_wrpc::anyhow::Result<::core::option::Option<String>>,
    > + ::core::marker::Send {
        let msg = format!("Hi, {}", name);

        async { Ok(Some(msg)) }
    }
}

#[tokio::main]
async fn main() -> anyhow::Result<()> {
    SimpleProvider::run().await.context("failed run")?;
    Ok(())
}
