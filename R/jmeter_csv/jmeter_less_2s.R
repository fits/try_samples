args <- commandArgs(TRUE)

files <- list.files(args[1], pattern = ".log", full.names = T, recursive = T)
dataList <- lapply(files, function(a) read.csv(a, fileEncoding="UTF-8"))

data <- Reduce(function(a, b) rbind(a, b), dataList)

ds <- density(data$elapsed)

fn <- approxfun(ds$x, ds$y)

integrate(fn, 0, 2000)

length(data[data$elapsed <= 2000,]$elapsed) / length(data$elapsed)
