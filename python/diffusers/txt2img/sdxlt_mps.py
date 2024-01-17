from diffusers import AutoPipelineForText2Image

import torch

import os
import sys

n = 3
steps = 1

dest = sys.argv[1]
prompt = sys.argv[2]

os.makedirs(dest, exist_ok=True)

pipe = AutoPipelineForText2Image.from_pretrained("stabilityai/sdxl-turbo", use_safetensors=True, local_files_only=True)
pipe = pipe.to('mps')

pipe.enable_attention_slicing()

imgs = pipe(prompt, guidance_scale=0.0, num_inference_steps=steps, num_images_per_prompt=n).images

for i, img in enumerate(imgs):
    img.save(f"{dest}/{i}.png")
