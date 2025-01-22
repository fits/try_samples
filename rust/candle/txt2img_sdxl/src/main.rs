use candle_core::{DType, Device, Module, Tensor, D};
use candle_transformers::models::stable_diffusion::{
    build_clip_transformer, clip::ClipTextTransformer, StableDiffusionConfig,
};
use std::env;
use tokenizers::{Result, Tokenizer};

fn main() -> Result<()> {
    let tokenizer_file = "model/tokenizer/tokenizer.json";
    let tokenizer2_file = "model/tokenizer_2/tokenizer.json";
    let clip_file = "model/text_encoder/model.safetensors";
    let clip2_file = "model/text_encoder_2/model.safetensors";
    let unet_file = "model/unet/diffusion_pytorch_model.safetensors";
    let vae_file = "model/vae/diffusion_pytorch_model.safetensors";

    let n_steps = 30;
    let use_flash_attn = false;
    let vae_scale = 0.18215;
    let guidance_scale = 7.5;
    let sliced_attention_size = None;

    let dtype = DType::F32;

    let prompt = env::args().nth(1).ok_or("prompt")?;
    let negative_prompt = env::args().nth(2).unwrap_or("".into());

    let device = Device::Cpu;

    let config = StableDiffusionConfig::sdxl(sliced_attention_size, None, None);

    let (tokenizer, pad_id) = create_tokenizer(&tokenizer_file, "<|endoftext|>")?;
    let (tokenizer2, pad2_id) = create_tokenizer(&tokenizer2_file, "<|endoftext|>")?;

    let clip = build_clip_transformer(&config.clip, clip_file, &device, dtype)?;

    let clip2_config = config.clip2.as_ref().unwrap();
    let clip2 = build_clip_transformer(clip2_config, clip2_file, &device, dtype)?;

    println!("clip");

    let prompt_embeds = Tensor::cat(
        &[
            text_embeddings(
                &device,
                &clip,
                &tokenizer,
                &prompt,
                config.clip.max_position_embeddings,
                pad_id,
            )?,
            text_embeddings(
                &device,
                &clip2,
                &tokenizer2,
                &prompt,
                clip2_config.max_position_embeddings,
                pad2_id,
            )?,
        ],
        D::Minus1,
    )?;

    let ng_prompt_embeds = Tensor::cat(
        &[
            text_embeddings(
                &device,
                &clip,
                &tokenizer,
                &negative_prompt,
                config.clip.max_position_embeddings,
                pad_id,
            )?,
            text_embeddings(
                &device,
                &clip2,
                &tokenizer2,
                &negative_prompt,
                clip2_config.max_position_embeddings,
                pad2_id,
            )?,
        ],
        D::Minus1,
    )?;

    let vae = config.build_vae(vae_file, &device, dtype)?;
    let unet = config.build_unet(unet_file, &device, 4, use_flash_attn, dtype)?;

    let mut scheduler = config.build_scheduler(n_steps)?;

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

        let noize_pred = (((noize_pred_text - &noize_pred_ng)? * guidance_scale)? + noize_pred_ng)?;

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

fn create_tokenizer(file: &str, eos_token: &str) -> Result<(Tokenizer, u32)> {
    let tokenizer = Tokenizer::from_file(file)?;

    let pad_id = tokenizer.token_to_id(eos_token).ok_or("not found eos")?;

    Ok((tokenizer, pad_id))
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
