@Grab('org.functionaljava:functionaljava:3.1')
import fj.*
import static fj.data.List.*

class Data {
	String id
	String type
	int value
}

def dataList = list(
	new Data(id: 1, type: 'A', value: 1),
	new Data(id: 2, type: 'A', value: 2),
	new Data(id: 3, type: 'B', value: 3),
	new Data(id: 4, type: 'A', value: 4),
	new Data(id: 5, type: 'C', value: 5),
	new Data(id: 6, type: 'B', value: 6)
)

/* ˆÈ‰º‚Ì‚æ‚¤‚È group ‚Å‚Í Groovy ‚Ì groupBy ‚Ì‚æ‚¤‚Èˆ—‚ğÀŒ»‚Å‚«‚È‚¢
 * group ˆ—‚Å‚Í—×Ú‚·‚é type ‚ª“™‚µ‚¢ê‡‚Í˜AŒ‹‚³‚ê‚é‚ª
 * •Ê‚Ì type ‚ğ‹²‚ñ‚¾ê‡‚Í•Ê‚à‚Ì‚Æ‚µ‚Äİ’è‚³‚ê‚é
 *
 * <<1, 2>, <3>, <4>, <5>, <6>> ‚Ì‚æ‚¤‚ÈŒ‹‰Ê‚Æ‚È‚é
 */
def res = dataList.group(Equal.equal(
	{a -> {b -> a.type == b.type} as F} as F
))

println res

res.foreach(
	{a ->
		println "------"
		a.foreach({b -> println b.id} as Effect)
	} as Effect
)
