
var list = List.empty[String]

def sample(i: Int) = {
//	println(i)
	list = list :+ i.toString
}

(1 to 1000).par.foreach { sample }

println(s"list : ${list.size}")


val list2 = scala.collection.mutable.ListBuffer.empty[String]

def sample2(i: Int) = {
	list2 += i.toString
}

(1 to 1000).par.foreach { sample2 }

println(s"list2 : ${list2.size}")

// Java Concurrent
val list3 = new java.util.concurrent.CopyOnWriteArrayList[String]()

def sample3(i: Int) = {
	list3.add(i.toString)
}

(1 to 1000).par.foreach { sample3 }

println(s"list3 : ${list3.size()}")

// STM
import scala.concurrent.stm._

val list4 = Ref(List.empty[String])

def sample4(i: Int) = {
	list4.single.transform( _ :+ i.toString )
}

(1 to 1000).par.foreach { sample4 }

atomic { implicit txn =>
	println(s"list4 : ${list4.get.size}")
}

