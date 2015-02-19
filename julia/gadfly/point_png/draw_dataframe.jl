
using DataFrames, Gadfly

d = readtable("data3a.csv")

p = plot(d, x = "x", y = "y", color = "f", Geom.point)

draw(PNG("data.png", 6inch, 6inch), p)
