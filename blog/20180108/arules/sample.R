library(arules)

args <- commandArgs(TRUE)

tr <- read.transactions(args[1], format = "basket", sep = ",")

tr.ap <- apriori(tr, parameter = list(support = 0.05, confidence = 0.7))

inspect(sort(tr.ap, by = "lift"))
