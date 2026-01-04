from diffusers import AutoModel, WanPipeline
from diffusers.hooks import apply_group_offloading
from diffusers.utils import export_to_video
from transformers import UMT5EncoderModel

import torch
import yaml
import sys

device = 'cuda'
local=True

with open(sys.argv[1]) as f:
    cfg = yaml.safe_load(f)

model_id = cfg['model']
steps = cfg['steps']
seed = cfg['seed']
output_file = cfg['output']
guidance_scale = cfg['guidance_scale']
negative_prompt = cfg['negative_prompt']
prompt = cfg['prompt']
num_frames = cfg['num_frames']
fps = cfg['fps']

text_enc = UMT5EncoderModel.from_pretrained(
    model_id, 
    subfolder='text_encoder', 
    torch_dtype=torch.bfloat16, 
    use_safetensors=True, 
    local_files_only=local
)

vae = AutoModel.from_pretrained(
    model_id, 
    subfolder='vae', 
    torch_dtype=torch.bfloat16, 
    use_safetensors=True, 
    local_files_only=local
)

trans = AutoModel.from_pretrained(
    model_id, 
    subfolder='transformer', 
    torch_dtype=torch.bfloat16, 
    use_safetensors=True, 
    local_files_only=local
)

pipe = WanPipeline.from_pretrained(
    model_id,
    vae=vae,
    transformer=trans,
    text_encoder=text_enc,
    torch_dtype=torch.bfloat16,
    use_safetensors=True,
    local_files_only=local
)

pipe = pipe.to(device)

onload_device = torch.device(device)
offload_device = torch.device('cpu')

pipe.transformer.enable_layerwise_casting(
    storage_dtype=torch.float8_e4m3fn,
    compute_dtype=torch.bfloat16
)

pipe.transformer.enable_group_offload(
    onload_device=onload_device,
    offload_device=offload_device,
    offload_type='leaf_level',
    use_stream=True,
    non_blocking=True
)

pipe.vae.enable_group_offload(
    onload_device=onload_device,
    offload_device=offload_device,
    offload_type='leaf_level',
    use_stream=True,
    non_blocking=True
)

apply_group_offloading(
    pipe.text_encoder, 
    onload_device=onload_device, 
    offload_type='leaf_level',
    use_stream=True,
    non_blocking=True
)

frames = pipe(
    prompt=prompt,
    negative_prompt=negative_prompt,
    num_frames=num_frames,
    guidance_scale=guidance_scale,
    num_inference_steps=steps,
    generator=torch.Generator(device).manual_seed(seed)
).frames[0]

export_to_video(frames, output_file, fps=fps)
