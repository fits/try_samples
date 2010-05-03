
def startRev = args[0]
def endRev = "HEAD"

def diffCount = {fileName ->
	def proc = Runtime.getRuntime().exec("svn diff -r${startRev}:${endRev} ${fileName}")

	def count = 0

	proc.in.eachLine {
		count++
	}

	return count
}

println "dir: ${args[1]}"

new File(args[1]).eachFileRecurse {f ->

	if (f.name =~ /\.(cpp|h|java|as|css|html|js|txt|xml|mxml)$/) {
		def count = diffCount(f.absolutePath)
		println "${f.name} : ${count}"
	}
}
