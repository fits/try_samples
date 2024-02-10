from diffusers import StableDiffusionPipeline, AutoencoderKL, DPMSolverMultistepScheduler

import torch
import sys

local = True
steps = 10
gscale = 6.0
cskip = 1
strength = 0.5

model = './model.safetensors'
config_file = './v1-inference.yaml'

ng_prompt = 'worst quality,low quality:1.4,normal quality,out of focus,blurry,bokeh,ugly,deformed,disfigured,poor details,bad anatomy'

prompt = sys.argv[1]
seed = int(sys.argv[2])
destfile = sys.argv[3]

vae = AutoencoderKL.from_pretrained('./vae', local_files_only=local)

pipe = StableDiffusionPipeline.from_single_file(model, use_safetensors=True, original_config_file=config_file, vae=vae, local_files_only=local, load_safety_checker=False)

pipe.scheduler = DPMSolverMultistepScheduler.from_config(pipe.scheduler.config, use_karras_sigmas=True)

pipe.enable_vae_slicing()
pipe.enable_attention_slicing()

generator = torch.manual_seed(seed)

imgs = pipe(prompt, negative_prompt=ng_prompt, generator=generator, num_inference_steps=steps, guidance_scale=gscale, clip_skip=cskip, strength=strength).images[0]

imgs.save(destfile)
