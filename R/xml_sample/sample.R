library(XML)

doc <- xmlInternalTreeParse("data.xml")
items <- getNodeSet(doc, "//item")

no <- sapply(items, function(x) xmlGetAttr(x, "no"))
category <- sapply(items, function(x) xmlGetAttr(x, "category"))
value <- sapply(items, function(x) strtoi(xmlValue(x)))

d <- data.frame(no, category, value)

write.csv(d, file = "data.csv", row.names = FALSE)