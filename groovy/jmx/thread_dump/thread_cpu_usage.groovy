
import com.sun.tools.attach.VirtualMachine

import java.lang.management.ThreadMXBean
import java.lang.management.ManagementFactory

import javax.management.remote.JMXConnectorFactory
import javax.management.remote.JMXServiceURL

import groovy.transform.Canonical

@Canonical class Time {
	long start = -1
	long end = -1

	long diff() {
		(start < 0 || end < 0)? -1: end - start
	}
}

@Canonical class ThreadData {
	long id
	Closure cpuTimeGetter

	private Time processTime
	private Time cpuTime

	def start() {
		processTime = new Time( System.nanoTime() )
		cpuTime = new Time( cpuTimeGetter(id) )

		this
	}

	def stop() {
		cpuTime.end = cpuTimeGetter(id)
		processTime.end = System.nanoTime()

		this
	}

	def getCpuUsage() {
		if (processTime == null || cpuTime == null) {
			return -1
		}

		(cpuTime.diff() as double) / processTime.diff()
	}
}


def calcCpuUsage = { interval, tbean ->

	def thList = tbean.getAllThreadIds().collect {
		new ThreadData(it, { id ->
			tbean.getThreadCpuTime(id) 
		}).start()
	}

	sleep(interval)

	thList.collectEntries { [ it.id, it.stop() ] }
}

def dumpThread = { tbean, cpuUsageFunc ->
	def cpuMap = cpuUsageFunc(tbean)

	tbean.dumpAllThreads(false, false).each {
		def cpuUsage = cpuMap[it.threadId]?.cpuUsage?.trunc(3)

		println "\"${it.threadName}\" id=${it.threadId} cpu=${cpuUsage} ${it.threadState}"

		println "  blockedCount=${it.blockedCount} blockedTime=${it.blockedTime} waitedCount=${it.waitedCount} waitedTime=${it.waitedTime}"

		println "  lockName=${it.lockName} lockOwnerId=${it.lockOwnerId} lockOwnerName=${it.lockOwnerName}"

		it.stackTrace.each { println "      at ${it}" }

		println ''
	}
}

def vm = VirtualMachine.attach(args[0])

try {
	def jmxuri = vm.startLocalManagementAgent()

	JMXConnectorFactory.connect(new JMXServiceURL(jmxuri)).withCloseable {
		def server = it.getMBeanServerConnection()

		def bean = ManagementFactory.newPlatformMXBeanProxy(server, 'java.lang:type=Threading', ThreadMXBean)

		dumpThread(bean, calcCpuUsage.curry(1000))
	}

} finally {
	vm.detach()
}
