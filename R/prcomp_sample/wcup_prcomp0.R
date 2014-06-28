
excRows = c(
  "Matches.Played",
  "Clearances.completion.rate",
  "Passes.Completed.1",
  "Crosses.Completed.1"
)

wd.all <- read.delim("data/teams_result.txt", row.names = 1)
wd.data <- wd.all[, !(colnames(wd.all) %in% excRows)]

wd.prcomp <- prcomp(wd.data, scale = T)

summary(wd.prcomp)
biplot(wd.prcomp)
