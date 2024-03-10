from diffusers import StableDiffusionPipeline, DPMSolverMultistepScheduler

import torch

import yaml
import sys

with open(sys.argv[1]) as f:
    cfg = yaml.safe_load(f)

model = cfg['model']
original_config_file = cfg['original_config_file']

device = cfg['device']

steps = cfg['steps']
guidance_scale = cfg['guidance_scale']
clip_skip = cfg['clip_skip']
strength = cfg['strength']
negative_prompt = cfg['negative_prompt']

output_dir = cfg['output_dir']

pipe = StableDiffusionPipeline.from_single_file(model, use_safetensors=True, original_config_file=original_config_file, local_files_only=True, load_safety_checker=False)
pipe = pipe.to(device)

pipe.scheduler = DPMSolverMultistepScheduler.from_config(pipe.scheduler.config, use_karras_sigmas=True)

pipe.enable_attention_slicing()

for c in cfg['images']:
    id = c['id']
    seed = c['seed']

    generator = torch.Generator(device).manual_seed(seed)

    for p in c['prompts']:
        sid = p['id']
        prompt = p['prompt']

        latent = pipe(prompt, output_type='latent',  generator=generator, negative_prompt=negative_prompt, num_inference_steps=steps, guidance_scale=guidance_scale, clip_skip=clip_skip, strength=strength).images[0]

        torch.save(latent, f"{output_dir}/{id}_{sid}.pt")
