d <- read.csv('data4a.csv')

plot(d$x, d$y, col = c("red", "blue")[d$f])

d.res <- glm(cbind(y, N - y) ~ x + f, data = d, family = binomial)

summary(d.res)

xx <- seq(min(d$x), max(d$x), length = 50)
ft <- factor("T", levels = c("C", "T"))
fc <- factor("C", levels = c("C", "T"))

nd.t <- predict(d.res, data.frame(x = xx, f = ft), type="response")
nd.c <- predict(d.res, data.frame(x = xx, f = fc), type="response")

# plotの結果に合わせるため predict の結果に N の値を乗算
lines(xx, nd.t * mean(d$N), col = "green")
lines(xx, nd.c * mean(d$N), col = "yellow")