
import sys
import numpy as np

import gym
from gym.spaces import Discrete, Box

import ray
from ray.rllib.agents.ppo import PPOTrainer

import collections

N = int(sys.argv[1])
EPISODE_STEPS = int(sys.argv[2])
STATE_TYPE = sys.argv[3]
BONUS_TYPE = sys.argv[4]

items = [
    [120, 10],
    [130, 12],
    [80, 7],
    [100, 9],
    [250, 21],
    [185, 16]
]

state_types = {
    "a": (-10, 10),
    "b": (0, 5),
    "c": (0, 3)
}

bonus_types = {
    "0": [],
    "1": [(750, 100), (760, 100), (765, 100)],
    "2": [(750, 100), (760, 200), (765, 400)],
    "3": [(750, 200), (760, 400), (765, 800)]
}

vf_clip_params = {
    "0": 800,
    "1": 1100,
    "2": 1500,
    "3": 2200
}

def next_state(items, state, action, state_range):
    for i in range(len(action)):
        v = state[i] + round(action[i])
        state[i] = min(state_range[1], max(state_range[0], v))

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
        self.episode_steps = config["episode_steps"]
        self.burst_reward = config["burst_reward"]
        self.state_range = config["state_range"]
        self.special_bonus = config["special_bonus"]
        
        self.action_space = Box(low = -1, high = 1, shape = (len(self.items), ))
        
        self.observation_space = Box(
            low = self.state_range[0], 
            high = self.state_range[1], 
            shape = (len(self.items), )
        )

        self.reset()

    def reset(self):
        self.n_steps = 0
        self.state = [0 for _ in self.items]
        
        return self.state

    def step(self, action):
        self.state = next_state(self.items, self.state, action, self.state_range)
        
        r, _ = calc_reward(self.items, self.state, self.max_weight, self.burst_reward)
        reward = r

        for (v, b) in self.special_bonus:
            if r > v:
                reward += b
        
        self.n_steps += 1
        done = self.n_steps >= self.episode_steps
        
        return self.state, reward, done, {}

config = {
    "env": Knapsack, 
    "vf_clip_param": vf_clip_params[BONUS_TYPE],
    "env_config": {
        "items": items, "max_weight": 65, "burst_reward": -100, 
        "episode_steps": EPISODE_STEPS, 
        "state_range": state_types[STATE_TYPE], 
        "special_bonus": bonus_types[BONUS_TYPE]
    }
}

ray.init()

trainer = PPOTrainer(config = config)

for _ in range(N):
    r = trainer.train()
    print(f'iter = {r["training_iteration"]}')

print(f'N = {N}, EPISODE_STEPS = {EPISODE_STEPS}, state_type = {STATE_TYPE}, bonus_type = {BONUS_TYPE}')

rs = []

for _ in range(1000):
    s = [0 for _ in range(len(items))]
    r_tmp = config["env_config"]["burst_reward"]

    for _ in range(config["env_config"]["episode_steps"]):
        a = trainer.compute_action(s)
        s = next_state(items, s, a, config["env_config"]["state_range"])

        r, w = calc_reward(
            items, s, 
            config["env_config"]["max_weight"], config["env_config"]["burst_reward"]
        )
        
        r_tmp = max(r, r_tmp)

    rs.append(r_tmp)

print( collections.Counter(rs) )

ray.shutdown()
