from diffusers import Flux2KleinPipeline
import torch
import yaml
import sys

with open(sys.argv[1]) as f:
    cfg = yaml.safe_load(f)

model = 'black-forest-labs/FLUX.2-klein-base-4B'
device = 'cuda'

guidance_scale = cfg['guidance_scale']
steps = cfg['steps']
dest = cfg['output_dir']
width = cfg['width']
height = cfg['height']

def load_model(local_only):
    return Flux2KleinPipeline.from_pretrained(
        model, 
        torch_dtype=torch.bfloat16, 
        local_files_only=local_only,
    )

try:
    pipe = load_model(True)
except EnvironmentError:
    pipe = load_model(False)

pipe = pipe.to(device)

pipe.vae.enable_slicing()
pipe.vae.enable_tiling()
pipe.enable_sequential_cpu_offload()

for c in cfg['images']:
    id = c['id']
    num = c['num']
    prompt = c['prompt']

    for i in range(num):
        seed = torch.seed()
        generator = torch.Generator(device).manual_seed(seed)

        img = pipe(
            prompt=prompt, 
            generator=generator, 
            num_inference_steps=steps, 
            width=width,
            height=height,
            guidance_scale=guidance_scale,
        ).images[0]
        
        img.save(f"{dest}/{id}_{seed}.png")