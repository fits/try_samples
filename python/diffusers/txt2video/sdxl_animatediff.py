from diffusers import AnimateDiffSDXLPipeline, DDIMScheduler
from diffusers.models import MotionAdapter
from diffusers.utils import export_to_gif, export_to_video

import torch

import yaml
import sys

device = 'cuda'

with open(sys.argv[1]) as f:
    cfg = yaml.safe_load(f)

model_id = cfg['model']
steps = cfg['steps']
seed = cfg['seed']
width = cfg['width']
height = cfg['height']
output_file = cfg['output']
guidance_scale = cfg['guidance_scale']
negative_prompt = cfg['negative_prompt']
prompt = cfg['prompt']
num_frames = cfg['num_frames']
fps = cfg['fps']

def load_model(local_only, m_adapter, scheduler):
    return AnimateDiffSDXLPipeline.from_pretrained(
        model_id, 
        motion_adapter=m_adapter,
        scheduler=scheduler,
        torch_dtype=torch.bfloat16, 
        use_safetensors=True,
        local_files_only=local_only
    )

def load_motion_model(local_only):
    return MotionAdapter.from_pretrained(
        'guoyww/animatediff-motion-adapter-sdxl-beta', 
        torch_dtype=torch.bfloat16, 
        use_safetensors=True,
        local_files_only=local_only
    )

def load_scheduler(local_only):
    return DDIMScheduler.from_pretrained(
        model_id,
        subfolder='scheduler',
        timestep_spacing='linspace',
        beta_schedule='linear',
        local_files_only=local_only
    )

try:
    scheduler = load_scheduler(True)
except EnvironmentError:
    scheduler = load_scheduler(False)

try:
    adapter = load_motion_model(True)
except EnvironmentError:
    adapter = load_motion_model(False)

try:
    pipe = load_model(True, adapter, scheduler)
except EnvironmentError:
    pipe = load_model(False, adapter, scheduler)

pipe = pipe.to(device)

pipe.vae.enable_slicing()
pipe.vae.enable_tiling()
pipe.enable_sequential_cpu_offload()

frames = pipe(
    prompt, 
    negative_prompt=negative_prompt,
    num_inference_steps=steps, 
    width=width,
    height=height,
    guidance_scale=guidance_scale,
    num_frames=num_frames,
    generator=torch.Generator(device).manual_seed(seed)
).frames[0]

if output_file.endswith('.gif'):
    export_to_gif(frames, output_file, fps)
else:
    export_to_video(frames, output_file, fps)
