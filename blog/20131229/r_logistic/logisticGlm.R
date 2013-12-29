d <- read.csv('data4a.csv')

d.res <- glm(cbind(y, N - y) ~ x + f, data = d, family = binomial)

summary(d.res)

# グラフ描画
png("logisticGlm.png")

plot(d$x, d$y, col = c("red", "blue")[d$f])

xx <- seq(min(d$x), max(d$x), length = 1000)
ft <- factor("T", levels = levels(d$f))
fc <- factor("C", levels = levels(d$f))
# 下記でも可
#ft <- factor("T", levels = c("C", "T"))
#fc <- factor("C", levels = c("C", "T"))

qq.t <- predict(d.res, data.frame(x = xx, f = ft), type="response")
qq.c <- predict(d.res, data.frame(x = xx, f = fc), type="response")

# predict の結果に N の値を乗算
lines(xx, max(d$N) * qq.t, col = "green")
lines(xx, max(d$N) * qq.c, col = "yellow")

dev.off()
