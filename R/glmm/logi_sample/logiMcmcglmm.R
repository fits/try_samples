d <- read.csv('data7.csv')

library(MCMCglmm)

d.res <- MCMCglmm(cbind(y, N - y) ~ x, random = ~id, data = d, family = "multinomial2", verbose = FALSE)

summary(d.res)