from diffusers import StableDiffusionXLPipeline, EulerAncestralDiscreteScheduler
import torch
import sys

model = './sd_xl_turbo_1.0_fp16.safetensors'
config = './sd_xl_base.yaml'

prompt = sys.argv[1]
dest_file = sys.argv[2]

pipe = StableDiffusionXLPipeline.from_single_file(model, torch_dtype=torch.float16, variant='fp16', original_config_file=config, local_files_only=True)
pipe = pipe.to('mps')

pipe.scheduler = EulerAncestralDiscreteScheduler.from_config(pipe.scheduler.config, timestep_spacing='trailing')

img = pipe(prompt, guidance_scale=0.0, num_inference_steps=1).images[0]

img.save(dest_file)
