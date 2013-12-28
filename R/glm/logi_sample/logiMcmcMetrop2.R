library(MCMCpack)

d <- read.csv('data4a.csv')

func <- function(beta, data) {
  eta <- beta[1] + beta[2] * data$x + beta[3] * as.numeric(data$f)
  p <- 1.0 / (1.0 + exp(-eta))
  sum(log(dbinom(data$y, data$N, p)))
}

d.res <- MCMCmetrop1R(func, theta.init = c(0, 0, 0), data = d, burnin = 1000, logfun = TRUE)

summary(d.res)

plot(d$x, d$y, col = c("red", "blue")[d$f])

xx <- seq(min(d$x), max(d$x), length = 50)
ft <- factor("T", levels = levels(d$f))
fc <- factor("C", levels = levels(d$f))

d.zt <- mean(d.res[,1]) + mean(d.res[,2]) * xx + mean(d.res[,3]) * as.numeric(ft)
d.zc <- mean(d.res[,1]) + mean(d.res[,2]) * xx + mean(d.res[,3]) * as.numeric(fc)

lines(xx, max(d$N) * 1.0 / (1.0 + exp(-d.zt)), col="green")
lines(xx, max(d$N) * 1.0 / (1.0 + exp(-d.zc)), col="yellow")
