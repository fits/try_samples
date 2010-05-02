
abstract class Node

case class NullNode() extends Node
case class TestNode(name: String) extends Node

def eval(n: Node) = n match {
	case NullNode() => println("null node")
	case TestNode(na) => println("test node: " + na)
}

eval(NullNode())
eval(TestNode("test"))

println(TestNode("aaaa").name)
