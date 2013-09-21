library(XML)

doc <- xmlParse("data.xml")
items <- getNodeSet(doc, "//item")

d <- sapply(items, function(x) list(
  xmlGetAttr(x, "no"),
  xmlGetAttr(x, "category"),
  strtoi(xmlValue(x))
))

no <- unlist(d[1,])
category <- unlist(d[2,])
value <- unlist(d[3,])

df <- data.frame(no, category, value, stringsAsFactors = FALSE)

write.csv(df, file = "data4.csv", row.names = FALSE)