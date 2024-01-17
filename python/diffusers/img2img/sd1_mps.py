from diffusers import AutoPipelineForImage2Image, DPMSolverMultistepScheduler
from diffusers.models.attention_processor import AttnProcessor2_0
from diffusers.utils import load_image

import sys

steps = 25

model = 'runwayml/stable-diffusion-v1-5'

inputfile = sys.argv[1]
destfile = sys.argv[2]
prompt = sys.argv[3]

pipe = AutoPipelineForImage2Image.from_pretrained(model, use_safetensors=True, local_files_only=True, requires_safety_checker=False, safety_checker=None)
pipe = pipe.to('mps')

pipe.unet.set_attn_processor(AttnProcessor2_0())
pipe.scheduler = DPMSolverMultistepScheduler.from_config(pipe.scheduler.config)

pipe.enable_attention_slicing()

input = load_image(inputfile)

img = pipe(prompt, image=input, num_inference_steps=steps, guidance_scale=3.0, strength=0.7).images[0]

img.save(destfile)
