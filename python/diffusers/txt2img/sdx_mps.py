from diffusers import DiffusionPipeline
import torch
import sys

model = 'stabilityai/stable-diffusion-xl-base-1.0'

local = True
ng_prompt = 'ugly, deformed, disfigured, poor details, bad anatomy'

prompt = sys.argv[1]
seed = int(sys.argv[2])
destfile = sys.argv[3]

pipe = DiffusionPipeline.from_pretrained(model, use_safetensors=True, local_files_only=local)
pipe = pipe.to('mps')

pipe.enable_attention_slicing()

generator = torch.Generator('mps').manual_seed(seed)

img = pipe(prompt, negative_prompt=ng_prompt, generator=generator).images[0]

img.save(destfile)
