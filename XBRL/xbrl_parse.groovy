
def doc = new XmlSlurper().parse(new File(args[0]))

def values = doc.OperatingIncome.findAll {it.@contextRef == "CurrentYearConsolidatedDuration"}

values.each {
	println it
}
