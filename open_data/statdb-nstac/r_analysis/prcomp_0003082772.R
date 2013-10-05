# 家計消費状況調査の主成分分析サンプル

data.df <- read.csv("../data/statsdata_0003082772.csv")

data.dfsub <- subset(data.df, 
  category %in% seq(from = 20, to = 100, by = 10) &
#  category %in% seq(from = 110, to = 140, by = 10) &
  substr(time, 1, 4) == "2013"
)

data.pca <- prcomp(
  ~ X0060 + X0070 + X0080 +
    X0090 + X0100 + X0110 + X0710 + X0750,
  data = data.dfsub, scale = TRUE
)

biplot(data.pca)

