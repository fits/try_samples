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

d.hs <- hist(d[d$x == 4,]$y, plot = FALSE)
plot(d.hs$counts)