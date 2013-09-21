library(XML)

doc <- xmlParse("data.xml")
items <- getNodeSet(doc, "//VALUE")

d <- sapply(items, function(x) list(
	tab = xmlGetAttr(x, "tab"),
	cat01 = xmlGetAttr(x, "cat01"),
	cat02 = xmlGetAttr(x, "cat02"),
	cat03 = xmlGetAttr(x, "cat03"),
	time = xmlGetAttr(x, "time"),
	value = xmlValue(x)
))

write.csv(t(d), file = "data2.csv", row.names = FALSE)