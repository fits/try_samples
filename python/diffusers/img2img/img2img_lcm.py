from diffusers import AutoPipelineForImage2Image, LCMScheduler
from diffusers.utils import load_image

import torch

import yaml
import sys

with open(sys.argv[1]) as f:
    cfg = yaml.safe_load(f)

model = cfg['model']
device = cfg['device']
steps = cfg['steps']
dest = cfg['output_dir']
ng_prompt = cfg['negative_prompt']

try:
    pipe = AutoPipelineForImage2Image.from_pretrained(model, use_safetensors=True, local_files_only=True)
except EnvironmentError:
    print('load model files')
    pipe = AutoPipelineForImage2Image.from_pretrained(model, use_safetensors=True, local_files_only=False)

pipe = pipe.to(device)

pipe.scheduler = LCMScheduler.from_config(pipe.scheduler.config)

pipe.enable_attention_slicing()

for c in cfg['images']:
    id = c['id']
    seed = c['seed']
    gscale = c['guidance_scale']
    strength = c['strength']

    input = load_image(c['input']).resize((512, 512))

    generator = torch.Generator(device).manual_seed(seed)

    for p in c['prompts']:
        sid = p['id']
        prompt = p['prompt']

        img = pipe(prompt, negative_prompt=ng_prompt, image=input, generator=generator, num_inference_steps=steps, guidance_scale=gscale, strength=strength).images[0]
        img.save(f"{dest}/{id}_{sid}.png")
