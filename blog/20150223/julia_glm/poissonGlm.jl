
using DataFrames, GLM

d = readtable("data3a.csv")

res = glm(y ~ x, d, Poisson(), LogLink())

println(res)
