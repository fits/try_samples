from diffusers import StableDiffusionXLPipeline, StableDiffusionXLImg2ImgPipeline
import torch
import sys

base_model = './sd_xl_base_1.0.safetensors'
base_config = './sd_xl_base.yaml'

refiner_model = './sd_xl_refiner_1.0.safetensors'
refiner_config = './sd_xl_refiner.yaml'

steps = 40
denoise = 0.8

prompt = sys.argv[1]
dest_file = sys.argv[2]

base = StableDiffusionXLPipeline.from_single_file(base_model, torch_dtype=torch.float16, variant='fp16', original_config_file=base_config, local_files_only=True)
base = base.to('mps')

refiner = StableDiffusionXLImg2ImgPipeline.from_single_file(refiner_model, torch_dtype=torch.float16, variant='fp16', original_config_file=refiner_config, local_files_only=True)
refiner = refiner.to('mps')

imgs = base(prompt, num_inference_steps=steps, denoising_end=denoise, output_type='latent').images
img = refiner(prompt, num_inference_steps=steps, denoising_start=denoise, image=imgs).images[0]

img.save(dest_file)
