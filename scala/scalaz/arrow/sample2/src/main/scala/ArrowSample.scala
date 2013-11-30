package sample

import scalaz._
import Scalaz._

object ArrowSample extends App {

	case class Product(val name: String) {}
	case class Type(val name: String) {}
	case class Result(val message: String) {}

	val product: String => Product = Product(_)

	val convert: Type => Product => Product = t => p => Product(s"${t.name}/${p.name}")

	val save: Type => Product => Result = t => p => Result(s"${t.name}:${p.name}")

	val f = product >>> (convert(Type("a")) &&& convert(Type("b"))) >>> save(Type("x")).first >>> save(Type("y")).second

	// (Result(x:a/id1), Result(y:b/id1))
	println("result : " + f("id1"))
}
