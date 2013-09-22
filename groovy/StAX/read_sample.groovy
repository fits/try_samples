import javax.xml.stream.*

def factory = XMLInputFactory.newInstance()
def xr = factory.createXMLStreamReader(new File(args[0]).newReader("UTF-8"))

def procValueNode = { stream ->
	if (stream.name.localPart == 'VALUE') {
		def items = (0..<stream.attributeCount).collect {
			stream.getAttributeValue(it)
		}
		items << stream.elementText

		println items.join(',')
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
