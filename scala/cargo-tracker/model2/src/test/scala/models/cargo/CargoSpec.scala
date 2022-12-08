package models.cargo

import org.scalatest._
import flatspec._
import matchers._

import java.time.LocalDateTime

class CargoSpec extends AnyFlatSpec with should.Matchers:
  import Cargo.*
  import CommandA.*

  def nextDays(n: Long): LocalDateTime = LocalDateTime.now().plusDays(n)

  val testRouteSpec = RouteSpecification("USNYC", "JNTKO", nextDays(10))

  val testItinerary = Itinerary(List(
    Leg("0100S", LocationTime("USNYC", nextDays(1)), LocationTime("CNSHA", nextDays(4))),
    Leg("0200A", LocationTime("CNSHA", nextDays(5)), LocationTime("JNTKO", nextDays(7)))
  ))

  val testItinerary2 = Itinerary(List(
    Leg("0100S", LocationTime("USNYC", nextDays(1)), LocationTime("AUMEL", nextDays(4)))
  ))

  it should "move to unrouted when created empty cargo" in {
    val (s, e) = Cargo.action(Create("t1", testRouteSpec)).run(emptyCargo)

    s shouldBe a [Cargo.Unrouted]

    e should have size 1
    e.head shouldBe a [Event.Created]
  }

  it should "not move when created empty cargo with blank trackingId" in {
    val (s, e) = Cargo.action(Create("  ", testRouteSpec)).run(emptyCargo)

    s shouldBe a[Cargo.Empty]
    e shouldBe empty
  }

  it should "not move when created empty cargo with past deadline" in {
    val (s, e) = Cargo.action(Create("t1", testRouteSpec.copy(deadline = nextDays(-1)))).run(emptyCargo)

    s shouldBe a[Cargo.Empty]
    e shouldBe empty
  }

  it should "not move when created non-empty cargo" in {
    val states = List(
      Cargo.Unrouted("t1", testRouteSpec),
      Cargo.Routed("t1", testRouteSpec, testItinerary),
      Cargo.Closed("t1", testRouteSpec, testItinerary)
    )

    states.foreach { c =>
      val (s, e) = Cargo.action(Create("t1", testRouteSpec.copy(destination = "CNSHA"))).run(c)

      s shouldBe c
      e shouldBe empty
    }
  }

  it should "move to routed when assigned valid route to unrouted cargo" in {
    val state = Cargo.Unrouted("t1", testRouteSpec)

    val (s, e) = Cargo.action(AssignRoute(testItinerary)).run(state)

    s shouldBe a[Cargo.Routed]

    e should have size 1
    e.head shouldBe a[Event.AssignedRoute]
  }

  it should "move to misrouted when assigned invalid destination route to unrouted cargo" in {
    val state = Cargo.Unrouted("t1", testRouteSpec)

    val (s, e) = Cargo.action(AssignRoute(testItinerary2)).run(state)

    s shouldBe a[Cargo.Misrouted]

    e should have size 1
    e.head shouldBe a[Event.AssignedRoute]
  }

  it should "move to misrouted when assigned short deadline route to unrouted cargo" in {
    val state = Cargo.Unrouted("t1", testRouteSpec.copy(deadline = nextDays(3)))

    val (s, e) = Cargo.action(AssignRoute(testItinerary)).run(state)

    s shouldBe a[Cargo.Misrouted]

    e should have size 1
    e.head shouldBe a[Event.AssignedRoute]
  }

  it should "move to misrouted when assigned invalid route to routed cargo" in {
    val state = Cargo.Routed("t1", testRouteSpec, testItinerary)

    val (s, e) = Cargo.action(AssignRoute(testItinerary2)).run(state)

    s shouldBe a[Cargo.Misrouted]

    e should have size 1
    e.head shouldBe a[Event.AssignedRoute]
  }

  it should "move to routed when assigned valid route to misrouted cargo" in {
    val state = Cargo.Misrouted("t1", testRouteSpec, testItinerary2)

    val (s, e) = Cargo.action(AssignRoute(testItinerary)).run(state)

    s shouldBe a[Cargo.Routed]

    e should have size 1
    e.head shouldBe a[Event.AssignedRoute]
  }

  it should "not update when assigned same route to routed cargo" in {
    val state = Cargo.Routed("t1", testRouteSpec, testItinerary)

    val (s, e) = Cargo.action(AssignRoute(Itinerary(testItinerary.legs))).run(state)

    s shouldBe a[Cargo.Routed]

    e shouldBe empty
  }

  it should "not move when closed non-routed cargo" in {
    val states = List(
      Cargo.Empty(),
      Cargo.Unrouted("t1", testRouteSpec),
      Cargo.Closed("t1", testRouteSpec, testItinerary)
    )

    states.foreach { c =>
      val (s, e) = Cargo.action(Close()).run(c)

      s shouldBe c
      e shouldBe empty
    }
  }

  it should "move to closed when closed routed cargo" in {
    val states = List(
      Cargo.Routed("t1", testRouteSpec, testItinerary),
      Cargo.Misrouted("t1", testRouteSpec, testItinerary2)
    )

    states.foreach { c =>
      val (s, e) = Cargo.action(Close()).run(c)

      s shouldBe a [Cargo.Closed]

      e should have size 1
      e.head shouldBe a[Event.Closed]
    }
  }

  it should "update when changed unrouted cargo to other destination" in {
    val state = Cargo.Unrouted("t1", testRouteSpec)

    val (s, e) = Cargo.action(ChangeDestination("AUMEL")).run(state)

    s shouldBe a[Cargo.Unrouted]

    e should have size 1
    e.head shouldBe a[Event.ChangedDestination]
  }

  it should "move to routed when changed misrouted cargo to valid destination" in {
    val state = Cargo.Misrouted("t1", testRouteSpec, testItinerary2)

    val (s, e) = Cargo.action(ChangeDestination("AUMEL")).run(state)

    s shouldBe a[Cargo.Routed]

    e should have size 1
    e.head shouldBe a[Event.ChangedDestination]
  }

  it should "move to misrouted when changed routed cargo to invalid destination" in {
    val state = Cargo.Routed("t1", testRouteSpec, testItinerary)

    val (s, e) = Cargo.action(ChangeDestination("AUMEL")).run(state)

    s shouldBe a[Cargo.Misrouted]

    e should have size 1
    e.head shouldBe a[Event.ChangedDestination]
  }

  it should "update when changed misrouted cargo to other invalid destination" in {
    val state = Cargo.Misrouted("t1", testRouteSpec, testItinerary2)

    val (s, e) = Cargo.action(ChangeDestination("FIHEL")).run(state)

    s shouldBe a[Cargo.Misrouted]

    e should have size 1
    e.head shouldBe a[Event.ChangedDestination]
  }

  it should "not update when changed unrouted cargo to same destination" in {
    val state = Cargo.Unrouted("t1", testRouteSpec)

    val (s, e) = Cargo.action(ChangeDestination(state.routeSpec.destination)).run(state)

    s shouldBe a[Cargo.Unrouted]

    e shouldBe empty
  }

  it should "not update when changed routed cargo to same destination" in {
    val state = Cargo.Routed("t1", testRouteSpec, testItinerary)

    val (s, e) = Cargo.action(ChangeDestination(state.routeSpec.destination)).run(state)

    s shouldBe a[Cargo.Routed]

    e shouldBe empty
  }

  it should "not update when changed closed cargo to other destination" in {
    val state = Cargo.Closed("t1", testRouteSpec, testItinerary)

    val (s, e) = Cargo.action(ChangeDestination("AUMEL")).run(state)

    s shouldBe a[Cargo.Closed]

    e shouldBe empty
  }

  it should "update when changed unrouted cargo to valid deadline" in {
    val state = Cargo.Unrouted("t1", testRouteSpec)

    val (s, e) = Cargo.action(ChangeDeadline(nextDays(15))).run(state)

    s shouldBe a[Cargo.Unrouted]

    e should have size 1
    e.head shouldBe a[Event.ChangedDeadline]
  }

  it should "update when changed routed cargo to valid deadline" in {
    val state = Cargo.Routed("t1", testRouteSpec, testItinerary)

    val (s, e) = Cargo.action(ChangeDeadline(nextDays(15))).run(state)

    s shouldBe a[Cargo.Routed]

    e should have size 1
    e.head shouldBe a[Event.ChangedDeadline]
  }

  it should "move to misrouted when changed routed cargo to short deadline" in {
    val state = Cargo.Routed("t1", testRouteSpec, testItinerary)

    val (s, e) = Cargo.action(ChangeDeadline(nextDays(3))).run(state)

    s shouldBe a[Cargo.Misrouted]

    e should have size 1
    e.head shouldBe a[Event.ChangedDeadline]
  }

  it should "move to routed when changed short deadline misrouted cargo to valid deadline" in {
    val state = Cargo.Misrouted("t1", testRouteSpec.copy(deadline = nextDays(3)), testItinerary)

    val (s, e) = Cargo.action(ChangeDeadline(nextDays(15))).run(state)

    s shouldBe a[Cargo.Routed]

    e should have size 1
    e.head shouldBe a[Event.ChangedDeadline]
  }

  it should "not update when changed unrouted cargo to past deadline" in {
    val state = Cargo.Unrouted("t1", testRouteSpec)

    val (s, e) = Cargo.action(ChangeDeadline(nextDays(-1))).run(state)

    s shouldBe a[Cargo.Unrouted]

    e shouldBe empty
  }

  it should "not update when changed routed cargo to past deadline" in {
    val state = Cargo.Routed("t1", testRouteSpec, testItinerary)

    val (s, e) = Cargo.action(ChangeDeadline(nextDays(-1))).run(state)

    s shouldBe a[Cargo.Routed]

    e shouldBe empty
  }

  it should "not update when changed unrouted cargo to same deadline" in {
    val state = Cargo.Unrouted("t1", testRouteSpec)
    val d = LocalDateTime.parse(state.routeSpec.deadline.toString)

    val (s, e) = Cargo.action(ChangeDeadline(d)).run(state)

    s shouldBe a[Cargo.Unrouted]

    e shouldBe empty
  }

  it should "not update when changed routed cargo to same deadline" in {
    val state = Cargo.Routed("t1", testRouteSpec, testItinerary)
    val d = LocalDateTime.parse(state.routeSpec.deadline.toString)

    val (s, e) = Cargo.action(ChangeDeadline(d)).run(state)

    s shouldBe a[Cargo.Routed]

    e shouldBe empty
  }

  it should "not update when changed closed cargo to other deadline" in {
    val state = Cargo.Closed("t1", testRouteSpec, testItinerary)

    val (s, e) = Cargo.action(ChangeDeadline(nextDays(20))).run(state)

    s shouldBe a[Cargo.Closed]

    e shouldBe empty
  }

  it should "IsDestination is true if location is equals RouteSpec's destination" in {
    val states = List(
      Cargo.Unrouted("t1", testRouteSpec),
      Cargo.Routed("t1", testRouteSpec, testItinerary),
      Cargo.Misrouted("t1", testRouteSpec, testItinerary2),
      Cargo.Closed("t1", testRouteSpec, testItinerary)
    )

    states.foreach { s =>
      val (_, r) = Cargo.action(IsDestination("JNTKO")).run(s)

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
      val (_, r) = Cargo.action(IsDestination("CNSHA")).run(s)

      r shouldBe false
    }
  }

  it should "IsDestination is false if state is empty" in {
    val (_, r) = Cargo.action(IsDestination("AUMEL")).run(Cargo.emptyCargo)

    r shouldBe false
  }

  it should "IsDestination is true if location is equals itinerary's last location" in {
    val r = RouteSpecification("USNYC", "AUMEL", nextDays(10))

    val states = List(
      Cargo.Misrouted("t1", r, testItinerary),
      Cargo.Closed("t1", r, testItinerary)
    )

    states.foreach { s =>
      val (_, r) = Cargo.action(IsDestination("JNTKO")).run(s)

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
      val (_, r) = Cargo.action(IsOnRoute("JNTKO", None)).run(s)

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
        val (_, r) = Cargo.action(IsOnRoute(l, None)).run(s)

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
      val (_, r) = Cargo.action(IsOnRoute("AUMEL", None)).run(s)

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
        val (_, r) = Cargo.action(IsOnRoute(l, Some(v))).run(s)

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
        val (_, r) = Cargo.action(IsOnRoute(l, Some(v))).run(s)

        r shouldBe Some(false)
      }
    }
  }

