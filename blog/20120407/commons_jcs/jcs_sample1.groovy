@Grapes([
	@Grab('org.apache.jcs:jcs:1.3'),
	@GrabExclude('logkit#logkit'),
	@GrabExclude('avalon-framework#avalon-framework')
])
import org.apache.jcs.JCS

//一定時間で無効になるキャッシュ
def jcs = JCS.getInstance('sample1')

jcs.put('data1', 'cached_data')

Thread.sleep(2000)

println "2秒後： ${jcs.get('data1')}"

Thread.sleep(2000)

println "さらに2秒後： ${jcs.get('data1')}"
