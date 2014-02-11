d <- read.csv('data7.csv')

library(MCMCglmm)

d.res <- MCMCglmm(cbind(y, N - y) ~ x, data = d, family = "multinomial2", verbose = FALSE)

summary(d.res)


# 生存確率の算出
calcProb <- function(x, b, r)
	1.0 / (1.0 + exp(-1 * (b[1] + b[2] * x + r)))

png("logiMcmcglmm_1.png")

plot(d$x, d$y)

xx <- seq(min(d$x), max(d$x), length = 100)

beta <- posterior.mode(d.res$Sol)
sd <- sqrt(posterior.mode(d.res$VCV))

lines(xx, max(d$N) * calcProb(xx, beta, 0), col="green")
lines(xx, max(d$N) * calcProb(xx, beta, -1 * sd), col="blue")
lines(xx, max(d$N) * calcProb(xx, beta, sd), col="blue")

dev.off()

# x=4 のyの分布
png("logiMcmcglmm_2.png")

yy <- 0:max(d$N)

plot(yy, table(d[d$x == 4,]$y), xlab="y", ylab="num")

# 葉数 x を固定した場合の生存種子数 y の確率分布を算出
calcL <- function(ylist, xfix, n, b, s)
  sapply(ylist, function(y) integrate(
      f = function(r) dbinom(y, n, calcProb(xfix, b, r)) * dnorm(r, 0, s),
      lower = s * -10,
      upper = s * 10
    )$value
  )

lines(yy, calcL(yy, 4, max(d$N), beta, sd) * length(d[d$x == 4,]$y), col="red", type="b")

dev.off()