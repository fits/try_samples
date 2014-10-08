args <- commandArgs(TRUE)

files <- list.files(args[1], pattern = ".log", full.names = T, recursive = T)
dataList <- lapply(files, function(a) read.csv(a, fileEncoding="UTF-8"))

data <- Reduce(function(a, b) rbind(a, b), dataList)

print("----- responseCode")
summary(as.factor(data$responseCode))

print("----- success")
summary(data$success)
