library(dplyr)
library(dlm)

n = 50

df <- read.csv('idwr.csv') %>% 
  dplyr::group_by(year, week) %>% 
  dplyr::summarise(Data = sum(インフルエンザ))

build <- function(x) {
  dlmModPoly(dV = exp(x[1]), dW = c(0, 0)) + 
    dlmModSeas(52, dV = 0, dW = rep(0, 51))
}

df.param <- dlmMLE(
  df$Data, 
  dlmMLE(df$Data, c(1), build)$par, 
  build,
  method = 'BFGS'
)

print(df.param)

df.model <- build(df.param$par)

totalComp <- function(d) {
  Reduce(
    function(a, b) { a + d[, b] },
    which(df.model$F == 1),
    rep(0, length(d[, 1]))
  )
}

df.filter <- dlmFilter(df$Data, df.model)
df.smooth <- dlmSmooth(df.filter)

df.filter_res = totalComp(df.filter$m)
df.smooth_res = totalComp(df.smooth$s)

matplot(cbind(df$Data, df.filter_res[-1], df.smooth_res[-1]), 
        type = 'l', lty = 1, xlim = c(1, 200 + n))

df.pred <- dlmForecast(df.filter, n)

lines(seq(201, 200 + n), df.pred$f, col = 'orange')

df.resi <- residuals(df.filter, sd = F)

qqnorm(df.resi)
qqline(df.resi)

acf(df.resi)

shapiro.test(df.resi)
Box.test(df.resi)