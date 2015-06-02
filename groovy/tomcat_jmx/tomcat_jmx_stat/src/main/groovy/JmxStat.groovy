import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

import com.sun.tools.attach.VirtualMachine
import sun.management.ConnectorAddressLink

import javax.management.ObjectName
import javax.management.remote.JMXConnectorFactory
import javax.management.remote.JMXServiceURL

if (args.length < 2) {
    println '<pid> <conf file> [delay(ms)] [count]'
    return
}

def getServiceUrl = { id ->
    ConnectorAddressLink.importFrom(id as int)
}

def applyJmxAgent = { id ->
    def vm = VirtualMachine.attach(id)

    def javaHome = vm.systemProperties.getProperty('java.home')
    vm.loadAgent("${javaHome}/lib/management-agent.jar")

    vm.detach()
}

def connectJmx = { id ->
    def url = getServiceUrl(id)

    if (url == null) {
        applyJmxAgent(id)
        url = getServiceUrl(id)
    }

    JMXConnectorFactory.connect(new JMXServiceURL(url))
}

def printResult = {
    def json = new JsonBuilder()
    json(it)

    println json.toString()
}

def pid = args[0];
def conf = new JsonSlurper().parse(new File(args[1]))

def delay = (args.length < 3)? 0: args[2] as int
def count = (args.length < 4)? 1: args[3] as int

def con = connectJmx(pid)
def jmx = con.getMBeanServerConnection()

(1..count).each {
    conf.each { k, v ->
        def res = jmx.queryNames(new ObjectName(k), null).collectEntries { name ->
            [
                name, 
                v.collectEntries { attr -> 
                    [attr, jmx.getAttribute(name, attr)]
                }
            ]
        }

        printResult(res)
    }
    sleep delay
}

con.close()