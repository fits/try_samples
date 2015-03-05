
using DataFrames, GLM

d = readtable("data4a.csv")

d[:yn] = map(x -> d[:y][x] / d[:N][x], 1:nrow(d))

# categorise (convert to PooledDataArray)
#d[:ft] = convert(PooledDataArray, d[:f])
d[:ft] = pool(d[:f])

res = glm(yn~x + ft, d, Binomial())

println(res)
