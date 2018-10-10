package sample.model

import org.specs2.SpecificationWithJUnit

class QuantitySpec extends SpecificationWithJUnit {
  def is =
    s2"""
      Quantity
        amount = 0 is success    $e1
        amount > 0 is success    $e2
        amount < 0 is fail       $e3
      """

  def e1 = Quantity(0) must beSuccessfulTry[Quantity]
  def e2 = Quantity(1) must beSuccessfulTry[Quantity]
  def e3 = Quantity(-1) must beFailedTry[Quantity]
}
