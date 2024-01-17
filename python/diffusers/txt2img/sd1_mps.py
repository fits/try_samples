from diffusers import DiffusionPipeline, DPMSolverMultistepScheduler, AutoencoderKL
from diffusers.models.attention_processor import AttnProcessor2_0

import torch

import os
import sys

n = 3
steps = 25

model = 'runwayml/stable-diffusion-v1-5'

seed = int(sys.argv[1])
dest = sys.argv[2]
prompt = sys.argv[3]

os.makedirs(dest, exist_ok=True)

pipe = DiffusionPipeline.from_pretrained(model, use_safetensors=True, local_files_only=True, requires_safety_checker=False, safety_checker=None)

pipe = pipe.to('mps')

pipe.unet.set_attn_processor(AttnProcessor2_0())
pipe.scheduler = DPMSolverMultistepScheduler.from_config(pipe.scheduler.config)

pipe.enable_attention_slicing()

generator = torch.Generator('mps').manual_seed(seed)

pipe.vae = AutoencoderKL.from_pretrained("stabilityai/sd-vae-ft-mse").to('mps')

imgs = pipe(prompt, generator=generator, num_inference_steps=steps, num_images_per_prompt=n).images

for i, img in enumerate(imgs):
    img.save(f"{dest}/{i}.png")
