import scala.xml.{Node, XML}

var num = 0

def check(node: Node): Unit = node match {
	case <data>{d @ _*}</data> => num = num + 1
	case _ => node.child.foreach(it => check(it))
}

check(XML.loadFile(args(0)))

println("instance num : " + num)
