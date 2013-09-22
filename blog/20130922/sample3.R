library(XML)

f <- file("data3.csv", "w")

cat('"tab","cat01","cat02","cat03","time","value"\n', file = f, append = TRUE)

procValueNode <- function(name, attrs, .state) {
	if (name == "VALUE") {
		cat(attrs[1], attrs[2], attrs[3], attrs[4], attrs[5], "", file = f, sep = ",", append = TRUE)
		.state = TRUE
	}
	.state
}

procValueText <- function(content, .state) {
	if (.state) {
		cat(content, "\n", file = f, sep = "", append = TRUE)
	}
	FALSE
}

xmlEventParse("data.xml", handlers = list(
	startElement = procValueNode,
	text = procValueText
), state = FALSE)

close(f)
