library(tree)

excCols = c(
  "Matches.Played",
  "Goals.for",
  "Total.Goals.scored",
  "Goals.Conceded",
  "Penalty.goal",
  "Own.goals.For",
  "Open.Play.Goals",
  "Set.Piece.Goals",
  "Clearances.completion.rate",
  "Passes.Completed.1",
  "Crosses.Completed.1",
  "Assists"
)

wd.all <- read.delim("data/teams_result.txt", row.names = 1)
wd.data <- wd.all[, !(colnames(wd.all) %in% excCols)]

wd.tree <- tree(Group.Stage.Result ~ ., data = wd.data)

plot(wd.tree)
text(wd.tree)