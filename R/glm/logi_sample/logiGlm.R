d <- read.csv('data4a.csv')

d.res <- glm(cbind(y, N - y) ~ x + f, data = d, family = binomial)

summary(d.res)

plot(d$x, d$y, col = c("red", "blue")[d$f])

xx <- seq(min(d$x), max(d$x), length = 50)
#ft <- factor("T", levels = c("C", "T"))
#fc <- factor("C", levels = c("C", "T"))
ft <- factor("T", levels = levels(d$f))
fc <- factor("C", levels = levels(d$f))

nd.t <- predict(d.res, data.frame(x = xx, f = ft), type="response")
nd.c <- predict(d.res, data.frame(x = xx, f = fc), type="response")

# plot に合わせるため predict の結果に N の値を乗算
lines(xx, nd.t * max(d$N), col = "green")
lines(xx, nd.c * max(d$N), col = "yellow")