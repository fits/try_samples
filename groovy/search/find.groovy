
def scriptName = System.getProperty("script.name")

if (args.length < 3) {
	println ">groovy ${scriptName} [dir name] [extension] [pattern]"
	return
}

new File(args[0]).eachFileRecurse({
	if (it.name =~ "\\.${args[1]}\$") {
		if (it.text =~ args[2]) {
			println it.path
		}
	}
})
