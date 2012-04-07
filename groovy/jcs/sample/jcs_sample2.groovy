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

//ˆê’èŠúŠÔ“à‚ÉƒAƒNƒZƒX‚µ‚È‚¯‚ê‚ÎÁ‚¦‚éƒLƒƒƒbƒVƒ…
def jcs = JCS.getInstance("sample2")

jcs.put("shrinker1", "test")
jcs.put("shrinker2", "no")

Thread.sleep(2000)

check jcs, "shrinker1", "test", "2•bŒã"

Thread.sleep(3000)

check jcs, "shrinker1", "test", "3•bŒã"

Thread.sleep(2000)

check jcs, "shrinker1", "test", "2•bŒã"
check jcs, "shrinker2", null, "7•bŒã"

Thread.sleep(5000)

check jcs, "shrinker1", null, "5•bŒã"

