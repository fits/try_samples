library(XML)

doc <- xmlParse("data.xml")
items <- getNodeSet(doc, "//item")

d <- sapply(items, function(x) c(
  no = xmlGetAttr(x, "no"),
  category = xmlGetAttr(x, "category"),
  value = xmlValue(x)
))

df <- data.frame(t(d), stringsAsFactors = FALSE)

write.csv(df, file = "data3.csv", row.names = FALSE)