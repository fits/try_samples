use hf_hub::Cache;
use std::path::PathBuf;

#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    let path = PathBuf::from("./model");
    let cache = Cache::new(path);

    let api = hf_hub::api::tokio::ApiBuilder::from_cache(cache)
        .with_max_files(8)
        .build()?;

    api.model("openai/clip-vit-base-patch32".into())
        .download("tokenizer.json")
        .await?;

    let model = api.model("runwayml/stable-diffusion-v1-5".into());

    let files = vec![
        "unet/diffusion_pytorch_model.safetensors",
        "vae/diffusion_pytorch_model.safetensors",
        "text_encoder/model.safetensors",
    ];

    for f in files {
        model.download(f).await?;
    }

    Ok(())
}
