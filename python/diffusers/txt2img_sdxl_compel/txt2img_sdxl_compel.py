from diffusers import StableDiffusionXLPipeline
from compel import CompelForSDXL

import torch

import yaml
import sys

with open(sys.argv[1]) as f:
    cfg = yaml.safe_load(f)

model = cfg['model']
device = cfg['device']
steps = cfg['steps']
dest = cfg['output_dir']
guidance_scale = cfg['guidance_scale']
negative_prompt = cfg['negative_prompt']
width = cfg['width']
height = cfg['height']

def load_model(local_only):
    return StableDiffusionXLPipeline.from_pretrained(
        model, 
        torch_dtype=torch.bfloat16, 
        use_safetensors=True,
        local_files_only=local_only,
    )

try:
    pipe = load_model(True)
except EnvironmentError:
    pipe = load_model(False)

pipe = pipe.to(device)

compel = CompelForSDXL(pipe)

for c in cfg['images']:
    id = c['id']
    num = c['num']
    prompt = c['prompt']

    cond = compel(prompt, negative_prompt=negative_prompt)

    for i in range(num):
        seed = torch.seed()
        generator = torch.Generator(device).manual_seed(seed)

        img = pipe(
            prompt_embeds=cond.embeds,
            pooled_prompt_embeds=cond.pooled_embeds,
            negative_prompt_embeds=cond.negative_embeds,
            negative_pooled_prompt_embeds=cond.negative_pooled_embeds,
            generator=generator, 
            num_inference_steps=steps, 
            width=width,
            height=height,
            guidance_scale=guidance_scale,
        ).images[0]
        
        img.save(f"{dest}/{id}_{seed}.png")