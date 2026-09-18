from transformers import AutoProcessor, AutoModelForMultimodalLM
import sys

model_id = 'google/gemma-4-E2B-it'
prompt = sys.argv[1]

def load_model(local_only):
    return AutoModelForMultimodalLM.from_pretrained(
        model_id,
        device_map='auto',
        local_files_only=local_only
    )

def load_processor(local_only):
    return AutoProcessor.from_pretrained(model_id, local_files_only=local_only)

try:
    model = load_model(True)
except EnvironmentError:
    model = load_model(False)

try:
    processor = load_processor(True)
except EnvironmentError:
    processor = load_processor(False)

messages = [
    {
        'role': 'user',
        'content': prompt
    }
]

inputs = processor.apply_chat_template(
    messages,
    tokenize=True,
    return_dict=True,
    return_tensors='pt',
    add_generation_prompt=True,
).to(model.device)

print(inputs)
print(inputs['input_ids'].shape)

output = model.generate(**inputs, max_new_tokens=1024)
print(output)

res = processor.decode(output[0], skip_special_tokens=False)
print(res)

# input_len = inputs['input_ids'].shape[-1]
# print( processor.decode(output[0][input_len:], skip_special_tokens=False) )
