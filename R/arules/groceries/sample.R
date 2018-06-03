library(arules)
data(Groceries)

rules <- apriori(Groceries, parameter = 
                list(supp = 0.001, conf = 0.3))

inspect(head(sort(rules, by = "lift"), 10))
