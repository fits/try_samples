d <- read.csv('data7.csv')

library(MCMCglmm)

d.res <- MCMCglmm(cbind(y, N - y) ~ x, data = d, family = "multinomial2", verbose = FALSE)

summary(d.res)

beta <- posterior.mode(d.res$Sol)
sd <- sqrt(posterior.mode(d.res$VCV))

sd

par(mfrow = c(1, 2))

plot(d$x, d$y)

xx <- seq(min(d$x), max(d$x), length = 1000)
lines(xx, max(d$N) * 1.0 / (1.0 + exp(-1 * (beta[1] + beta[2] * xx))), col="green")

# x=4 のyの分布
plot(0:8, table(d[d$x == 4,]$y))

d.dbinom <- function(xl)
  sapply(xl, function(x) integrate(
    f = function(r, x)
      dbinom(x, 8, 1.0 / (1.0 + exp(-1 * (beta[1] + beta[2] * 4 + r)))) * dnorm(r, 0, sd)
    ,
    lower = sd * -10,
    upper = sd * 10,
    x = x
  )$value
)

lines(0:8, d.dbinom(0:8) * length(d[d$x == 4,]$y), col="red", type="b")