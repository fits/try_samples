
using DataFrames, GLM, Gadfly

d = readtable("data3a.csv")

res = glm(y ~ x, d, Poisson(), LogLink())

xx = [minimum(d[:x]):0.1:maximum(d[:x])]
nd = DataFrame(n = [1 for i = 1:length(xx)], x = xx)

yy = predict(res, nd)

p = plot(
	layer(d, x = "x", y = "y", color = "f", Geom.point),
	layer(DataFrame(x = xx, y = yy), x = "x", y = "y", Geom.line)
)

draw(PNG("poissonGlm_draw2.png", 500px, 400px), p)
