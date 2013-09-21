library(XML)

doc <- xmlParse("data.xml")
items <- getNodeSet(doc, "//item")

d <- sapply(items, function(x) list(
  no = xmlGetAttr(x, "no"),
  category = xmlGetAttr(x, "category"),
  value = strtoi(xmlValue(x))
))

write.csv(t(d), file = "data2.csv", row.names = FALSE)