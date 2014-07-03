library(tree)

excCols = c(
  "Matches.Played",
  "Clearances.completion.rate",
  "Passes.Completed.1",
  "Crosses.Completed.1",
  "Group.Stage.Result",
  "Goals.for",
  "Goals.Conceded",
  "Penalty.goal",
  "Own.goals.For",
  "Open.Play.Goals",
  "Set.Piece.Goals"
)

wd.all <- read.delim("data/teams_result.txt", row.names = 1)
wd.data <- wd.all[, !(colnames(wd.all) %in% excCols)]

wd.tree <- tree(Total.Goals.scored ~ ., data = wd.data, split = "gini")

plot(wd.tree)
text(wd.tree)