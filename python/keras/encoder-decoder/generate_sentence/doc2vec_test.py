
import sys
import numpy as np
from gensim.models.doc2vec import Doc2Vec, TaggedLineDocument
from janome.tokenizer import Tokenizer

tokenizer = Tokenizer()

model_file = sys.argv[1]
data_file = sys.argv[2]
gen_size = int(sys.argv[3])

questions = [q.split(' ') for q in sys.argv[4].split(';')]

steps = 50
base_word_prob = 0.7
prob_weight = 1.5
keyword_rate = 2
replace_targets = ['名詞', '形容詞']

model = Doc2Vec.load(model_file)

docs_list = list(TaggedLineDocument(data_file))

docs_list_str = [''.join(d.words) for d in docs_list]

is_replace_target = lambda t: np.any([
    t.part_of_speech.startswith(trg) for trg in replace_targets
])

def random_choice(cd):
    probs = np.exp(np.array([p for _, p in cd]) * prob_weight)
    probs /= probs.sum()

    return np.random.choice([d for d, _ in cd], p = probs)

adjust_prob = lambda c, q: (c[0], c[1] * keyword_rate) if c[0] in q else c

def answer(q):
    v = model.infer_vector(q, steps = steps)
    cd = model.docvecs.most_similar([v])

    d = random_choice(cd)

    words, _ = docs_list[d]

    res = []

    for w in words:
        if w in model.wv:
            t = tokenizer.tokenize(w)[0]

            if is_replace_target(t):
                wc = [ adjust_prob(c, q) for c in model.wv.most_similar(w) 
                       if tokenizer.tokenize(c[0])[0].part_of_speech == t.part_of_speech
                ]

                w = random_choice(wc + [(w, base_word_prob)])

        res += w

    s = ''.join(res)

    return (s, model.docvecs.similarity_unseen_docs(model, q, res), s in docs_list_str)


for q in questions:
    ans = [ answer(q) for _ in range(gen_size)]

    for a in sorted(ans, key = lambda x: x[1], reverse = True):
        print(f'{q} = {a}')


