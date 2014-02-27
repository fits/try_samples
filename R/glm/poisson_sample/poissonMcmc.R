library(MCMCpack)

md <- read.csv('data3a.csv')

plot(md$x, md$y, col = c("red", "blue")[md$f])

md.res <- MCMCpoisson(y ~ x, data = md)

summary(md.res)

xx <- seq(min(md$x), max(md$x), length = 10000)
lines(xx, exp(quantile(md.res[,1], 0.25) + quantile(md.res[,2], 0.25) * xx), col="gray")
lines(xx, exp(mean(md.res[,1]) + mean(md.res[,2]) * xx), col="green")
lines(xx, exp(quantile(md.res[,1], 0.75) + quantile(md.res[,2], 0.75) * xx), col="gray")
