data <- read.csv('data.csv')

cor(data$x, data$y)

plot(data$x, data$y)

res <- lm(y ~ x, data = data)
summary(res)
