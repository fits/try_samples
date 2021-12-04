from transformers import RobertaTokenizer
import sys

tokenizer = RobertaTokenizer.from_pretrained("roberta-base")

input_ids = tokenizer(sys.argv[1]).input_ids
print(input_ids)

tokens = tokenizer.convert_ids_to_tokens(input_ids)
print(tokens)
