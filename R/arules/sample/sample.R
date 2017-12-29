library(arules)

tr <- read.transactions("data/sample.basket", format = "basket", sep = ",")

#inspect(tr)
itemFrequencyPlot(tr, type = "absolute")
summary(tr)

tr.ap <- apriori(tr, parameter = list(supp = 0.05, conf = 0.6))

inspect(sort(tr.ap, by = "lift"))
