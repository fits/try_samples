use candle_core::{DType, Device, Module, Tensor};
use candle_transformers::models::stable_diffusion::{
    build_clip_transformer, clip::ClipTextTransformer, StableDiffusionConfig,
};
use serde::{Deserialize, Serialize};
use std::env;
use tokenizers::{Result, Tokenizer};

#[derive(Debug, Clone, Serialize, Deserialize)]
struct Task {
    tokenizer: String,
    clip: String,
    unet: String,
    vae: String,
    prompt: String,
    negative_prompt: Option<String>,
    steps: usize,
    guidance_scale: f64,
    output_file: Option<String>,
    width: Option<usize>,
    height: Option<usize>,
}

fn main() -> Result<()> {
    let task: Task = {
        let file = env::args().nth(1).ok_or("task file")?;
        serde_json::from_str(&std::fs::read_to_string(file)?)?
    };

    let vae_scale = 0.18215;
    let pad_token = "<|endoftext|>";
    let use_flash_attn = false;

    let dtype = DType::F32;

    let device = Device::Cpu;

    let config = StableDiffusionConfig::v1_5(None, task.height, task.width);

    let tokenizer = Tokenizer::from_file(task.tokenizer)?;
    let pad_id = tokenizer
        .token_to_id(&pad_token)
        .ok_or("not found eos")?;

    let clip = build_clip_transformer(&config.clip, task.clip, &device, dtype)?;

    println!("clip");

    let prompt_embeds = text_embeddings(
        &device,
        &clip,
        &tokenizer,
        &task.prompt,
        config.clip.max_position_embeddings,
        pad_id,
    )?;

    let ng_prompt_embeds = text_embeddings(
        &device,
        &clip,
        &tokenizer,
        &task.negative_prompt.unwrap_or("".into()),
        config.clip.max_position_embeddings,
        pad_id,
    )?;

    let vae = config.build_vae(task.vae, &device, dtype)?;
    let unet = config.build_unet(task.unet, &device, 4, use_flash_attn, dtype)?;

    let mut scheduler = config.build_scheduler(task.steps)?;

    let timesteps = scheduler.timesteps().to_vec();

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

        let noize_pred_text = unet.forward(&input, timestep as f64, &prompt_embeds)?;
        let noize_pred_ng = unet.forward(&input, timestep as f64, &ng_prompt_embeds)?;

        let noize_pred = (((noize_pred_text - &noize_pred_ng)? * task.guidance_scale)? + noize_pred_ng)?;

        latents = scheduler.step(&noize_pred, timestep, &latents)?;

        println!("  done: {}/{}, timestep={timestep}", i + 1, task.steps);
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
        task.output_file.unwrap_or("output.png".into()),
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
    prompt: &str,
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
