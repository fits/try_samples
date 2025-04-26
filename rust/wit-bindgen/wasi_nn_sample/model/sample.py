import torch
import torch.nn as nn
import torch.nn.functional as F
import torch.onnx

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

net.eval()

dummy_input = torch.zeros((1, 2))

torch.onnx.export(net, dummy_input, 'sample.onnx', input_names=['input'], output_names=['output'])
