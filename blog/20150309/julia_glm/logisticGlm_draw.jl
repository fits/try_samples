
using DataFrames, GLM, Gadfly

d = readtable("data4a.csv")

d[:yn] = map(x -> d[:y][x] / d[:N][x], 1:nrow(d))
d[:ff] = pool(d[:f])

res = glm(yn~x + ff, d, Binomial())

xx = [minimum(d[:x]):0.1:maximum(d[:x])]

rt = predict(res, DataFrame(n = rep([1], length(xx)), x = xx, ff = rep([1], length(xx))))
rc = predict(res, DataFrame(n = rep([1], length(xx)), x = xx, ff = rep([0], length(xx))))

p = plot(
	layer(d, x = "x", y = "y", color = "f", Geom.point),
	layer(x = xx, y = maximum(d[:N]) * rt, Geom.line, Theme(default_color = color("red"))),
	layer(x = xx, y = maximum(d[:N]) * rc, Geom.line, Theme(default_color = color("green")))
)

draw(PNG("logisticGlm_draw2.png", 500px, 400px), p)
