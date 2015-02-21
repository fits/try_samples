
using DataFrames, GLM, Gadfly

d = readtable("data3a.csv")

res = glm(y~x, d, Poisson(), LogLink())

println(res)

xx = [minimum(d[:x]):0.1:maximum(d[:x])]
yy = exp(coef(res)[1] + coef(res)[2] * xx)

p = plot(
	layer(d, x = "x", y = "y", color = "f", Geom.point),
	layer(DataFrame(x = xx, y = yy), x = "x", y = "y", Geom.line)
)

draw(PNG("poissonGlm.png", 6inch, 6inch), p)
