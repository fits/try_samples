
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

a <- wd.prcomp$rotation
# PC1-PC4で絶対値が0.3より大きい変数を抽出
a[abs(a[,1]) > 0.3 | abs(a[,2]) > 0.3 | abs(a[,3]) > 0.3 | abs(a[,4]) > 0.3, 1:4]
