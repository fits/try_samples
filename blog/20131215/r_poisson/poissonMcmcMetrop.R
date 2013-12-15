library(MCMCpack)

d <- read.csv('data3a.csv')

# 尤度関数（対数尤度関数）
func <- function(beta, x, y) {
  lambda <- exp(beta[1] + beta[2] * x)
  sum(log(dpois(y, lambda)))
}

d.res <- MCMCmetrop1R(func, theta.init = c(0, 0), x = d$x, y = d$y, logfun = TRUE)

summary(d.res)


png("poissonMcmcMetrop.png")

plot(d$x, d$y, col = c("red", "blue")[d$f])

xx <- seq(min(d$x), max(d$x), length = 10000)

lines(xx, exp(mean(d.res[,1]) + mean(d.res[,2]) * xx), col="green")

#lines(xx, exp(quantile(d.res[,1], 0.25) + quantile(d.res[,2], 0.25) * xx), col="gray")
#lines(xx, exp(quantile(d.res[,1], 0.75) + quantile(d.res[,2], 0.75) * xx), col="gray")

dev.off()