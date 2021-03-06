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

/* 以下のような group では Groovy の groupBy のような処理を実現できない
 * group 処理では隣接する type が等しい場合は連結されるが
 * 別の type を挟んだ場合は別ものとして設定される
 *
 * <<1, 2>, <3>, <4>, <5>, <6>> のような結果となる
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
