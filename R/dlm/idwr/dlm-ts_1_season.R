library(dplyr)
library(dlm)

n = 50

df <- read.csv('idwr.csv') %>% 
  dplyr::group_by(year, week) %>% 
  dplyr::summarise(Data = sum(インフルエンザ))

df.ts <- ts(df$Data, frequency = 52, 
            start = c(df$year[1], df$week[1]))

build <- function(x) {
  dlmModPoly(dV = exp(x[1]), dW = c(0, 0)) +
    dlmModSeas(52, dV = 0, dW = rep(0, 51))
}

#df.param <- dlmMLE(
#  df.ts,
#  dlmMLE(df.ts, c(1), build)$par,
#  build,
#  method = 'BFGS'
#)
#print(df.param)
#df.model <- build(df.param$par)

df.model <- build(c(1))

totalComp <- function(d) {
  Reduce(
    function(a, b) { a + d[, b] },
    which(df.model$F == 1),
    rep(0, length(d[, 1]))
  )
}

df.filter <- dlmFilter(df.ts, df.model)
df.smooth <- dlmSmooth(df.filter)

df.pred <- dlmForecast(df.filter, n)

plot(df.ts, type = 'l', 
     xlim = c(start(df.ts)[1], end(df.pred$f)[1] + 1))

lines(dropFirst(totalComp(df.filter$m)), col = 'green')
lines(dropFirst(totalComp(df.smooth$s)), col = 'blue')

lines(df.pred$f, col = 'orange')

df.resi <- residuals(df.filter, sd = F)

qqnorm(df.resi)
qqline(df.resi)

acf(df.resi)

shapiro.test(df.resi)
Box.test(df.resi)