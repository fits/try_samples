d <- read.csv('data7.csv')

library(bayesm)

count <- 1000
lgtdata <- NULL

for (i in 1:length(d$id)) {
  yv <- c(rep(0, d$N[i] - d$y[i]), rep(1, d$y[i]))
  xv <- matrix(rep(d$x[i], d$N[i]), nrow = d$N[i], ncol = 1)
  lgtdata[[i]] <- list(y=yv, X=xv)
}

d.res <- rhierBinLogit(Data = list(lgtdata = lgtdata),Mcmc = list(R = count))
