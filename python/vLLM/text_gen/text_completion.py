from vllm import LLM, SamplingParams
import sys

model_id = 'Qwen/Qwen2.5-3B-Instruct'
prompt = sys.argv[1]

def main():
    params = SamplingParams(temperature=0.1, top_p=0.7, max_tokens=50)

    llm = LLM(model=model_id, gpu_memory_utilization=0.8)

    res = llm.enqueue(prompt, params)

    print(f"enqueue result = {res}")

    output = llm.wait_for_completion(use_tqdm=False)[0]

    print(output)
    print("output = " + output.outputs[0].text)

if __name__ == "__main__":
    main()
