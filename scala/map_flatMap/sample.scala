val res = (1 until 10).map(x => x * 2)
res.foreach(println)

println("------------")

val res2 = (1 to 10).flatMap(x => x match {
	case d if d % 2 == 0 => "gu" + d
	case _ => List("ki")
})

res2.foreach(println)
