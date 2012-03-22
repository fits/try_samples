@Grapes([
	@Grab('org.apache.jcs:jcs:1.3'),
	@GrabExclude('logkit#logkit'),
	@GrabExclude('avalon-framework#avalon-framework')
])
import org.apache.jcs.JCS

def jcs = JCS.getInstance("sample")

Thread.sleep(5000)

jcs.put("data1", "abc")

Thread.sleep(2000)

println jcs.get("data1")

Thread.sleep(7000)
jcs.put("data2", 123)

println jcs.get("data1")

Thread.sleep(2000)

println jcs.get("data1")
println jcs.get("data2")

Thread.sleep(8000)

println jcs.get("data2")
