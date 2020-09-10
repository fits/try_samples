import gym
from gym.spaces import Discrete, Box

import ray
import ray.rllib.agents.ppo as ppo
from ray.tune.logger import pretty_print

import random

class Bandit(gym.Env):
    def __init__(self, config):
        self.probs = config["probs"]
        self.max_count = config["max_count"]
        self.count = 0
        
        self.action_space = Discrete(len(self.probs))
        self.observation_space = Discrete(self.max_count + 1)

    def reset(self):
        self.count = 0
        return self.count

    def step(self, action):
        prob = self.probs[action]
        
        reward = 1.0 if random.random() < prob else 0.0
        
        self.count += 1
        
        done = self.count >= self.max_count
        
        return self.count, reward, done, {}

ray.init()

config = {
    "env": Bandit, 
    "env_config": { "probs": [0.2, 0.5, 0.7], "max_count": 10 }
}

trainer = ppo.PPOTrainer(config = config)

def eval():
    for n in range(10):
        a = trainer.compute_action(n)
        print(f"{n}: {a}")

eval()

for i in range(15):
    r = trainer.train()
    print(pretty_print(r))

eval()

ray.shutdown()
