d <- read.csv('data7.csv')

summary(d)

library(glmmML)

d.res <- glmmML(cbind(y, N - y) ~ x, data = d, family = binomial, cluster = id)

summary(d.res)

par(mfrow = c(1, 2))

plot(d$x, d$y)

xx <- seq(min(d$x), max(d$x), length = 1000)
beta <- d.res$coefficients
lines(xx, max(d$N) * 1.0 / (1.0 + exp(-1 * (beta[1] + beta[2] * xx))), col="green")

# x=4 のyの分布
plot(0:8, table(d[d$x == 4,]$y))

d.fbinom <- function(r, x)
  dbinom(x, 8, 1.0 / (1.0 + exp(-1 * (beta[1] + beta[2] * 4 + r)))) * dnorm(r, 0, d.res$sigma)

d.dbinom <- function(xl)
  sapply(xl, function(x) integrate(
      f = d.fbinom,
      lower = d.res$sigma * -10,
      upper = d.res$sigma * 10,
      x = x
    )$value
  )

lines(0:8, d.dbinom(0:8) * length(d[d$x == 4,]$y), col="red", type="b")