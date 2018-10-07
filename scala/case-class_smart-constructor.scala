
sealed case class Quantity1(amount: Int)

println(new Quantity1(1))
println(Quantity1(2))
println(Quantity1(3).copy(amount = 30))
println(Quantity1(4).copy(40))

sealed case class Quantity2 private (amount: Int)

//println(new Quantity2(1))
println(Quantity2(2))
println(Quantity2(3).copy(amount = 30))
println(Quantity2(4).copy(40))

sealed abstract case class Quantity3 private(amount: Int)

object Quantity3 {
    def apply(amount: Int): Quantity3 = new Quantity3(amount){}
}

//println(new Quantity3(1))
println(Quantity3(2))
//println(Quantity3(3).copy(amount = 30))
//println(Quantity3(4).copy(40))

println("----------------------")

import scala.util.{Failure, Success, Try}

sealed abstract case class Quantity private(amount: Int)

object Quantity {
    def apply(amount: Int): Try[Quantity] = 
        if (amount >= 0)
            Success(new Quantity(amount){})
        else
            Failure(new Error)
}

println(Quantity(5))
println(Quantity(0))
println(Quantity(-1))
