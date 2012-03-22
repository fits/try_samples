@Grapes([
	@Grab('org.apache.jcs:jcs:1.3'),
	@GrabExclude('logkit#logkit'),
	@GrabExclude('avalon-framework#avalon-framework')
])
import org.apache.jcs.JCS

def check = {cache, key, expected, info ->
	def res = cache.get(key) == expected
	println "$info - get('$key') == $expected : $res"
}

//有効期限（MaxLifeSeconds）のあるキャッシュ
def jcs = JCS.getInstance("sample1")

Thread.sleep(5000)

jcs.put("life1", "abc")

Thread.sleep(2000)

check jcs, "life1", "abc", "2秒後"

Thread.sleep(7000)

jcs.put("life2", 123)

check jcs, "life1", "abc", "9秒後"

Thread.sleep(2000)

//有効期限切れ
check jcs, "life1", null, "11秒後"

check jcs, "life2", 123, "2秒後"

Thread.sleep(8000)

//有効期限切れ
check jcs, "life2", null, "10秒後"
