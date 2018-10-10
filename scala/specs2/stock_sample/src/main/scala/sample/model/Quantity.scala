package sample.model

import scala.util.{Failure, Success, Try}

sealed abstract case class Quantity private (amount: Amount = 0)

object Quantity {
  def apply(amount: Amount): Try[Quantity] =
    if (amount >= 0)
      Success(new Quantity(amount){})
    else
      Failure(new IllegalArgumentException(s"amount($amount) < 0"))
}
