package models

import org.scalatest._
import flatspec._
import matchers._

import java.time.OffsetDateTime

@unchecked
class CargoSpec extends AnyFlatSpec with should.Matchers:
  import Cargo.*
  import CommandA.*

  def nextDays(n: Long): OffsetDateTime = OffsetDateTime.now().plusDays(n)

  val testRouteSpec = RouteSpec("USNYC", "JNTKO", nextDays(10))

  val testItinerary = Itinerary(List(
    Leg("0100S", LocationTime("USNYC", nextDays(1)), LocationTime("CNSHA", nextDays(4))),
    Leg("0200A", LocationTime("CNSHA", nextDays(5)), LocationTime("JNTKO", nextDays(7)))
  ))

  val testItinerary2 = Itinerary(List(
    Leg("0100S", LocationTime("USNYC", nextDays(1)), LocationTime("AUMEL", nextDays(4)))
  ))

  it should "transit to unrouted when created empty cargo" in {
    val Right((s, e)) = Cargo.action(Create("t1", testRouteSpec)).run(emptyCargo)

    s shouldBe a [Cargo.Unrouted]
    e shouldBe a [Event.Created]
  }

  it should "be error when created empty cargo with blank trackingId" in {
    val Left(err) = Cargo.action(Create("  ", testRouteSpec)).run(emptyCargo)

    err shouldBe a [CommandError.BlankId]
  }

  it should "be error when created empty cargo with past deadline" in {
    val Left(err) = Cargo.action(Create("t1", testRouteSpec.copy(deadline = nextDays(-1)))).run(emptyCargo)

    err shouldBe a [CommandError.PastDeadline]
  }

  it should "be error when created non-empty cargo" in {
    val states = List(
      Cargo.Unrouted("t1", testRouteSpec),
      Cargo.Routed("t1", testRouteSpec, testItinerary),
      Cargo.Closed("t1", testRouteSpec, testItinerary)
    )

    states.foreach { c =>
      val Left(err) = Cargo.action(Create("t1", testRouteSpec.copy(destination = "CNSHA"))).run(c)

      err shouldBe a [CommandError.InvalidState]
    }
  }

  it should "transit to routed when assigned valid route to unrouted cargo" in {
    val state = Cargo.Unrouted("t1", testRouteSpec)

    val Right((s, e)) = Cargo.action(AssignRoute(testItinerary)).run(state)

    s shouldBe a [Cargo.Routed]
    e shouldBe a [Event.AssignedRoute]
  }

  it should "transit to misrouted when assigned invalid destination route to unrouted cargo" in {
    val state = Cargo.Unrouted("t1", testRouteSpec)

    val Right((s, e)) = Cargo.action(AssignRoute(testItinerary2)).run(state)

    s shouldBe a [Cargo.Misrouted]
    e shouldBe a [Event.AssignedRoute]
  }

  it should "transit to misrouted when assigned short deadline route to unrouted cargo" in {
    val state = Cargo.Unrouted("t1", testRouteSpec.copy(deadline = nextDays(3)))

    val Right((s, e)) = Cargo.action(AssignRoute(testItinerary)).run(state)

    s shouldBe a [Cargo.Misrouted]
    e shouldBe a [Event.AssignedRoute]
  }

  it should "transit to misrouted when assigned invalid route to routed cargo" in {
    val state = Cargo.Routed("t1", testRouteSpec, testItinerary)

    val Right((s, e)) = Cargo.action(AssignRoute(testItinerary2)).run(state)

    s shouldBe a [Cargo.Misrouted]
    e shouldBe a [Event.AssignedRoute]
  }

  it should "transit to routed when assigned valid route to misrouted cargo" in {
    val state = Cargo.Misrouted("t1", testRouteSpec, testItinerary2)

    val Right((s, e)) = Cargo.action(AssignRoute(testItinerary)).run(state)

    s shouldBe a [Cargo.Routed]
    e shouldBe a [Event.AssignedRoute]
  }

  it should "be error when assigned same route to routed cargo" in {
    val state = Cargo.Routed("t1", testRouteSpec, testItinerary)

    val Left(err) = Cargo.action(AssignRoute(Itinerary(testItinerary.legs))).run(state)

    err shouldBe a [CommandError.NoChange]
  }

  it should "be error when closed non-routed cargo" in {
    val states = List(
      Cargo.Empty(),
      Cargo.Unrouted("t1", testRouteSpec),
      Cargo.Closed("t1", testRouteSpec, testItinerary)
    )

    states.foreach { c =>
      val Left(err) = Cargo.action(Close()).run(c)

      err shouldBe a [CommandError.InvalidState]
    }
  }

  it should "transit to closed when closed routed cargo" in {
    val states = List(
      Cargo.Routed("t1", testRouteSpec, testItinerary),
      Cargo.Misrouted("t1", testRouteSpec, testItinerary2)
    )

    states.foreach { c =>
      val Right((s, e)) = Cargo.action(Close()).run(c)

      s shouldBe a [Cargo.Closed]
      e shouldBe a [Event.Closed]
    }
  }

  it should "update when changed unrouted cargo to other destination" in {
    val state = Cargo.Unrouted("t1", testRouteSpec)

    val Right((s, e)) = Cargo.action(ChangeDestination("AUMEL")).run(state)

    s shouldBe a [Cargo.Unrouted]
    e shouldBe a [Event.ChangedDestination]
  }

  it should "transit to routed when changed misrouted cargo to valid destination" in {
    val state = Cargo.Misrouted("t1", testRouteSpec, testItinerary2)

    val Right((s, e)) = Cargo.action(ChangeDestination("AUMEL")).run(state)

    s shouldBe a [Cargo.Routed]
    e shouldBe a [Event.ChangedDestination]
  }

  it should "transit to misrouted when changed routed cargo to invalid destination" in {
    val state = Cargo.Routed("t1", testRouteSpec, testItinerary)

    val Right((s, e)) = Cargo.action(ChangeDestination("AUMEL")).run(state)

    s shouldBe a [Cargo.Misrouted]
    e shouldBe a [Event.ChangedDestination]
  }

  it should "update when changed misrouted cargo to other invalid destination" in {
    val state = Cargo.Misrouted("t1", testRouteSpec, testItinerary2)

    val Right((s, e)) = Cargo.action(ChangeDestination("FIHEL")).run(state)

    s shouldBe a [Cargo.Misrouted]
    e shouldBe a [Event.ChangedDestination]
  }

  it should "be error when changed unrouted cargo to same destination" in {
    val state = Cargo.Unrouted("t1", testRouteSpec)

    val Left(err) = Cargo.action(ChangeDestination(state.routeSpec.destination)).run(state)

    err shouldBe a [CommandError.NoChange]
  }

  it should "be error when changed routed cargo to same destination" in {
    val state = Cargo.Routed("t1", testRouteSpec, testItinerary)

    val Left(err) = Cargo.action(ChangeDestination(state.routeSpec.destination)).run(state)

    err shouldBe a [CommandError.NoChange]
  }

  it should "be error when changed closed cargo to other destination" in {
    val state = Cargo.Closed("t1", testRouteSpec, testItinerary)

    val Left(err) = Cargo.action(ChangeDestination("AUMEL")).run(state)

    err shouldBe a [CommandError.InvalidState]
  }

  it should "update when changed unrouted cargo to valid deadline" in {
    val state = Cargo.Unrouted("t1", testRouteSpec)

    val Right((s, e)) = Cargo.action(ChangeDeadline(nextDays(15))).run(state)

    s shouldBe a [Cargo.Unrouted]
    e shouldBe a [Event.ChangedDeadline]
  }

  it should "update when changed routed cargo to valid deadline" in {
    val state = Cargo.Routed("t1", testRouteSpec, testItinerary)

    val Right((s, e)) = Cargo.action(ChangeDeadline(nextDays(15))).run(state)

    s shouldBe a [Cargo.Routed]
    e shouldBe a [Event.ChangedDeadline]
  }

  it should "transit to misrouted when changed routed cargo to short deadline" in {
    val state = Cargo.Routed("t1", testRouteSpec, testItinerary)

    val Right((s, e)) = Cargo.action(ChangeDeadline(nextDays(3))).run(state)

    s shouldBe a [Cargo.Misrouted]
    e shouldBe a [Event.ChangedDeadline]
  }

  it should "transit to routed when changed short deadline misrouted cargo to valid deadline" in {
    val state = Cargo.Misrouted("t1", testRouteSpec.copy(deadline = nextDays(3)), testItinerary)

    val Right((s, e)) = Cargo.action(ChangeDeadline(nextDays(15))).run(state)

    s shouldBe a [Cargo.Routed]
    e shouldBe a [Event.ChangedDeadline]
  }

  it should "be error when changed unrouted cargo to past deadline" in {
    val state = Cargo.Unrouted("t1", testRouteSpec)

    val Left(err) = Cargo.action(ChangeDeadline(nextDays(-1))).run(state)

    err shouldBe a [CommandError.PastDeadline]
  }

  it should "be error when changed routed cargo to past deadline" in {
    val state = Cargo.Routed("t1", testRouteSpec, testItinerary)

    val Left(err) = Cargo.action(ChangeDeadline(nextDays(-1))).run(state)

    err shouldBe a [CommandError.PastDeadline]
  }

  it should "be error when changed unrouted cargo to same deadline" in {
    val state = Cargo.Unrouted("t1", testRouteSpec)
    val d = OffsetDateTime.parse(state.routeSpec.deadline.toString)

    val Left(err) = Cargo.action(ChangeDeadline(d)).run(state)

    err shouldBe a [CommandError.NoChange]
  }

  it should "be error when changed routed cargo to same deadline" in {
    val state = Cargo.Routed("t1", testRouteSpec, testItinerary)
    val d = OffsetDateTime.parse(state.routeSpec.deadline.toString)

    val Left(err) = Cargo.action(ChangeDeadline(d)).run(state)

    err shouldBe a [CommandError.NoChange]
  }

  it should "be error when changed closed cargo to other deadline" in {
    val state = Cargo.Closed("t1", testRouteSpec, testItinerary)

    val Left(err) = Cargo.action(ChangeDeadline(nextDays(20))).run(state)

    err shouldBe a [CommandError.InvalidState]
  }

  it should "IsDestination is true if location is equals RouteSpec's destination" in {
    val states = List(
      Cargo.Unrouted("t1", testRouteSpec),
      Cargo.Routed("t1", testRouteSpec, testItinerary),
      Cargo.Misrouted("t1", testRouteSpec, testItinerary2),
      Cargo.Closed("t1", testRouteSpec, testItinerary)
    )

    states.foreach { s =>
      val Right((_, r)) = Cargo.action(IsDestination("JNTKO")).run(s)

      r shouldBe true
    }
  }

  it should "IsDestination is false if location is not equals RouteSpec's destination" in {
    val states = List(
      Cargo.Unrouted("t1", testRouteSpec),
      Cargo.Routed("t1", testRouteSpec, testItinerary),
      Cargo.Misrouted("t1", testRouteSpec, testItinerary2),
      Cargo.Closed("t1", testRouteSpec, testItinerary)
    )

    states.foreach { s =>
      val Right((_, r)) = Cargo.action(IsDestination("CNSHA")).run(s)

      r shouldBe false
    }
  }

  it should "IsDestination is false if state is empty" in {
    val Right((_, r)) = Cargo.action(IsDestination("AUMEL")).run(Cargo.emptyCargo)

    r shouldBe false
  }

  it should "IsDestination is true if location is equals itinerary's last location" in {
    val r = RouteSpec("USNYC", "AUMEL", nextDays(10))

    val states = List(
      Cargo.Misrouted("t1", r, testItinerary),
      Cargo.Closed("t1", r, testItinerary)
    )

    states.foreach { s =>
      val Right((_, r)) = Cargo.action(IsDestination("JNTKO")).run(s)

      r shouldBe true
    }
  }

  it should "IsOnRoute is none if state is not progressing" in {
    val states = List(
      Cargo.Empty(),
      Cargo.Unrouted("t1", testRouteSpec),
      Cargo.Closed("t1", testRouteSpec, testItinerary)
    )

    states.foreach { s =>
      val Right((_, r)) = Cargo.action(IsOnRoute("JNTKO", None)).run(s)

      r shouldBe None
    }
  }

  it should "IsOnRoute is true if state is progressing and itinerary contains location" in {
    val states = List(
      Cargo.Routed("t1", testRouteSpec, testItinerary),
      Cargo.Misrouted("t1", testRouteSpec.copy(destination = "AUMEL"), testItinerary)
    )

    states.foreach { s =>
      List("USNYC", "CNSHA", "JNTKO").foreach { l =>
        val Right((_, r)) = Cargo.action(IsOnRoute(l, None)).run(s)

        r shouldBe Some(true)
      }
    }
  }

  it should "IsOnRoute is false if state is progressing and itinerary does not contain location" in {
    val states = List(
      Cargo.Routed("t1", testRouteSpec, testItinerary),
      Cargo.Misrouted("t1", testRouteSpec.copy(destination = "AUMEL"), testItinerary)
    )

    states.foreach { s =>
      val Right((_, r)) = Cargo.action(IsOnRoute("AUMEL", None)).run(s)

      r shouldBe Some(false)
    }
  }

  it should "IsOnRoute is true if state is progressing and itinerary contains target voyage's location" in {
    val states = List(
      Cargo.Routed("t1", testRouteSpec, testItinerary),
      Cargo.Misrouted("t1", testRouteSpec.copy(destination = "AUMEL"), testItinerary)
    )

    states.foreach { s =>
      List(("0100S", "USNYC"), ("0200A", "CNSHA")).foreach { (v, l) =>
        val Right((_, r)) = Cargo.action(IsOnRoute(l, Some(v))).run(s)

        r shouldBe Some(true)
      }
    }
  }

  it should "IsOnRoute is false if state is progressing and itinerary does not contain target voyage's location" in {
    val states = List(
      Cargo.Routed("t1", testRouteSpec, testItinerary),
      Cargo.Misrouted("t1", testRouteSpec.copy(destination = "AUMEL"), testItinerary)
    )

    states.foreach { s =>
      List(("0100S", "JNTKO"), ("0200A", "USNYC"), ("0999B", "AUMEL")).foreach { (v, l) =>
        val Right((_, r)) = Cargo.action(IsOnRoute(l, Some(v))).run(s)

        r shouldBe Some(false)
      }
    }
  }

