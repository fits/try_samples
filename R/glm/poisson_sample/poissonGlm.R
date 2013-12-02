d <- read.csv('data3a.csv')

summary(d)

plot(d$x, d$y, col = c("red", "blue")[d$f])

d.all <- glm(y ~ ., data = d, family = poisson)

library(MASS)
stepAIC(d.all)