d <- read.csv('data7.csv')

summary(d)

d.hs <- hist(d[d$x == 4,]$y, plot = FALSE)
plot(d.hs$counts)

library(glmmML)

d.res <- glmmML(cbind(y, N - y) ~ x, data = d, family = binomial, cluster = id)

summary(d.res)
