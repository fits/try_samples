import com.sun.jdi.Bootstrap

def pid = args[0]

def blobPrint = { blob -> 
	//blob.print()
	try {
		println "name = ${blob.name}, frame size = ${blob.frameSize}, code size = ${blob.codeSize}, size = ${blob.size}, content size = ${blob.contentSize}, header size = ${blob.headerSize}"
	} catch(ex) {
		//println ex
	}
}

def manager = Bootstrap.virtualMachineManager()

def connector = manager.attachingConnectors().find {
	it.name() == 'sun.jvm.hotspot.jdi.SAPIDAttachingConnector'
}

def params = connector.defaultArguments()
params.get('pid').setValue(pid)

def vm = connector.attach(params)

try {
	def codeCache = vm.saVM().codeCache

	def heap = codeCache.heap
	def ptr = heap.begin()
	def end = heap.end()

	def blobList = []

	while (ptr && ptr.lessThan(end)) {
		try {
			def blob = codeCache.findBlobUnsafe(heap.findStart(ptr))

			if (blob != null && 
				(blobList.isEmpty() || blob != blobList.last()) ) {

				blobList << blob
			}

			ptr = heap.nextBlock(ptr)
		} catch (ex) {
			ex.printStackTrace()
		}
	}

	blobList.each(blobPrint)

	println ''

	println "*** codeblob total size = ${blobList*.size.sum()}"

	def memory = heap.memory

	println "*** committed size = ${memory.committedSize()}, max size = ${memory.reservedSize()}"

} finally {
	vm.dispose()
}