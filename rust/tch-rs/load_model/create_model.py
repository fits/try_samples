import torch
from torch.nn import Module

class SampleModule(Module):
    def __init__(self):
        super().__init__()

        self.l1 = torch.nn.Linear(4, 6)
        self.l2 = torch.nn.Linear(6, 3)
        self.relu = torch.nn.ReLU()
        self.softmax = torch.nn.Softmax()

    def forward(self, x):
        x = self.l1(x)
        x = self.relu(x)
        x = self.l2(x)
        return self.softmax(x)

module = torch.jit.script(SampleModule())
module.save('model.pt')
