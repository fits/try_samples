import gym
from gym.spaces import Discrete, Box

import ray
from ray.rllib.agents.ppo import PPOTrainer
from ray.tune.logger import pretty_print

import numpy as np

N = 50
MAX_WEIGHT = 65
BURST_REWARD = -100

items = [
    [120, 10],
    [130, 12],
    [80, 7],
    [100, 9],
    [250, 21],
    [185, 16]
]

def calc_reward(items, state, max_weight, burst_reward):
    reward = 0
    weight = 0
    
    for i in range(len(state)):
        reward += items[i][0] * state[i]
        weight += items[i][1] * state[i]
    
    if weight > max_weight or min(state) < 0:
        reward = burst_reward
    
    return reward, weight

class Knapsack(gym.Env):
    def __init__(self, config):
        self.items = config["items"]
        self.max_weight = config["max_weight"]
        self.burst_reward = config["burst_reward"]
        
        h = self.max_weight // min(np.array(self.items)[:, 1])

        self.action_space = Box(0, h, shape = (len(self.items), ))
        self.observation_space = Box(0, h, shape = (len(self.items), ))

    def reset(self):
        return [0 for _ in self.items]

    def step(self, action):
        state = [round(d) for d in action]
        
        reward, _ = calc_reward(
            self.items, state, 
            self.max_weight, self.burst_reward
        )
        
        return state, reward, True, {}

config = {
    "env": Knapsack, 
    "env_config": {
        "items": items, 
        "max_weight": MAX_WEIGHT, 
        "burst_reward": BURST_REWARD
    }
}

ray.init()

trainer = PPOTrainer(config = config)

for _ in range(N):
    r = trainer.train()
    print(pretty_print(r))

for _ in range(20):
    s = [0 for _ in range(len(items))]
    a = trainer.compute_action(s)

    s = [round(d) for d in a]

    r, w = calc_reward(items, s, MAX_WEIGHT, BURST_REWARD)

    print(f"{s} : {r}, {w}")

ray.shutdown()