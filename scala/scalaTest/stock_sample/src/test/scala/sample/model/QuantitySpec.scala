package sample.model

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FlatSpec, Matchers}

import scala.util.{Failure, Success}

@RunWith(classOf[JUnitRunner])
class QuantitySpec extends FlatSpec with Matchers {
  "Quantity" should "amount >= 0 is success" in {
    val q0 = Quantity(0)
    val q1 = Quantity(1)

    q0 shouldBe a [Success[_]]
    q1 shouldBe a [Success[_]]
  }

  it should "amount < 0 is failure" in {
    val q = Quantity(-1)

    q shouldBe a [Failure[_]]
  }
}
