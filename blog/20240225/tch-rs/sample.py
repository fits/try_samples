import torch
import torch.nn as nn
import torch.nn.functional as F
from safetensors.torch import save_file

class Net(nn.Module):
    def __init__(self):
        super(Net, self).__init__()

        self.fc1 = nn.Linear(2, 3)
        self.fc2 = nn.Linear(3, 1)

    def forward(self, x):
        x = F.relu(self.fc1(x))
        return self.fc2(x)

net = Net()

net.fc1.weight = nn.Parameter(torch.tensor([[0.1, 0.2], [0.3, 0.4], [0.5, 0.6]]))
net.fc1.bias = nn.Parameter(torch.tensor([0.7, 0.8, 0.9]))

net.fc2.weight = nn.Parameter(torch.tensor([[0.4, 0.3, 0.2]]))
net.fc2.bias = nn.Parameter(torch.tensor([0.1]))

print(net.state_dict())

x = torch.tensor([[1.0, 2.0]])
y = net(x)

print(y)

save_file(net.state_dict(), 'sample.safetensors')
