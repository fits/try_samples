import com.sun.jdi.Bootstrap

import sun.jvm.hotspot.oops.*
import sun.jvm.hotspot.debugger.*
import sun.jvm.hotspot.memory.GenCollectedHeap
import sun.jvm.hotspot.memory.ConcurrentMarkSweepGeneration
import sun.jvm.hotspot.memory.CompactibleFreeListSpace

def pid = args[0]

def manager = Bootstrap.virtualMachineManager()

def connector = manager.attachingConnectors().find {
	it.name() == 'sun.jvm.hotspot.jdi.SAPIDAttachingConnector'
}

def params = connector.defaultArguments()
params.get('pid').setValue(pid)

def vm = connector.attach(params)

try {
	def objHeap = vm.saVM().objectHeap
	//def objHeap = vm.saObjectHeap()

	def colHeap = vm.saVM().universe.heap()
	def cmsSpaceOld = null

	if (colHeap instanceof GenCollectedHeap) {
		def genOld = colHeap.getGen(1)

		if (genOld instanceof ConcurrentMarkSweepGeneration) {
			cmsSpaceOld = genOld.cmsSpace()
		}
	}

	def objectSize = { handle, obj ->
		long res = 0

		if (obj) {
			res = obj.objectSize
		}
		else if (cmsSpaceOld && cmsSpaceOld.contains(handle)) {
			res = cmsSpaceOld.collector().blockSizeUsingPrintezisBits(handle)
		}

		if (res > 0 && cmsSpaceOld && cmsSpaceOld.contains(handle)) {
			res = CompactibleFreeListSpace.adjustObjectSizeInBytes(res)
		}

		res
	}

	def oopList = []

	def liveRegions = objHeap.collectLiveRegions()

	for (i = 0; i < liveRegions.size(); i += 2) {
		def bottom = liveRegions.get(i)
		def top = liveRegions.get(i + 1)

		def handle = bottom.addOffsetToAsOopHandle(0)

		try {
			while(handle.lessThan(top)) {
				def oop = objHeap.newOop(handle)

				if (oop) {
					oopList << oop
				}

				def objSize = objectSize(handle, oop)

				if (objSize <= 0) {
					break
				}

				handle = handle.addOffsetToAsOopHandle(objSize)
			}
		} catch (AddressException e) {
		}
	}

	oopList.each { 
		println "class: ${it.klass.name.asString()}, address: ${it.mark.address}, age: ${it.mark.age()}"
	}

} finally {
	vm.dispose()
}