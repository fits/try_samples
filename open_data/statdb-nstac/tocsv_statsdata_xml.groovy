@Grab('commons-cli:commons-cli:1.2')
import javax.xml.stream.*
import groovy.transform.Immutable

import org.apache.commons.cli.Options
import org.apache.commons.cli.PosixParser
import org.apache.commons.cli.HelpFormatter

import static org.apache.commons.cli.OptionBuilder.*

/**
 * 次世代統計利用システム API で取得した統計データを
 * CSV 化するスクリプト
 *
 */

def params = [
	category: 'cat01',
	subCategory: 'cat02',
	time: 'time',
	value: 'cat03',
	defaultValue: 0
]

@Immutable class StHeader {
	String category
	String subCategory
	String time
}

def opt = new Options()

opt.addOption('c', 'category', true, 'category attribute name')
opt.addOption('s', 'subCategory', true, 'subCategory attribute name')
opt.addOption('v', 'value', true, 'value attribute name')
opt.addOption('d', 'defaultValue', true, 'default value')
opt.addOption('h', 'help', false, 'help')

def cmdLine = new PosixParser().parse(opt, args)

if (cmdLine.hasOption('h')) {
	new HelpFormatter().printHelp('tocsv_statsdata_xml', opt, true)
	return
}

params.keySet().each { k ->
	if (cmdLine.hasOption(k)) {
		params[k] = cmdLine.getOptionValue(k)
	}
}


def factory = XMLInputFactory.newInstance()
def xr = factory.createXMLStreamReader(new File(cmdLine.args[0]).newReader("UTF-8"))

def dataMap = [:] as LinkedHashMap
def valueTypes = [] as TreeSet

def procValueNode = { stream ->
	if (stream.name.localPart == 'VALUE') {
		def header = new StHeader(
			category: stream.getAttributeValue(null, params.category),
			subCategory: stream.getAttributeValue(null, params.subCategory),
			time: stream.getAttributeValue(null, params.time)
		)

		def valueMap = dataMap.get(header)

		if (valueMap == null) {
			valueMap = [:]
			dataMap.put(header, valueMap)
		}

		def vtype = stream.getAttributeValue(null, params.value)

		valueMap.put(vtype, stream.elementText)
		valueTypes << vtype
	}
}

while(xr.hasNext()) {
	switch (xr.eventType) {
		case XMLStreamConstants.START_ELEMENT:
			procValueNode(xr)
			break
	}
	xr.next()
}

xr.close()

print 'category,subCategory,time,'
println valueTypes.join(',')

dataMap.each {k, v ->
	def items = valueTypes.inject([k.category, k.subCategory, k.time]) { acc, val ->
		def item = v.get(val)
		acc << ( (item == null)? params.defaultValue: item )
		acc
	}

	println items.join(',')
}
