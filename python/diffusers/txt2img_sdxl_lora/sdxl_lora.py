from diffusers import StableDiffusionXLPipeline, LCMScheduler

import torch

import yaml
import sys

with open(sys.argv[1]) as f:
    cfg = yaml.safe_load(f)

local_only = True

model = cfg['model']
lora = cfg['lora']
device = cfg['device']
steps = cfg['steps']
guidance_scale = cfg['guidance_scale']
dest = cfg['output_dir']
negative_prompt = cfg['negative_prompt']

pipe = StableDiffusionXLPipeline.from_pretrained(
    model, 
    dtype=torch.float16, 
    variant='fp16', 
    use_safetensors=True,
    local_files_only=local_only,
)

pipe = pipe.to(device)
pipe.scheduler = LCMScheduler.from_config(pipe.scheduler.config)

pipe.load_lora_weights(
    lora, 
    weight_name='pytorch_lora_weights.safetensors',
    local_files_only=local_only
)

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
