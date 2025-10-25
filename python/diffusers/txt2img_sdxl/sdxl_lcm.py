from diffusers import StableDiffusionXLPipeline, UNet2DConditionModel, LCMScheduler

import torch

import yaml
import sys

with open(sys.argv[1]) as f:
    cfg = yaml.safe_load(f)

local_only = True

model = cfg['model']
unet_model = cfg['unet_model']
device = cfg['device']
steps = cfg['steps']
guidance_scale = cfg['guidance_scale']
dest = cfg['output_dir']
negative_prompt = cfg['negative_prompt']

unet = UNet2DConditionModel.from_pretrained(
    unet_model,
    torch_dtype=torch.float16, 
    variant='fp16', 
    local_files_only=local_only,
)

pipe = StableDiffusionXLPipeline.from_pretrained(
    model, 
    unet=unet,
    torch_dtype=torch.float16, 
    variant='fp16', 
    use_safetensors=True,
    local_files_only=local_only,
)

pipe = pipe.to(device)
pipe.scheduler = LCMScheduler.from_config(pipe.scheduler.config)

for c in cfg['images']:
    id = c['id']
    num = c['num']
    prompt = c['prompt']

    for i in range(num):
        seed = torch.seed()
        generator = torch.Generator(device).manual_seed(seed)

        img = pipe(
            prompt, 
            negative_prompt=negative_prompt,
            generator=generator, 
            num_inference_steps=steps, 
            guidance_scale=guidance_scale
        ).images[0]
        
        img.save(f"{dest}/{id}_{seed}.png")
