import sys
import torch
from transformers import AutoModelForCausalLM, AutoTokenizer, pipeline

model_id = 'microsoft/Phi-1.5'
local = True

max_tokens = int(sys.argv[1])
prompt = sys.argv[2]

model = AutoModelForCausalLM.from_pretrained(model_id, local_files_only=local)
tokenizer = AutoTokenizer.from_pretrained(model_id, local_files_only=local)

pipe = pipeline(
    'text-generation',
    model=model,
    tokenizer=tokenizer,
)

output = pipe(prompt, max_new_tokens=max_tokens)

print(output[0]['generated_text'])
