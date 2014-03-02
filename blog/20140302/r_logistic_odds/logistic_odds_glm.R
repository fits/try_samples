d <- data.frame(Titanic)

d.data <- data.frame(
  Class = rep(d$Class, d$Freq),
  Sex = rep(d$Sex, d$Freq),
  Age = rep(d$Age, d$Freq),
  Survived = rep(d$Survived, d$Freq)
)

d.res <- glm(Survived~., data = d.data, family = binomial)
summary(d.res)

# オッズ比
exp(d.res$coefficients)
