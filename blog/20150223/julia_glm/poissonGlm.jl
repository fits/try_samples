
using DataFrames, GLM, Gadfly

d = readtable("data3a.csv")

res = glm(y ~ x, d, Poisson(), LogLink())

println(res)
