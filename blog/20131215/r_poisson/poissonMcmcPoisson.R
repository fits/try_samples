library(MCMCpack)

d <- read.csv('data3a.csv')

d.res <- MCMCpoisson(y ~ x, data = d)

summary(d.res)


png("poissonMcmcPoisson.png")

plot(d$x, d$y, col = c("red", "blue")[d$f])

xx <- seq(min(d$x), max(d$x), length = 10000)

lines(xx, exp(mean(d.res[,1]) + mean(d.res[,2]) * xx), col="green")

#lines(xx, exp(quantile(d.res[,1], 0.25) + quantile(d.res[,2], 0.25) * xx), col="gray")
#lines(xx, exp(quantile(d.res[,1], 0.75) + quantile(d.res[,2], 0.75) * xx), col="gray")

dev.off()