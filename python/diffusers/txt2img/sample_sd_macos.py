from diffusers import DiffusionPipeline

import os
import sys

n = 3

dest = sys.argv[1]
prompt = sys.argv[2]

os.makedirs(dest, exist_ok=True)

pipe = DiffusionPipeline.from_pretrained('runwayml/stable-diffusion-v1-5')
pipe = pipe.to('mps')

pipe.enable_attention_slicing()

imgs = pipe(prompt, num_images_per_prompt=n).images

for i, img in enumerate(imgs):
    img.save(f"{dest}/{i}.png")
