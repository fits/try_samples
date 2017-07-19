import numpy as np
import pymc as pm

d = np.loadtxt('data3a.csv', delimiter = ',', skiprows = 1, 
		dtype = {'names': ('y', 'x', 'f'), 'formats': ('i4', 'f4', 'S1')})

beta1 = pm.Normal('beta_1', 0, 0.01, value = 0)
beta2 = pm.Normal('beta_2', 0, 0.01, value = 0)

@pm.deterministic
def lambda_(d = d, beta1 = beta1, beta2 = beta2):
	return np.exp(beta1 + beta2 * d['x'])

y = pm.Poisson('y', lambda_, value = d['y'], observed = True)

model = pm.Model([y, beta1, beta2, d])

mcmc = pm.MCMC(model)

mcmc.sample(40000, 10000)

resBeta1 = mcmc.trace('beta_1')[:]
resBeta2 = mcmc.trace('beta_2')[:]

print('beta1 mean=%f, std=%f' % (np.mean(resBeta1), np.std(resBeta1)))
print('beta2 mean=%f, std=%f' % (np.mean(resBeta2), np.std(resBeta2)))
