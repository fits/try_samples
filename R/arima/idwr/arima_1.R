library(dplyr)
library(forecast)

df <- read.csv('idwr.csv') %>% 
  dplyr::group_by(year, week) %>% 
  dplyr::summarise(Data = sum(インフルエンザ))

df.arima <- arima(df$Data, order = c(2, 1, 0))

df.pred <- forecast(df.arima, h = 50)

plot(df$Data, xlim = c(0, 250), type = 'l')

lines(fitted(df.arima), col = 'red')
lines(201:250, df.pred$mean, col = 'green')

#dev.off()
