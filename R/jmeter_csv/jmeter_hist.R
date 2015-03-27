# 下記コマンドで実行
#
#  > r CMD BATCH --encoding=UTF-8 "--args <ログディレクトリ>" jmeter_hist.R

args <- commandArgs(TRUE)

# JMeter 結果ファイルの収集
files <- list.files(args[1], pattern = "\\.log$", full.names = T, recursive = T)
# JMeter 結果ファイル（csv）の読み込み
dataList <- lapply(files, function(a) read.csv(a, header=F, fileEncoding="UTF-8"))

# データを連結して、単一 DataFrame 化
data <- Reduce(function(a, b) rbind(a, b), dataList)

summary(data)

png("jmeter_hist.png")

# 処理時間のヒストグラムを描画
hist(data$V12, 50)

dev.off()
