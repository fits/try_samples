library(ggplot2)

excRows = c(
  "Matches.Played",
  "Clearances.completion.rate",
  "Passes.Completed.1",
  "Crosses.Completed.1"
)

wd.all <- read.delim("data/teams_result.txt", row.names = 1)
wd.data <- wd.all[, !(colnames(wd.all) %in% excRows)]

wd.kmeans <- kmeans(scale(wd.data), 5, nstart=20)
wd.kmeans

wd.prcomp <- prcomp(wd.data, scale = T)

wd.df <- data.frame(wd.prcomp$x)
wd.df$name <- rownames(wd.df)
wd.df$cluster <- as.factor(wd.kmeans$cluster)

dev.off()
ggplot(wd.df, aes(x=PC1, y=PC2, label=name, col=cluster)) + geom_text()