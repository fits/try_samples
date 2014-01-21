d <- read.csv('data7.csv')

library(MCMCglmm)

d.res <- MCMCglmm(cbind(y, N - y) ~ x, data = d, family = "multinomial2", verbose = FALSE)

summary(d.res)

summary(sqrt(d.res$VCV))

sd <- sqrt(posterior.mode(d.res$VCV))