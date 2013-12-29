library(MCMCpack)

d <- read.csv('data4a.csv')

func <- function(beta, data) {
  z <- beta[1] + beta[2] * data$x + beta[3] * as.numeric(data$f)
  q <- 1.0 / (1.0 + exp(-z))
  sum(log(choose(data$N, data$y)) + data$y * log(q) + (data$N - data$y) * log(1 - q))
  
  # 下記でも可
  # sum(log(dbinom(data$y, data$N, q)))
}

d.res <- MCMCmetrop1R(func, theta.init = c(0, 0, 0), data = d, logfun = TRUE)

summary(d.res)

# グラフ描画
png("logisticMcmcMetrop.png")

plot(d$x, d$y, col = c("red", "blue")[d$f])

xx <- seq(min(d$x), max(d$x), length = 1000)
ft <- factor("T", levels = levels(d$f))
fc <- factor("C", levels = levels(d$f))

zt <- mean(d.res[,1]) + mean(d.res[,2]) * xx + mean(d.res[,3]) * as.numeric(ft)
zc <- mean(d.res[,1]) + mean(d.res[,2]) * xx + mean(d.res[,3]) * as.numeric(fc)

lines(xx, max(d$N) * 1.0 / (1.0 + exp(-zt)), col="green")
lines(xx, max(d$N) * 1.0 / (1.0 + exp(-zc)), col="yellow")

dev.off()
