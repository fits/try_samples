d <- read.csv('data7.csv')

library(MCMCpack)

yv <- NULL
xv <- NULL

for (i in 1:length(d$id)) {
  yv <- c(yv, rep(0, d$N[i] - d$y[i]), rep(1, d$y[i]))
  xv <- c(xv, rep(d$x[i], d$N[i]))
}
# Y を 0, 1 の値をとるようにデータ変換
d.data <- data.frame(cbind(X = xv, Y = yv))

d.res <- MCMChlogit(Y ~ X, random=~1, group="X", r=1, R=1, data = d.data)