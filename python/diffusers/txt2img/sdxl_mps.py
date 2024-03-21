from diffusers import StableDiffusionXLPipeline
import torch
import sys

model = './sd_xl_base_1.0.safetensors'
config = './sd_xl_base.yaml'

steps = 40

prompt = sys.argv[1]
dest_file = sys.argv[2]

pipe = StableDiffusionXLPipeline.from_single_file(model, torch_dtype=torch.float16, variant='fp16', original_config_file=config, local_files_only=True)
pipe = pipe.to('mps')

img = pipe(prompt, num_inference_steps=steps).images[0]

img.save(dest_file)
