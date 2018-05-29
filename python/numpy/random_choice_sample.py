import numpy as np

word_values = [('A', 2.7), ('B', 0.5), ('C', 1.1)]

words = [w for w, _ in word_values]

probs = np.array([v for _, v in word_values])
probs /= probs.sum()

print( probs.sum() )

for _ in range(10):
    print( np.random.choice(words, p = probs) )
