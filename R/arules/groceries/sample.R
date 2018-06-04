library(arules)
data(Groceries)

params <- list(supp = 0.001, conf = 0.1)

rules <- apriori(Groceries, parameter = params)

inspect(head(sort(rules, by = "lift"), 10))
