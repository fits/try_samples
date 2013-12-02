d <- read.csv('data3a.csv')

summary(d)

plot(d$x, d$y, col = c("red", "blue")[d$f])

d.all <- glm(y ~ ., data = d, family = poisson)

library(MASS)
d.res <- stepAIC(d.all)

summary(d.res)

xx <- seq(min(d$x), max(d$x), length = 1000)
#lines(xx, exp(d.res$coefficients["(Intercept)"] + d.res$coefficients["x"] * xx), col="green")
lines(xx, predict(d.res, newdata = data.frame(x = xx), type = "response"), col = "green")

