library(dplyr)
library(dlm)

n = 100

df <- read.csv('idwr.csv') %>% 
  dplyr::group_by(year, week) %>% 
  dplyr::summarise(Data = sum(手足口病))

build <- function(x) {
  dlmModPoly(dV = x[1], dW = c(0, 0)) + 
    dlmModTrig(104)
}

df.model <- build(c(1))

totalComp <- function(d) {
  Reduce(
    function(a, b) { a + d[, b] },
    which(df.model$F == 1),
    rep(0, length(d[, 1]))
  )
}

df.filter <- dlmFilter(df$Data, df.model)
df.smooth <- dlmSmooth(df.filter)

df.filter_res <- totalComp(df.filter$m)
df.smooth_res <- totalComp(df.smooth$s)

matplot(cbind(df$Data, df.filter_res[-1], df.smooth_res[-1]), 
        type = 'l', lty = 1, xlim = c(1, 200 + n))

df.pred <- dlmForecast(df.filter, n)

lines(seq(201, 200 + n), df.pred$f, col = 'orange')
