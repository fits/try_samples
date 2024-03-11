from diffusers import AutoencoderKL
from torchvision.utils import save_image

import torch
import sys

vae_dir = sys.argv[1]
inputfile = sys.argv[2]
destfile = sys.argv[3]

input = torch.load(inputfile)
input = torch.unsqueeze(input, 0)

vae = AutoencoderKL.from_pretrained(vae_dir, local_files_only=True)

imgs = vae.decode(input / vae.config.scaling_factor, return_dict=False)[0]

imgs = ((imgs + 1) * 0.5).clamp(0, 1)

save_image(imgs, destfile)
