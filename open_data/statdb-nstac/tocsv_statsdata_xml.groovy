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


def factory = XMLInputFactory.newInstance()
def xr = factory.createXMLStreamReader(new File(args[0]).newReader("UTF-8"))

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

