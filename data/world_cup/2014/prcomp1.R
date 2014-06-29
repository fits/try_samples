
incRows = c(
  "Assists",
  "Attempts.on.target.from.outside.the.area",
  "Goals.for",
  "Total.Goals.scored",
  "Open.Play.Goals",
  "Fouls.causing.a.penalty",
  "Tackles.won",
  "Tackles.suffered",
  "Throw.ins"
)

wd.all <- read.delim("data/teams_result.txt", row.names = 1)
wd.data <- wd.all[, colnames(wd.all) %in% incRows]

wd.prcomp <- prcomp(wd.data, scale = T)

summary(wd.prcomp)
biplot(wd.prcomp)
