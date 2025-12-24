import torch
import sys
from diffusers import ShapEPipeline
from diffusers.utils import export_to_obj

steps = 64
frame_size = 256
guidance_scale = 15.0

prompt = sys.argv[1]

pipe = ShapEPipeline.from_pretrained(
    'openai/shap-e', 
    torch_dtype=torch.float16, 
)

pipe = pipe.to('cuda')

images = pipe(
    prompt, 
    guidance_scale=guidance_scale, 
    num_inference_steps=steps, 
    frame_size=frame_size,
    output_type='mesh',
).images

export_to_obj(images[0], 'output.obj')
