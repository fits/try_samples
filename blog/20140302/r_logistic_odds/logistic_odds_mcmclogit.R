library(MCMCpack)

d <- data.frame(Titanic)

d.data <- data.frame(
  Class = rep(d$Class, d$Freq),
  Sex = rep(d$Sex, d$Freq),
  Age = rep(d$Age, d$Freq),
  Survived = rep(as.numeric(d$Survived) - 1, d$Freq)
)

d.res <- MCMClogit(Survived~., data = d.data)

d.summ <- summary(d.res)

d.summ

#オッズ比
exp(d.summ$statistics[, "Mean"])
