from diffusers import AutoPipelineForImage2Image, DPMSolverMultistepScheduler, AutoencoderKL
from diffusers.models.attention_processor import AttnProcessor2_0
from diffusers.utils import load_image

import sys

steps = 25
local = True

ng_prompt = 'ugly, deformed, disfigured, poor details, bad anatomy'

inputfile = sys.argv[1]
prompt = sys.argv[2]
destfile = sys.argv[3]

pipe = AutoPipelineForImage2Image.from_pretrained('runwayml/stable-diffusion-v1-5', use_safetensors=True, local_files_only=local, requires_safety_checker=False, safety_checker=None)
pipe = pipe.to('mps')

pipe.unet.set_attn_processor(AttnProcessor2_0())
pipe.scheduler = DPMSolverMultistepScheduler.from_config(pipe.scheduler.config)
pipe.vae = AutoencoderKL.from_pretrained('stabilityai/sd-vae-ft-mse').to('mps')

pipe.enable_attention_slicing()

input = load_image(inputfile).resize((512, 512))

img = pipe(prompt, negative_prompt=ng_prompt, image=input, num_inference_steps=steps, guidance_scale=8.0, strength=0.6).images[0]

img.save(destfile)
