import javax.xml.stream.*
import groovy.transform.Immutable

/**
 * 次世代統計利用システム API で取得した統計データを
 * CSV 化するスクリプト
 *
 */

// データ内容に合わせて下記を調整
def params = [
	category: 'cat01',
	type: 'cat02',
	time: 'time',
	value: 'cat03',
	valueTypes: (1..75).collect { String.format('%04d', it * 10) },
	defaultValue: 0
]

@Immutable class StHeader {
	String category
	String type
	String time
}


def factory = XMLInputFactory.newInstance()
def xr = factory.createXMLStreamReader(new File(args[0]).newReader("UTF-8"))

def dataMap = [:] as LinkedHashMap

def procValueNode = { stream ->
	if (stream.name.localPart == 'VALUE') {
		def header = new StHeader(
			category: stream.getAttributeValue(null, params.category),
			type: stream.getAttributeValue(null, params.type),
			time: stream.getAttributeValue(null, params.time)
		)

		def valueMap = dataMap.get(header)

		if (valueMap == null) {
			valueMap = [:]
			dataMap.put(header, valueMap)
		}

		valueMap.put(
			stream.getAttributeValue(null, params.value),
			stream.elementText
		)
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

print 'category,type,time,'
println params.valueTypes.join(',')

dataMap.each {k, v ->
	def items = params.valueTypes.inject([k.category, k.type, k.time]) { acc, val ->
		def item = v.get(val)
		acc << ( (item == null)? params.defaultValue: item )
		acc
	}

	println items.join(',')
}

