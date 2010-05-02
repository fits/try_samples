import scala.xml.{Node, Elem}

def check(node: Node): Unit = node match {
	case <data>{ data }</data> => println("1: '" + data + "'")
	case <data>{ data @ _* }</data> => println("2: " + data)
	case _ => println("3: no match")
}

def check2(node: Node): Unit = node match {
	case Elem(_, "data", _, _, data) => println("1: '" + data + "'")
	case Elem(_, "data", _, _, data @ _ *) => println("2: " + data)
	case _ => println("3: no match")
}

println("--- check ---")

check(<data>test1</data>)
check(<data>test2<abc /> <name><no>first</no></name></data>)
check(<data><a href="">http://</a></data>)
check(<data />)
check(<data></data>)
check(<data> </data>)
check(<data>
</data>)
check(<data name="test">123</data>)
check(<dataList>123</dataList>)

println
println("--- check2 ---")

check2(<data>test1</data>)
check2(<data>test2<abc /> <name><no>first</no></name></data>)
check2(<data><a href="">http://</a></data>)
check2(<data />)
check2(<data></data>)
check2(<data> </data>)
check2(<data>
</data>)
check2(<data name="test">123</data>)
check2(<dataList>123</dataList>)

