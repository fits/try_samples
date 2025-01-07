use candle_core::{DType, Device, Module, Tensor};
use candle_transformers::models::stable_diffusion::{
    build_clip_transformer, clip::ClipTextTransformer, StableDiffusionConfig,
};
use std::env;
use tokenizers::{Result, Tokenizer};

fn main() -> Result<()> {
    let tokenizer_file = "model/tokenizer/tokenizer.json";
    let clip_file = "model/text_encoder/model.safetensors";
    let unet_file = "model/unet/diffusion_pytorch_model.safetensors";
    let vae_file = "model/vae/diffusion_pytorch_model.safetensors";

    let n_steps = 40;
    let use_flash_attn = false;
    let vae_scale = 0.18215;

    let dtype = DType::F32;

    let prompt = env::args().nth(1).ok_or("prompt")?;

    let device = Device::Cpu;

    let config = StableDiffusionConfig::v1_5(None, None, None);

    let tokenizer = Tokenizer::from_file(tokenizer_file)?;
    let pad_id = tokenizer
        .token_to_id("<|endoftext|>")
        .ok_or("not found endoftext")?;

    let clip = build_clip_transformer(&config.clip, clip_file, &device, dtype)?;

    println!("clip");

    let prompt_embeds = text_embeddings(
        &device,
        &clip,
        &tokenizer,
        prompt,
        config.clip.max_position_embeddings,
        pad_id,
    )?;

    let vae = config.build_vae(vae_file, &device, dtype)?;
    let unet = config.build_unet(unet_file, &device, 4, use_flash_attn, dtype)?;

    let scheduler = config.build_scheduler(n_steps)?;

    let timesteps = scheduler.timesteps();

    let init_latents = Tensor::randn(
        0f32,
        1f32,
        (1, 4, config.height / 8, config.width / 8),
        &device,
    )?;

    let mut latents = (init_latents * scheduler.init_noise_sigma())?.to_dtype(dtype)?;

    println!("unet");

    for (i, &timestep) in timesteps.iter().enumerate() {
        let input = scheduler.scale_model_input(latents.clone(), timestep)?;

        let noize_pred = unet.forward(&input, timestep as f64, &prompt_embeds)?;

        latents = scheduler.step(&noize_pred, timestep, &latents)?;

        println!("  done: {}/{n_steps}, timestep={timestep}", i + 1);
    }

    println!("vae");

    let img = {
        let img = vae.decode(&(latents / vae_scale)?)?;

        let img_p = ((img + 1.)? / 2.)?.to_device(&Device::Cpu)?; // [-1, 1] => [0, 1]
        (img_p.clamp(0f32, 1.)? * 255.)?.to_dtype(DType::U8)? // => [0, 255]
    };

    let buf = img
        .squeeze(0)?
        .permute((1, 2, 0))?
        .flatten_all()?
        .to_vec1::<u8>()?;

    image::save_buffer(
        "output.png",
        &buf,
        config.width as u32,
        config.height as u32,
        image::ColorType::Rgb8,
    )?;

    Ok(())
}

fn text_embeddings(
    device: &Device,
    clip: &ClipTextTransformer,
    tokenizer: &Tokenizer,
    prompt: String,
    tokens_len: usize,
    pad_id: u32,
) -> Result<Tensor> {
    let mut tokens = tokenizer.encode(prompt, true)?.get_ids().to_vec();

    if tokens.len() > tokens_len {
        println!(
            "[WARN] too long prompt: current={}, max={}",
            tokens.len(),
            tokens_len
        );
        tokens.truncate(tokens_len);
    } else {
        while tokens.len() < tokens_len {
            tokens.push(pad_id);
        }
    }

    let tokens_t = Tensor::new(tokens.as_slice(), &device)?.unsqueeze(0)?;

    clip.forward(&tokens_t).map_err(|e| e.into())
}
