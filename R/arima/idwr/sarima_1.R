library(dplyr)
library(forecast)

df <- read.csv('idwr.csv') %>% 
  dplyr::group_by(year, week) %>% 
  dplyr::summarise(Data = sum(インフルエンザ))

df.ts <- ts(df$Data, frequency = 52)

df.arima <- arima(
  df.ts, 
  order = c(2, 0, 0), 
  seasonal = list(order = c(1, 1, 0), period = 52)
)

df.pred <- forecast(df.arima, h = 50)

print(df.arima$arma)

plot(df.ts, xlim = c(1, 6), ylim = c(0, max(df$Data)), type = 'l')

lines(fitted(df.arima), col = 'red')
lines(df.pred$mean, col = 'green')

#dev.off()