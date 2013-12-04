library(MCMCpack)

mc <- read.csv('data3a.csv')

plot(mc$x, mc$y, col = c("red", "blue")[mc$f])

func <- function(beta, x, y) {
  p <- exp(beta[1] + beta[2] * x)
  sum(log(dpois(y, p)))
}

mc.res <- MCMCmetrop1R(func, theta.init = c(0, 0), x = mc$x, y = mc$y, burnin = 1000, logfun = TRUE)

summary(mc.res)

xx <- seq(min(mc$x), max(mc$x), length = 10000)
lines(xx, exp(mean(mc.res[,1]) + mean(mc.res[,2]) * xx), col="green")
