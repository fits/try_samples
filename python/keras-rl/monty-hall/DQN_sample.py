
import sys
import numpy as np

from numpy.random import randint

from rl.core import Env
from rl.agents.dqn import DQNAgent
from rl.policy import BoltzmannQPolicy
from rl.memory import SequentialMemory

from keras.models import Sequential
from keras.layers import Dense
from keras.optimizers import Adam

steps = int(sys.argv[1])
n_hidden = int(sys.argv[2])

memory_limit = 1000
window_length = 1

def select_answer():
    return randint(3)

rest_of = lambda a, b: [c for c in range(3) if c not in [a, b]][0]

class MontyHall(Env):
    def __init__(self):
        self.answer = None
        self.stage = 0

    def step(self, action):
        if self.stage:
            redraw = 1 if action == self.answer else -1
            return -1, redraw, True, {}
        else:
            self.stage = 1
            return rest_of(action, self.answer), 0, False, {}

    def reset(self):
        self.answer = select_answer()
        self.stage = 0

        return -1

    def render(self, mode = 'human', close = False):
        print(f'*** render: answer = {self.answer}, close = {close}')

    def close(self):
        pass


env = MontyHall()

model = Sequential()

model.add(Dense(n_hidden, activation = 'relu', input_shape = (window_length,)))
model.add(Dense(3, activation = 'linear'))

model.summary()

memory = SequentialMemory(memory_limit, window_length = window_length)
policy = BoltzmannQPolicy()

dqn = DQNAgent(model, nb_actions = 3, policy = policy, memory = memory)

dqn.compile(Adam())

dqn.fit(env, nb_steps = steps)

tst_hist = dqn.test(env, nb_episodes = 1000, visualize = False)

rs = tst_hist.history['episode_reward']

acc = sum(r == 1 for r in rs) / len(rs)

print(f'accuracy = {acc}')
