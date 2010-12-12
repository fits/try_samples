import scala.annotation.tailrec

@tailrec def total(num: Int, list: List[Int]): Int = {
	list match {
		case Nil => num
		case x :: xs => total(num + x, xs)
	}
}

println("result: " + total(0, (1 to 10).toList))
