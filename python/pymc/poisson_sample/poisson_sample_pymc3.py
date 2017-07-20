import numpy as np
import pymc3 as pm

d = np.loadtxt('data3a.csv', delimiter = ',', skiprows = 1, 
		dtype = {'names': ('y', 'x', 'f'), 'formats': ('i4', 'f4', 'S1')})

with pm.Model() as model:

	beta1 = pm.Normal('beta_1', mu = 0, sd = 10)
	beta2 = pm.Normal('beta_2', mu = 0, sd = 10)

	theta = beta1 + beta2 * d['x']

	y = pm.Poisson('y', mu = np.exp(theta), observed = d['y'])

with model:
	step = pm.Metropolis()
	start = pm.find_MAP()

	trace = pm.sample(2000, step, start = start)

pm.summary(trace[:])

print(trace)
