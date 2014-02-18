
def baseUrl = 'https://images-na.ssl-images-amazon.com/images/G/01/rainier/help/xsd/release_1_9'

def destDir = 'xsd'
def baseXsd = (args.length > 0)? args[0]: 'amzn-envelope.xsd'

def getIncludes = { xml ->
	def doc = new XmlSlurper().parseText(xml)

	doc.include.collect {
		it.@schemaLocation
	}
}

def downloadXsd = { xsdList, doneList = [] ->
	def list = xsdList.flatten { xsd ->
		def result = []

		if (!doneList.contains(xsd)) {
			try {
				new URL("${baseUrl}/${xsd}").withInputStream { input ->
					def buf = input.bytes
					new File("${destDir}/${xsd}").bytes = buf

					doneList << xsd
					println "success download: ${xsd}"

					result = getIncludes(new String(buf, 'UTF-8'))
					result.removeAll(doneList)
				}
			} catch(e) {
				println "failed : ${xsd}, ${e}"
			}
		}
		result
	}

	if (!list.empty) {
		trampoline(list, doneList)
	}
}.trampoline()

downloadXsd([baseXsd])
