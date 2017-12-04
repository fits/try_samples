library(dplyr)
library(dlm)

n = 50

df <- read.csv('idwr.csv') %>% 
  dplyr::group_by(year, week) %>% 
  dplyr::summarise(Data = sum(インフルエンザ))

build <- function(x) {
  dlmModPoly(1, dV = exp(x[1]), dW = exp(x[2]))
}

df.param <- dlmMLE(df$Data, c(1, 1), build)

print(df.param)

df.model <- build(df.param$par)

df.filter <- dlmFilter(df$Data, df.model)
df.smooth <- dlmSmooth(df.filter)

matplot(cbind(df$Data, df.filter$m[-1], df.smooth$s[-1]), 
        type = 'l', lty = 1, xlim = c(1, 200 + n))

df.pred <- dlmForecast(df.filter, n)

lines(seq(201, 200 + n), df.pred$f, col = 'orange')

df.resi <- residuals(df.filter, sd = F)

qqnorm(df.resi)
qqline(df.resi)

acf(df.resi)

shapiro.test(df.resi)
Box.test(df.resi)