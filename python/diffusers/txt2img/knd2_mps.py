from diffusers import KandinskyV22PriorPipeline, KandinskyV22Pipeline
import torch
import sys

destfile = sys.argv[1]
prompt = sys.argv[2]

local = True
gpu = 'mps'

ng_prompt = 'low quality, bad quality'

pr_pipe = KandinskyV22PriorPipeline.from_pretrained('kandinsky-community/kandinsky-2-2-prior', local_files_only=local).to(gpu)
pipe = KandinskyV22Pipeline.from_pretrained("kandinsky-community/kandinsky-2-2-decoder", local_files_only=local).to(gpu)

eimg, ng_eimg = pr_pipe(prompt, ng_prompt, guidance_scale=1.0).to_tuple()

img = pipe(image_embeds=eimg, negative_image_embeds=ng_eimg).images[0]

img.save(destfile)
