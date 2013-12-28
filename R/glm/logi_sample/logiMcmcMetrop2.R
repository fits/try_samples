library(MCMCpack)

d <- read.csv('data4a.csv')

func <- function(beta, data) {
  eta <- beta[1] + beta[2] * data$x + beta[3] * as.numeric(data$f)
  p <- 1.0 / (1.0 + exp(-eta))
  sum(log(dbinom(data$y, data$N, p)))
}

d.res <- MCMCmetrop1R(func, theta.init = c(0, 0, 0), data = d, burnin = 1000, logfun = TRUE)

summary(d.res)