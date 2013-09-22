
def doc = new XmlSlurper().parse(new File(args[0]))

doc.STATISTICAL_DATA.DATA_INF.VALUE.each {
	println "${it.@tab},${it.@cat01},${it.@cat02},${it.@cat03},${it.@time},${it.text()}"
}
