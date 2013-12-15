d <- read.csv('data3a.csv')

# 説明変数を全て投入したモデル
d.all <- glm(y ~ ., data = d, family = poisson)

library(MASS)
# AIC によるモデル選択
d.res <- stepAIC(d.all)

summary(d.res)


png("poissonGlm.png")

plot(d$x, d$y, col = c("red", "blue")[d$f])

xx <- seq(min(d$x), max(d$x), length = 1000)

# y ~ x のモデルを使った平均種子数の予測値の曲線を描画
lines(xx, exp(d.res$coefficients["(Intercept)"] + d.res$coefficients["x"] * xx), col="green")

# 以下でも可
#lines(xx, predict(d.res, newdata = data.frame(x = xx), type = "response"), col = "green")

dev.off()
