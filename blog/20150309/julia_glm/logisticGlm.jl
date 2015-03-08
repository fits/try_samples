
using DataFrames, GLM

d = readtable("data4a.csv")

d[:yn] = map(x -> d[:y][x] / d[:N][x], 1:nrow(d))
d[:ff] = pool(d[:f])

res = glm(yn~x + ff, d, Binomial())

println(res)
