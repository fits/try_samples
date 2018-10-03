
case class Data(id: String, value: Int)

val d = Data("item1", 10)

println(d)

println(Data.tupled(("item2", 1)))

println(Data.unapply(d))
