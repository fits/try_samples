package models.delivery

import org.scalatest.*
import flatspec.*
import matchers.*

import java.time.LocalDateTime

class DeliverySpec extends AnyFlatSpec with should.Matchers:
  import Delivery.*
  import CommandA.*

  def now: LocalDateTime = LocalDateTime.now()
  def nextDay(n: Long): LocalDateTime = now.plusDays(n)

  it should "transit to in-port when notReceived state receive" in {
    val (s, e) = Delivery.action(Receive("USNYC", now)).run(Delivery.createDelivery("t1"))

    s shouldBe a [Delivery.InPort]

    e should have size 1
    e.head shouldBe a[HandlingEvent.Received]
  }

  it should "not transit when other states receive" in {
    val states = List(
      Delivery.InPort("t1", "USNYC"),
      Delivery.OnBoardCarrier("t1", "0101S", "USNYC"),
      Delivery.Claimed("t1", "USNYC", now)
    )

    states.foreach { c =>
      val (s, e) = Delivery.action(Receive("USNYC", now)).run(c)

      s shouldBe c
      e shouldBe empty
    }
  }

  it should "transit to on-board-carrier when inPort state load" in {
    val (s, e) = Delivery.action(Load("V01", now)).run(Delivery.InPort("t1", "LOC01"))

    s shouldBe a [Delivery.OnBoardCarrier]

    e should have size 1
    e.head shouldBe a[HandlingEvent.Loaded]

    e.head match {
      case HandlingEvent.Loaded(t, v, l, _) =>
        t shouldBe "t1"
        v shouldBe "V01"
        l shouldBe "LOC01"
      case _ => assert(false)
    }
  }

  it should "not transit when inPort state load if voyageNo is blank" in {
    val (s, e) = Delivery.action(Load("   ", now)).run(Delivery.InPort("t1", "LOC01"))

    s shouldBe a [Delivery.InPort]

    e shouldBe empty
  }

  it should "not transit when other states load" in {
    val states = List(
      Delivery.NotReceived("t1"),
      Delivery.OnBoardCarrier("t1", "0101S", "USNYC"),
      Delivery.Claimed("t1", "USNYC", now)
    )

    states.foreach { c =>
      val (s, e) = Delivery.action(Load("V01", now)).run(c)

      s shouldBe c
      e shouldBe empty
    }
  }

  it should "transit to in-port when onBoardCarrier state unload" in {
    val (s, e) = Delivery.action(Unload("LOC02", now)).run(Delivery.OnBoardCarrier("t1", "V01", "LOC01"))

    s shouldBe a[Delivery.InPort]

    e should have size 1
    e.head shouldBe a[HandlingEvent.Unloaded]

    e.head match {
      case HandlingEvent.Unloaded(t, v, l, _) =>
        t shouldBe "t1"
        v shouldBe "V01"
        l shouldBe "LOC02"
      case _ => assert(false)
    }
  }

  it should "not transit when other states unload" in {
    val states = List(
      Delivery.NotReceived("t1"),
      Delivery.InPort("t1", "LOC01"),
      Delivery.Claimed("t1", "LOC01", now)
    )

    states.foreach { c =>
      val (s, e) = Delivery.action(Unload("LOC02", now)).run(c)

      s shouldBe c
      e shouldBe empty
    }
  }

  it should "transit to claimed when inPort state claim" in {
    val (s, e) = Delivery.action(Claim(now)).run(Delivery.InPort("t1", "LOC01"))

    s shouldBe a[Delivery.Claimed]

    e should have size 1
    e.head shouldBe a[HandlingEvent.Claimed]

    e.head match {
      case HandlingEvent.Claimed(t, l, _) =>
        t shouldBe "t1"
        l shouldBe "LOC01"
      case _ => assert(false)
    }
  }

  it should "not transit when other states claim" in {
    val states = List(
      Delivery.NotReceived("t1"),
      Delivery.OnBoardCarrier("t1", "0101S", "USNYC"),
      Delivery.Claimed("t1", "USNYC", now)
    )

    states.foreach { c =>
      val (s, e) = Delivery.action(Claim(now)).run(c)

      s shouldBe c
      e shouldBe empty
    }
  }
