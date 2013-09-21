library(XML)

doc <- xmlParse("data.xml")
items <- getNodeSet(doc, "//VALUE")

tab <- sapply(items, function(x) xmlGetAttr(x, "tab"))
cat01 <- sapply(items, function(x) xmlGetAttr(x, "cat01"))
cat02 <- sapply(items, function(x) xmlGetAttr(x, "cat02"))
cat03 <- sapply(items, function(x) xmlGetAttr(x, "cat03"))
time <- sapply(items, function(x) xmlGetAttr(x, "time"))
value <- sapply(items, function(x) strtoi(xmlValue(x)))

df <- data.frame(tab, cat01, cat02, cat03, time, value, stringsAsFactors = FALSE)

write.csv(df, file = "data.csv", row.names = FALSE)