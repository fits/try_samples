library(rpart)
library(partykit)

excRows = c(
  "Matches.Played",
  "Clearances.completion.rate",
  "Passes.Completed.1",
  "Crosses.Completed.1"
)

wd.all <- read.delim("data/teams_result.txt", row.names = 1)
wd.data <- wd.all[, !(colnames(wd.all) %in% excRows)]

wd.rpart <- rpart(League.Result ~ ., data = wd.data)

plot(as.party(wd.rpart))
