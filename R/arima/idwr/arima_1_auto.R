library(dplyr)
library(forecast)

df <- read.csv('idwr.csv') %>% 
  dplyr::group_by(year, week) %>% 
  dplyr::summarise(Data = sum(インフルエンザ))

df.ts <- ts(df$Data, frequency = 52)

df.arima <- auto.arima(df.ts)

df.pred <- forecast(df.arima, h = 50)

print(df.arima$arma)

plot(df.ts, xlim = c(1, 6), ylim = c(0, max(df$Data)), type = 'l')

lines(df.arima$fitted, col = 'red')
lines(df.pred$mean, col = 'green')

#dev.off()