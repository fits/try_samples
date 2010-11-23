
if (args.length < 1) {
	println "groovy parse_tsv.groovy [tsv file]"
	return
}

new File(args[0]).eachLine {
	def items = it.split('\t')

	def len = items.length

	if (len == 1) {
		println items[0].trim()
	}
	else if (len > 1) {
		println items[1].trim()
	}
}

