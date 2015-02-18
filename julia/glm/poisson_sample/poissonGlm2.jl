
using DataFrames, GLM

d = readtable("data3a.csv")

d[:ft] = map(x -> if x == "C" 0 else 1 end, d[:f])

res = glm(y~x + ft, d, Poisson(), LogLink())

println(res)
