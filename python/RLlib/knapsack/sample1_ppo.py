import gym
from gym.spaces import Discrete, Box

import ray
from ray.rllib.agents.ppo import PPOTrainer
from ray.tune.logger import pretty_print

import numpy as np

N = 30
MAX_COUNT = 20
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

def next_state(items, state, action):
    idx = action // 2
    act = action % 2
    
    if idx < len(items):
        state[idx] += (1 if act == 1 else -1)
    
    return state

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
        self.max_count = config["max_count"]
        self.burst_reward = config["burst_reward"]
        
        h = self.max_count

        self.action_space = Discrete(len(self.items) * 2 + 1)
        self.observation_space = Box(low = -h, high = h, shape = (len(self.items), ))

        self.reset()

    def reset(self):
        self.count = 0
        self.state = [0 for _ in self.items]
        
        return self.state

    def step(self, action):
        self.state = next_state(self.items, self.state, action)
        
        reward, _ = calc_reward(
            self.items, self.state, 
            self.max_weight, self.burst_reward
        )
        
        self.count += 1
        done = self.count >= self.max_count
        
        return self.state, reward, done, {}

config = {
    "env": Knapsack, 
    "env_config": {
        "items": items, 
        "max_count": MAX_COUNT, 
        "max_weight": MAX_WEIGHT, 
        "burst_reward": BURST_REWARD
    }
}

ray.init()

trainer = PPOTrainer(config = config)

for _ in range(N):
    r = trainer.train()
    print(pretty_print(r))

s = [0 for _ in range(len(items))]

for _ in range(MAX_COUNT):
    a = trainer.compute_action(s)
    s = next_state(items, s, a)

    r, w = calc_reward(items, s, MAX_WEIGHT, BURST_REWARD)

    print(f"{a}, {s} : {r}, {w}")

ray.shutdown()