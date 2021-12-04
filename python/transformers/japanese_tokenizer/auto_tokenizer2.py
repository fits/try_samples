from transformers import AutoTokenizer
import sys

models = {
    '1': 'rinna/japanese-roberta-base',
    '2': 'rinna/japanese-gpt2-xsmall',
    '3': 'sonoisa/t5-base-japanese'
}

model = models[sys.argv[1]]

tokenizer = AutoTokenizer.from_pretrained(model)

input_ids = tokenizer(sys.argv[2]).input_ids
print(input_ids)

tokens = tokenizer.convert_ids_to_tokens(input_ids)
print(tokens)
