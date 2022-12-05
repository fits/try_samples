package models.delivery

import org.scalatest.*
import flatspec.*
import matchers.*

import java.time.LocalDateTime

class DeliverySpec extends AnyFlatSpec with should.Matchers:
  import models.*
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
    val f: FindRoute = t => {
      t shouldBe "t1"

      Some(Itinerary(List(
        Leg("V01", LocationTime("LOC01", nextDay(1)), LocationTime("LOC02", nextDay(2))),
        Leg("V02", LocationTime("LOC03", nextDay(3)), LocationTime("LOC04", nextDay(4))),
      )))
    }

    val (s, e) = Delivery.action(Load("V01", now, f)).run(Delivery.InPort("t1", "LOC01"))

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

  it should "not transit when inPort state load if itinerary is none" in {
    val f: FindRoute = _ => None

    val (s, e) = Delivery.action(Load("V01", now, f)).run(Delivery.InPort("t1", "LOC01"))

    s shouldBe a [Delivery.InPort]

    e shouldBe empty
  }

  it should "not transit when inPort state load if voyageNo is not found in itinerary" in {
    val f: FindRoute = _ => Some(Itinerary(List(
      Leg("V01", LocationTime("LOC01", nextDay(1)), LocationTime("LOC02", nextDay(2))),
      Leg("V02", LocationTime("LOC03", nextDay(3)), LocationTime("LOC04", nextDay(4))),
    )))

    val (s, e) = Delivery.action(Load("V21", now, f)).run(Delivery.InPort("t1", "LOC01"))

    s shouldBe a [Delivery.InPort]

    e shouldBe empty
  }

  it should "not transit when other states load" in {
    val states = List(
      Delivery.NotReceived("t1"),
      Delivery.OnBoardCarrier("t1", "0101S", "USNYC"),
      Delivery.Claimed("t1", "USNYC", now)
    )

    val f: FindRoute = _ => Some(Itinerary(List(
      Leg("V01", LocationTime("LOC01", nextDay(1)), LocationTime("LOC02", nextDay(2))),
      Leg("V02", LocationTime("LOC03", nextDay(3)), LocationTime("LOC04", nextDay(4))),
    )))

    states.foreach { c =>
      val (s, e) = Delivery.action(Load("V01", now, f)).run(c)

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

  it should "be not misdirected if notReceived is current state" in {
    val f: FindRoute = t => {
      t shouldBe "t1"

      Some(Itinerary(List(
        Leg("V01", LocationTime("LOC01", nextDay(1)), LocationTime("LOC02", nextDay(2))),
        Leg("V02", LocationTime("LOC02", nextDay(3)), LocationTime("LOC03", nextDay(4))),
      )))
    }

    val (_, r) = Delivery.action(IsMisdirected(f)).run(Delivery.NotReceived("t1"))

    r shouldBe false
  }

  it should "be not misdirected if inPort location contained in itinerary" in {
    val f: FindRoute = t => {
      t shouldBe "t1"

      Some(Itinerary(List(
        Leg("V01", LocationTime("LOC01", nextDay(1)), LocationTime("LOC02", nextDay(2))),
        Leg("V02", LocationTime("LOC02", nextDay(3)), LocationTime("LOC03", nextDay(4))),
      )))
    }

    List("LOC01", "LOC02", "LOC03").foreach { l =>
      val (_, r) = Delivery.action(IsMisdirected(f)).run(Delivery.InPort("t1", l))

      r shouldBe false
    }
  }

  it should "be misdirected if inPort location did not contain in itinerary" in {
    val f: FindRoute = t => {
      t shouldBe "t1"

      Some(Itinerary(List(
        Leg("V01", LocationTime("LOC01", nextDay(1)), LocationTime("LOC02", nextDay(2))),
        Leg("V02", LocationTime("LOC02", nextDay(3)), LocationTime("LOC03", nextDay(4))),
      )))
    }

    val (_, r) = Delivery.action(IsMisdirected(f)).run(Delivery.InPort("t1", "LOC99"))

    r shouldBe true
  }

  it should "be misdirected if inPort state and none itinerary" in {
    val f: FindRoute = _ => None

    val (_, r) = Delivery.action(IsMisdirected(f)).run(Delivery.InPort("t1", "LOC01"))

    r shouldBe true
  }

  it should "be not misdirected if onBoardCarrier location contained in itinerary's target load location" in {
    val f: FindRoute = t => {
      t shouldBe "t1"

      Some(Itinerary(List(
        Leg("V01", LocationTime("LOC01", nextDay(1)), LocationTime("LOC02", nextDay(2))),
        Leg("V02", LocationTime("LOC02", nextDay(3)), LocationTime("LOC03", nextDay(4))),
      )))
    }

    List(("V01", "LOC01"), ("V02", "LOC02")).foreach { (v, l) =>
      val (_, r) = Delivery.action(IsMisdirected(f)).run(Delivery.OnBoardCarrier("t1", v, l))

      r shouldBe false
    }
  }

  it should "be misdirected if onBoardCarrier location did not contain in itinerary's target load location" in {
    val f: FindRoute = t => {
      t shouldBe "t1"

      Some(Itinerary(List(
        Leg("V01", LocationTime("LOC01", nextDay(1)), LocationTime("LOC02", nextDay(2))),
        Leg("V02", LocationTime("LOC02", nextDay(3)), LocationTime("LOC03", nextDay(4))),
      )))
    }

    List(("V01", "LOC02"), ("V02", "LOC03")).foreach { (v, l) =>
      val (_, r) = Delivery.action(IsMisdirected(f)).run(Delivery.OnBoardCarrier("t1", v, l))

      r shouldBe true
    }
  }

  it should "be not misdirected if claimed location is equal itinerary's last unload location" in {
    val f: FindRoute = t => {
      t shouldBe "t1"

      Some(Itinerary(List(
        Leg("V01", LocationTime("LOC01", nextDay(1)), LocationTime("LOC02", nextDay(2))),
        Leg("V02", LocationTime("LOC02", nextDay(3)), LocationTime("LOC03", nextDay(4))),
      )))
    }

    val (_, r) = Delivery.action(IsMisdirected(f)).run(Delivery.Claimed("t1", "LOC03", now))

    r shouldBe false
  }

  it should "be misdirected if claimed location is not equal itinerary's last unload location" in {
    val f: FindRoute = t => {
      t shouldBe "t1"

      Some(Itinerary(List(
        Leg("V01", LocationTime("LOC01", nextDay(1)), LocationTime("LOC02", nextDay(2))),
        Leg("V02", LocationTime("LOC02", nextDay(3)), LocationTime("LOC03", nextDay(4))),
      )))
    }

    val (_, r) = Delivery.action(IsMisdirected(f)).run(Delivery.Claimed("t1", "LOC02", now))

    r shouldBe true
  }

  it should "be misdirected if itinerary is empty (exclude notReceived)" in {
    val f: FindRoute = _ => Some(Itinerary(List.empty))

    val states = List(
      Delivery.InPort("t1", "LOC01"),
      Delivery.OnBoardCarrier("t1", "V01", "LOC01"),
      Delivery.Claimed("t1", "LOC02", now)
    )

    states.foreach { c =>
      val (s, r) = Delivery.action(IsMisdirected(f)).run(c)

      s shouldBe c
      r shouldBe true
    }
  }

  it should "be arrived if inPort location is equal destination" in {
    val f: FindRouteAndSpec = t => {
      t shouldBe "t1"

      Some(
        (
          Itinerary(List(
            Leg("V01", LocationTime("LOC01", nextDay(1)), LocationTime("LOC02", nextDay(2))),
            Leg("V02", LocationTime("LOC02", nextDay(3)), LocationTime("LOC03", nextDay(4))),
          )),
          RouteSpecification("LOC01", "LOC03", nextDay(5))
        )
      )
    }

    val (_, r) = Delivery.action(IsUnloadedAtDestination(f)).run(Delivery.InPort("t1", "LOC03"))

    r shouldBe true
  }

  it should "be arrived if inPort location is equal itinerary's last unload location" in {
    val f: FindRouteAndSpec = t => {
      t shouldBe "t1"

      Some(
        (
          Itinerary(List(
            Leg("V01", LocationTime("LOC01", nextDay(1)), LocationTime("LOC02", nextDay(2))),
            Leg("V02", LocationTime("LOC02", nextDay(3)), LocationTime("LOC03", nextDay(4))),
          )),
          RouteSpecification("LOC01", "LOC99", nextDay(5))
        )
      )
    }

    val (_, r) = Delivery.action(IsUnloadedAtDestination(f)).run(Delivery.InPort("t1", "LOC03"))

    r shouldBe true
  }

  it should "be not arrived if inPort location is not equal destination" in {
    val f: FindRouteAndSpec = t => {
      t shouldBe "t1"

      Some(
        (
          Itinerary(List(
            Leg("V01", LocationTime("LOC01", nextDay(1)), LocationTime("LOC02", nextDay(2))),
            Leg("V02", LocationTime("LOC02", nextDay(3)), LocationTime("LOC03", nextDay(4))),
          )),
          RouteSpecification("LOC01", "LOC03", nextDay(5))
        )
      )
    }

    val (_, r) = Delivery.action(IsUnloadedAtDestination(f)).run(Delivery.InPort("t1", "LOC02"))

    r shouldBe false
  }