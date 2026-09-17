from PIL import Image
from transformers import ViTImageProcessorPil, ViTForImageClassification
import sys

model_id = 'google/vit-base-patch16-224'
img_file = sys.argv[1]

def load_processor(local_only):
    return ViTImageProcessorPil.from_pretrained(model_id, local_files_only=local_only)

def load_model(local_only):
    return ViTForImageClassification.from_pretrained(model_id, local_files_only=local_only)

try:
    img_proc = load_processor(True)
except EnvironmentError:
    img_proc = load_processor(False)

try:
    model = load_model(True)
except EnvironmentError:
    model = load_model(False)

img = Image.open(img_file).convert('RGB')

inputs = img_proc(images=img, return_tensors='pt')

print(inputs)
print(inputs['pixel_values'].shape)

output = model(**inputs)

print(output)

cls_id = output.logits.argmax(dim=-1)

res = model.config.id2label[cls_id.item()]

print(res)

top_n = output.logits.topk(k=5)

print(top_n.values)
print([model.config.id2label[x] for x in top_n.indices.flatten().tolist()])
