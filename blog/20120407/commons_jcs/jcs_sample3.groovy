@Grapes([
	@Grab('org.apache.jcs:jcs:1.3'),
	@GrabExclude('logkit#logkit'),
	@GrabExclude('avalon-framework#avalon-framework')
])
import org.apache.jcs.JCS

def jcs = JCS.getInstance('sample3')

jcs.put("data1", "cached_data1")

Thread.sleep(3000)

println "3ïbå„ÅF data1 = ${jcs.get('data1')}"

Thread.sleep(3000)

println "Ç≥ÇÁÇ…3ïbå„ÅF data1 = ${jcs.get('data1')}"

Thread.sleep(3500)

println "Ç≥ÇÁÇ…3.5ïbå„ÅF data1 = ${jcs.get('data1')}"
