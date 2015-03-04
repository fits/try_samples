
using DataFrames, GLM

d = readtable("data4a.csv")

d[:yn] = d[:y] / maximum(d[:N])
d[:ft] = map(x -> if x == "C" 0 else 1 end, d[:f])

res = glm(yn~x + ft, d, Binomial())

println(res)
